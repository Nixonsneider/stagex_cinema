package com.stagex.stagex_backend.booking.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stagex.stagex_backend.booking.application.BookingViews.Hold;
import com.stagex.stagex_backend.booking.application.BookingViews.ScreeningSeats;
import com.stagex.stagex_backend.booking.application.BookingViews.Seat;
import com.stagex.stagex_backend.booking.application.BookingViews.SeatStatus;
import com.stagex.stagex_backend.booking.repository.BookingRepository;
import com.stagex.stagex_backend.booking.repository.BookingRepository.OrderRow;
import com.stagex.stagex_backend.booking.repository.BookingRepository.ScreeningRow;
import com.stagex.stagex_backend.booking.repository.BookingRepository.SeatRow;
import com.stagex.stagex_backend.payment.domain.PaymentGateway;
import com.stagex.stagex_backend.payment.domain.PaymentRequest;
import com.stagex.stagex_backend.payment.domain.PaymentSession;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Disponibilidad y reserva temporal de asientos (T-040, T-041 y T-042).
 *
 * <p>La reserva crea la orden {@code PENDING} con su total calculado en el backend, bloquea los
 * asientos ({@code HELD}) durante {@link BookingProperties#holdTtl()} y abre la sesión de pago.
 * Las filas se bloquean con {@code SELECT ... FOR UPDATE}: si dos clientes piden el mismo asiento
 * a la vez, el segundo espera y luego recibe {@code 409 SEAT_NOT_AVAILABLE}.
 */
@Service
public class BookingService {

	private static final Logger log = LoggerFactory.getLogger(BookingService.class);

	static final String SCREENING_NOT_FOUND = "Esta función ya no está disponible.";
	static final String HOLD_NOT_FOUND = "No encontramos esta reserva.";

	private final BookingRepository repository;
	private final PaymentGateway payments;
	private final BookingProperties properties;
	private final Clock clock;

	public BookingService(BookingRepository repository, PaymentGateway payments, BookingProperties properties,
			Clock clock) {
		this.repository = repository;
		this.payments = payments;
		this.properties = properties;
		this.clock = clock;
	}

	/** T-040: mapa de la función con el estado vigente de cada asiento. */
	@Transactional(readOnly = true)
	public ScreeningSeats getSeatMap(UUID screeningId) {
		Instant now = clock.instant();
		ScreeningRow screening = bookableScreening(screeningId, now);
		List<Seat> seats = repository.findSeats(screeningId).stream()
				.map(row -> new Seat(row.id(), row.row(), row.number(), row.seatType(), statusOf(row, now)))
				.toList();
		return new ScreeningSeats(screening.id(), screening.movieId(), screening.movieTitle(), screening.startsAt(),
				screening.roomName(), screening.price(), screening.currency(), properties.maxSeatsPerOrder(),
				properties.holdTtl().toMinutes(), seats);
	}

	/**
	 * T-041: bloquea los asientos pedidos y crea la orden pendiente.
	 *
	 * @param userId     cliente autenticado; nulo si compra como invitado
	 * @param guestEmail correo del invitado; obligatorio solo si {@code userId} es nulo
	 */
	@Transactional
	public Hold createHold(UUID screeningId, List<UUID> screeningSeatIds, UUID userId, String guestEmail) {
		validateSeatSelection(screeningSeatIds);
		String buyerEmail = userId == null ? requireGuestEmail(guestEmail) : null;
		Instant now = clock.instant();
		ScreeningRow screening = bookableScreening(screeningId, now);

		List<SeatRow> locked = repository.lockSeats(screeningId, screeningSeatIds);
		if (locked.size() != screeningSeatIds.size()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
					"Uno o más asientos no pertenecen a esta función.");
		}
		if (locked.stream().anyMatch(seat -> !seat.isFree(now))) {
			throw new ApiException(HttpStatus.CONFLICT, "SEAT_NOT_AVAILABLE",
					"Uno o más asientos ya no están disponibles. Elige otros asientos.");
		}

		// Un bloqueo vencido que el job aún no liberó deja su orden anterior como vencida.
		repository.expirePendingOrdersHolding(screeningSeatIds, now);

		UUID orderId = UUID.randomUUID();
		BigDecimal total = screening.price().multiply(BigDecimal.valueOf(screeningSeatIds.size()));
		Instant expiresAt = now.plus(properties.holdTtl());
		repository.insertOrder(orderId, userId, buyerEmail, screeningId, total, screening.currency(), now);
		for (UUID seatId : screeningSeatIds) {
			repository.insertItem(orderId, seatId, screening.price());
		}
		repository.holdSeats(screeningSeatIds, expiresAt);

		String paymentUrl = null;
		if (total.signum() > 0) {
			PaymentSession session = payments.createSession(new PaymentRequest(orderId, total, screening.currency()));
			repository.setPaymentReference(orderId, session.providerReference(), now);
			paymentUrl = session.paymentUrl().toString();
		}
		log.info("Reserva {} creada con {} asientos para la función {}", orderId, screeningSeatIds.size(), screeningId);
		return new Hold(orderId, orderId, expiresAt, total, screening.currency(), paymentUrl);
	}

	/**
	 * Libera una reserva pendiente. Si la orden es de un cliente registrado, solo él puede
	 * liberarla; a cualquier otro se le responde 404 para no revelar que existe. Repetir la
	 * petición sobre una reserva ya liberada o vencida no hace nada.
	 */
	@Transactional
	public void releaseHold(UUID holdId, UUID userId) {
		OrderRow order = repository.findOrderForUpdate(holdId)
				.filter(row -> row.userId() == null || row.userId().equals(userId))
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", HOLD_NOT_FOUND));
		switch (order.status()) {
			case "PENDING" -> {
				Instant now = clock.instant();
				repository.releaseOrderSeats(order.id());
				repository.updateOrderStatus(order.id(), "CANCELLED", now);
				log.info("Reserva {} liberada por el comprador", order.id());
			}
			case "PAID" -> throw new ApiException(HttpStatus.CONFLICT, "ORDER_ALREADY_PAID",
					"Esta compra ya fue pagada y no se puede liberar.");
			default -> {
				// EXPIRED, CANCELLED o REJECTED: los asientos ya se liberaron.
			}
		}
	}

	/** T-042: vence las órdenes pendientes y libera los asientos cuyo bloqueo terminó. */
	@Transactional
	public int releaseExpiredHolds() {
		Instant now = clock.instant();
		int orders = repository.expireStaleOrders(now);
		int seats = repository.releaseExpiredSeats(now);
		if (orders > 0 || seats > 0) {
			log.info("Reservas vencidas: {} órdenes y {} asientos liberados", orders, seats);
		}
		return seats;
	}

	static SeatStatus statusOf(SeatRow seat, Instant now) {
		if (!seat.seatActive()) {
			return SeatStatus.UNAVAILABLE;
		}
		if ("SOLD".equals(seat.status())) {
			return SeatStatus.SOLD;
		}
		return seat.isFree(now) ? SeatStatus.AVAILABLE : SeatStatus.HELD;
	}

	private ScreeningRow bookableScreening(UUID screeningId, Instant now) {
		return repository.findScreening(screeningId, now)
				.filter(ScreeningRow::bookable)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", SCREENING_NOT_FOUND));
	}

	private void validateSeatSelection(List<UUID> screeningSeatIds) {
		if (screeningSeatIds == null || screeningSeatIds.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Elige al menos un asiento.");
		}
		if (screeningSeatIds.size() > properties.maxSeatsPerOrder()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
					"Puedes reservar máximo " + properties.maxSeatsPerOrder() + " asientos por compra.");
		}
		if (screeningSeatIds.contains(null) || new HashSet<>(screeningSeatIds).size() != screeningSeatIds.size()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La selección de asientos no es válida.");
		}
	}

	private static String requireGuestEmail(String guestEmail) {
		if (guestEmail == null || guestEmail.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
					"Escribe tu correo para enviarte las entradas o inicia sesión.");
		}
		return guestEmail.strip().toLowerCase(Locale.ROOT);
	}
}
