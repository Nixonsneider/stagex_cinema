package com.stagex.stagex_backend.booking.repository;

import static com.stagex.stagex_backend.shared.db.SqlTimes.instant;
import static com.stagex.stagex_backend.shared.db.SqlTimes.utc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Acceso a funciones, asientos por función y órdenes para el flujo de reserva
 * (T-040 a T-042). Los bloqueos se toman con {@code SELECT ... FOR UPDATE} en orden de
 * identificador para que dos reservas simultáneas no se bloqueen mutuamente.
 */
@Repository
public class BookingRepository {

	/** Función con los datos que necesita el mapa y la reserva. */
	public record ScreeningRow(UUID id, UUID movieId, String movieTitle, Instant startsAt, String roomName,
			BigDecimal price, String currency, boolean bookable) {
	}

	/** Asiento de una función con su estado guardado (sin interpretar vencimientos). */
	public record SeatRow(UUID id, String row, int number, String seatType, boolean seatActive, String status,
			Instant holdExpiresAt) {

		/** Libre para reservar: disponible o con un bloqueo ya vencido, y el asiento físico habilitado. */
		public boolean isFree(Instant now) {
			return seatActive && ("AVAILABLE".equals(status)
					|| ("HELD".equals(status) && holdExpiresAt != null && !holdExpiresAt.isAfter(now)));
		}
	}

	/** Orden con el comprador y su estado. {@code userId} es nulo en compras de invitado. */
	public record OrderRow(UUID id, UUID userId, String status) {
	}

	private final JdbcClient jdbc;

	public BookingRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Función con su película y sala. {@code bookable} indica si se puede reservar ahora: programada,
	 * futura, con película, sala y cine activos.
	 */
	public Optional<ScreeningRow> findScreening(UUID screeningId, Instant now) {
		return jdbc.sql("""
				SELECT s.id, s.movie_id, m.title AS movie_title, s.starts_at, r.name AS room_name, s.price, s.currency,
				       (s.status = 'SCHEDULED' AND s.starts_at > :now AND r.status = 'ACTIVE'
				        AND m.status = 'ACTIVE' AND c.status = 'ACTIVE') AS bookable
				FROM screenings s
				JOIN movies m ON m.id = s.movie_id
				JOIN rooms r ON r.id = s.room_id
				JOIN cinemas c ON c.id = r.cinema_id
				WHERE s.id = :screeningId""")
				.param("screeningId", screeningId)
				.param("now", utc(now))
				.query((rs, rowNum) -> new ScreeningRow(
						rs.getObject("id", UUID.class),
						rs.getObject("movie_id", UUID.class),
						rs.getString("movie_title"),
						instant(rs, "starts_at"),
						rs.getString("room_name"),
						rs.getBigDecimal("price"),
						rs.getString("currency").strip(),
						rs.getBoolean("bookable")))
				.optional();
	}

	/** Mapa completo de la función, ordenado por fila y número. */
	public List<SeatRow> findSeats(UUID screeningId) {
		return jdbc.sql("""
				SELECT ss.id, se.row_label, se.seat_number, se.seat_type, se.status AS seat_status,
				       ss.status, ss.hold_expires_at
				FROM screening_seats ss
				JOIN seats se ON se.id = ss.seat_id
				WHERE ss.screening_id = :screeningId
				ORDER BY length(se.row_label), se.row_label, se.seat_number""")
				.param("screeningId", screeningId)
				.query((rs, rowNum) -> seat(rs))
				.list();
	}

	/**
	 * Bloquea las filas pedidas de la función hasta el final de la transacción. Devuelve solo
	 * las que existen en esa función.
	 */
	public List<SeatRow> lockSeats(UUID screeningId, Collection<UUID> screeningSeatIds) {
		return jdbc.sql("""
				SELECT ss.id, se.row_label, se.seat_number, se.seat_type, se.status AS seat_status,
				       ss.status, ss.hold_expires_at
				FROM screening_seats ss
				JOIN seats se ON se.id = ss.seat_id
				WHERE ss.screening_id = :screeningId AND ss.id IN (:ids)
				ORDER BY ss.id
				FOR UPDATE OF ss""")
				.param("screeningId", screeningId)
				.param("ids", List.copyOf(screeningSeatIds))
				.query((rs, rowNum) -> seat(rs))
				.list();
	}

	/**
	 * Marca como vencidas las órdenes pendientes que todavía apuntan a estos asientos. Se usa
	 * al tomar un asiento cuyo bloqueo anterior ya venció y el job aún no lo había liberado.
	 */
	public int expirePendingOrdersHolding(Collection<UUID> screeningSeatIds, Instant now) {
		return jdbc.sql("""
				UPDATE orders o
				SET status = 'EXPIRED', updated_at = :now
				WHERE o.status = 'PENDING'
				  AND EXISTS (SELECT 1 FROM order_items oi
				              WHERE oi.order_id = o.id AND oi.screening_seat_id IN (:ids))""")
				.param("ids", List.copyOf(screeningSeatIds))
				.param("now", utc(now))
				.update();
	}

