package com.stagex.stagex_backend.booking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.stagex.stagex_backend.booking.application.BookingViews.Hold;
import com.stagex.stagex_backend.booking.application.BookingViews.ScreeningSeats;
import com.stagex.stagex_backend.booking.application.BookingViews.SeatStatus;
import com.stagex.stagex_backend.booking.repository.BookingRepository;
import com.stagex.stagex_backend.booking.repository.BookingRepository.OrderRow;
import com.stagex.stagex_backend.booking.repository.BookingRepository.ScreeningRow;
import com.stagex.stagex_backend.booking.repository.BookingRepository.SeatRow;
import com.stagex.stagex_backend.payment.gateway.FakePaymentGateway;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Reglas de disponibilidad y reserva (T-040 a T-042) sin base de datos.
 */
class BookingServiceTest {

	private static final Instant NOW = Instant.parse("2026-11-23T20:00:00Z");
	private static final UUID SCREENING = UUID.randomUUID();
	private static final BigDecimal PRICE = new BigDecimal("18000.00");

	private final BookingRepository repository = mock(BookingRepository.class);
	private final FakePaymentGateway payments = new FakePaymentGateway();
	private final BookingProperties properties = new BookingProperties(Duration.ofMinutes(10), 4,
			Duration.ofSeconds(30));
	private BookingService service;

	@BeforeEach
	void setUp() {
		service = new BookingService(repository, payments, properties, Clock.fixed(NOW, ZoneOffset.UTC));
		when(repository.findScreening(eq(SCREENING), any())).thenReturn(Optional.of(screening(true)));
	}

	@Test
	void elMapaMuestraVencidosComoDisponiblesEInactivosComoNoDisponibles() {
		SeatRow free = seat("AVAILABLE", null, true);
		SeatRow held = seat("HELD", NOW.plusSeconds(60), true);
		SeatRow expired = seat("HELD", NOW.minusSeconds(1), true);
		SeatRow sold = seat("SOLD", null, true);
		SeatRow broken = seat("AVAILABLE", null, false);
		when(repository.findSeats(SCREENING)).thenReturn(List.of(free, held, expired, sold, broken));

		ScreeningSeats map = service.getSeatMap(SCREENING);

		assertEquals(List.of(SeatStatus.AVAILABLE, SeatStatus.HELD, SeatStatus.AVAILABLE, SeatStatus.SOLD,
				SeatStatus.UNAVAILABLE), map.seats().stream().map(BookingViews.Seat::status).toList());
		assertEquals(4, map.maxSeatsPerOrder());
		assertEquals(10, map.holdMinutes());
	}

	@Test
	void unaFuncionNoDisponibleResponde404() {
		when(repository.findScreening(eq(SCREENING), any())).thenReturn(Optional.of(screening(false)));
		assertEquals("NOT_FOUND", assertThrows(ApiException.class, () -> service.getSeatMap(SCREENING)).getCode());

		UUID unknown = UUID.randomUUID();
		when(repository.findScreening(eq(unknown), any())).thenReturn(Optional.empty());
		assertEquals("NOT_FOUND", assertThrows(ApiException.class,
				() -> service.createHold(unknown, List.of(UUID.randomUUID()), null, "ana@correo.com")).getCode());
	}

	@Test
	void reservarBloqueaLosAsientosYCalculaElTotalEnElBackend() {
		SeatRow a = seat("AVAILABLE", null, true);
		SeatRow b = seat("HELD", NOW.minusSeconds(5), true);
		List<UUID> ids = List.of(a.id(), b.id());
		when(repository.lockSeats(SCREENING, ids)).thenReturn(List.of(a, b));

		Hold hold = service.createHold(SCREENING, ids, null, "  Ana@Correo.com ");

		assertEquals(hold.orderId(), hold.holdId());
		assertEquals(new BigDecimal("36000.00"), hold.amount());
		assertEquals("COP", hold.currency());
		assertEquals(NOW.plus(Duration.ofMinutes(10)), hold.expiresAt());
		assertTrue(hold.paymentUrl().startsWith("https://fake.local/checkout/"));
		verify(repository).insertOrder(hold.orderId(), null, "ana@correo.com", SCREENING, new BigDecimal("36000.00"),
				"COP", NOW);
		verify(repository).insertItem(hold.orderId(), a.id(), PRICE);
		verify(repository).insertItem(hold.orderId(), b.id(), PRICE);
		verify(repository).holdSeats(ids, NOW.plus(Duration.ofMinutes(10)));
		verify(repository).expirePendingOrdersHolding(ids, NOW);
		verify(repository).setPaymentReference(eq(hold.orderId()), eq("fake_1"), eq(NOW));
		assertEquals(new BigDecimal("36000.00"), payments.requests().get(0).amount());
	}

	@Test
	void unClienteRegistradoNoNecesitaCorreoDeInvitado() {
		SeatRow a = seat("AVAILABLE", null, true);
		when(repository.lockSeats(SCREENING, List.of(a.id()))).thenReturn(List.of(a));
		UUID userId = UUID.randomUUID();

		Hold hold = service.createHold(SCREENING, List.of(a.id()), userId, null);

		verify(repository).insertOrder(eq(hold.orderId()), eq(userId), isNull(), eq(SCREENING), any(), eq("COP"),
				eq(NOW));
	}

	@Test
	void unAsientoBloqueadoOVendidoRespondeConflicto() {
		SeatRow free = seat("AVAILABLE", null, true);
		SeatRow held = seat("HELD", NOW.plusSeconds(120), true);
		List<UUID> ids = List.of(free.id(), held.id());
		when(repository.lockSeats(SCREENING, ids)).thenReturn(List.of(free, held));

		ApiException error = assertThrows(ApiException.class,
				() -> service.createHold(SCREENING, ids, null, "ana@correo.com"));

		assertEquals("SEAT_NOT_AVAILABLE", error.getCode());
		assertEquals(409, error.getStatus().value());
		verify(repository, never()).insertOrder(any(), any(), any(), any(), any(), any(), any());
		verify(repository, never()).holdSeats(anyList(), any());
	}

	@Test
	void validaLaSeleccionYElComprador() {
		UUID seat = UUID.randomUUID();
		assertEquals("VALIDATION_ERROR", assertThrows(ApiException.class,
				() -> service.createHold(SCREENING, List.of(), null, "ana@correo.com")).getCode());
		assertEquals("VALIDATION_ERROR", assertThrows(ApiException.class,
				() -> service.createHold(SCREENING, List.of(seat, seat), null, "ana@correo.com")).getCode());
		List<UUID> tooMany = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			tooMany.add(UUID.randomUUID());
		}
		assertEquals("VALIDATION_ERROR", assertThrows(ApiException.class,
				() -> service.createHold(SCREENING, tooMany, null, "ana@correo.com")).getCode());
		assertEquals("VALIDATION_ERROR", assertThrows(ApiException.class,
				() -> service.createHold(SCREENING, List.of(seat), null, " ")).getCode());

		// Un asiento que no es de esta función no se puede reservar.
		when(repository.lockSeats(SCREENING, List.of(seat))).thenReturn(List.of());
		assertEquals("VALIDATION_ERROR", assertThrows(ApiException.class,
				() -> service.createHold(SCREENING, List.of(seat), null, "ana@correo.com")).getCode());
	}

	@Test
	void soloElDuenoLiberaUnaReservaDeClienteRegistrado() {
		UUID owner = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		when(repository.findOrderForUpdate(orderId)).thenReturn(Optional.of(new OrderRow(orderId, owner, "PENDING")));

		assertEquals("NOT_FOUND",
				assertThrows(ApiException.class, () -> service.releaseHold(orderId, UUID.randomUUID())).getCode());
		assertEquals("NOT_FOUND", assertThrows(ApiException.class, () -> service.releaseHold(orderId, null)).getCode());
		verify(repository, never()).releaseOrderSeats(orderId);

		service.releaseHold(orderId, owner);
		verify(repository).releaseOrderSeats(orderId);
		verify(repository).updateOrderStatus(orderId, "CANCELLED", NOW);
	}

	@Test
	void liberarUnaReservaVencidaNoHaceNadaYUnaPagadaEsConflicto() {
		UUID expired = UUID.randomUUID();
		UUID paid = UUID.randomUUID();
		when(repository.findOrderForUpdate(expired)).thenReturn(Optional.of(new OrderRow(expired, null, "EXPIRED")));
		when(repository.findOrderForUpdate(paid)).thenReturn(Optional.of(new OrderRow(paid, null, "PAID")));

		service.releaseHold(expired, null);
		verify(repository, never()).releaseOrderSeats(expired);
		assertEquals("ORDER_ALREADY_PAID",
				assertThrows(ApiException.class, () -> service.releaseHold(paid, null)).getCode());
	}

	@Test
	void elJobVenceOrdenesYLiberaAsientos() {
		when(repository.expireStaleOrders(NOW)).thenReturn(1);
		when(repository.releaseExpiredSeats(NOW)).thenReturn(2);

		assertEquals(2, service.releaseExpiredHolds());
		verify(repository).expireStaleOrders(NOW);
		verify(repository).releaseExpiredSeats(NOW);
	}

	@Test
	void unaReservaGratuitaNoAbreSesionDePago() {
		when(repository.findScreening(eq(SCREENING), any())).thenReturn(Optional.of(new ScreeningRow(SCREENING,
				UUID.randomUUID(), "Función gratis", NOW.plusSeconds(86_400), "Sala 1", BigDecimal.ZERO, "COP", true)));
		SeatRow a = seat("AVAILABLE", null, true);
		when(repository.lockSeats(SCREENING, List.of(a.id()))).thenReturn(List.of(a));

		Hold hold = service.createHold(SCREENING, List.of(a.id()), null, "ana@correo.com");

		assertNull(hold.paymentUrl());
		assertTrue(payments.requests().isEmpty());
	}

	private static ScreeningRow screening(boolean bookable) {
		return new ScreeningRow(SCREENING, UUID.randomUUID(), "El Último Faro", NOW.plusSeconds(86_400), "Sala 1", PRICE,
				"COP", bookable);
	}

	private static SeatRow seat(String status, Instant holdExpiresAt, boolean active) {
		return new SeatRow(UUID.randomUUID(), "A", 1, "STANDARD", active, status, holdExpiresAt);
	}
}