	public void insertOrder(UUID orderId, UUID userId, String guestEmail, UUID screeningId, BigDecimal total,
			String currency, Instant now) {
		jdbc.sql("""
				INSERT INTO orders (id, user_id, guest_email, screening_id, status, total, currency, created_at, updated_at)
				VALUES (:id, :userId, :guestEmail, :screeningId, 'PENDING', :total, :currency, :now, :now)""")
				.param("id", orderId)
				.param("userId", userId)
				.param("guestEmail", guestEmail)
				.param("screeningId", screeningId)
				.param("total", total)
				.param("currency", currency)
				.param("now", utc(now))
				.update();
	}

	public void insertItem(UUID orderId, UUID screeningSeatId, BigDecimal unitPrice) {
		jdbc.sql("""
				INSERT INTO order_items (id, order_id, screening_seat_id, unit_price)
				VALUES (:id, :orderId, :screeningSeatId, :unitPrice)""")
				.param("id", UUID.randomUUID())
				.param("orderId", orderId)
				.param("screeningSeatId", screeningSeatId)
				.param("unitPrice", unitPrice)
				.update();
	}

	public int holdSeats(Collection<UUID> screeningSeatIds, Instant expiresAt) {
		return jdbc.sql("""
				UPDATE screening_seats
				SET status = 'HELD', hold_expires_at = :expiresAt, version = version + 1
				WHERE id IN (:ids)""")
				.param("ids", List.copyOf(screeningSeatIds))
				.param("expiresAt", utc(expiresAt))
				.update();
	}

	public void setPaymentReference(UUID orderId, String paymentReference, Instant now) {
		jdbc.sql("UPDATE orders SET payment_reference = :reference, updated_at = :now WHERE id = :id")
				.param("reference", paymentReference)
				.param("now", utc(now))
				.param("id", orderId)
				.update();
	}

	public Optional<OrderRow> findOrderForUpdate(UUID orderId) {
		return jdbc.sql("SELECT id, user_id, status FROM orders WHERE id = :id FOR UPDATE")
				.param("id", orderId)
				.query((rs, rowNum) -> new OrderRow(
						rs.getObject("id", UUID.class),
						rs.getObject("user_id", UUID.class),
						rs.getString("status")))
				.optional();
	}

	/** Devuelve a disponibles los asientos que esta orden mantiene bloqueados. */
	public int releaseOrderSeats(UUID orderId) {
		return jdbc.sql("""
				UPDATE screening_seats ss
				SET status = 'AVAILABLE', hold_expires_at = NULL, version = ss.version + 1
				FROM order_items oi
				WHERE oi.order_id = :orderId AND oi.screening_seat_id = ss.id AND ss.status = 'HELD'""")
				.param("orderId", orderId)
				.update();
	}

	public void updateOrderStatus(UUID orderId, String status, Instant now) {
		jdbc.sql("UPDATE orders SET status = :status, updated_at = :now WHERE id = :id")
				.param("status", status)
				.param("now", utc(now))
				.param("id", orderId)
				.update();
	}

	/** T-042: las órdenes pendientes con algún bloqueo vencido pasan a {@code EXPIRED}. */
	public int expireStaleOrders(Instant now) {
		return jdbc.sql("""
				UPDATE orders o
				SET status = 'EXPIRED', updated_at = :now
				WHERE o.status = 'PENDING'
				  AND EXISTS (SELECT 1
				              FROM order_items oi
				              JOIN screening_seats ss ON ss.id = oi.screening_seat_id
				              WHERE oi.order_id = o.id AND ss.status = 'HELD' AND ss.hold_expires_at <= :now)""")
				.param("now", utc(now))
				.update();
	}

	/** T-042: los bloqueos vencidos vuelven a estar disponibles. */
	public int releaseExpiredSeats(Instant now) {
		return jdbc.sql("""
				UPDATE screening_seats
				SET status = 'AVAILABLE', hold_expires_at = NULL, version = version + 1
				WHERE status = 'HELD' AND hold_expires_at <= :now""")
				.param("now", utc(now))
				.update();
	}

	private static SeatRow seat(ResultSet rs) throws SQLException {
		return new SeatRow(
				rs.getObject("id", UUID.class),
				rs.getString("row_label"),
				rs.getInt("seat_number"),
				rs.getString("seat_type"),
				"ACTIVE".equals(rs.getString("seat_status")),
				rs.getString("status"),
				instant(rs, "hold_expires_at"));
	}
}
