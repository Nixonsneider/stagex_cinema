package com.stagex.stagex_backend.booking.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Respuestas de disponibilidad y reserva (docs/API.md, "Catálogo y funciones" y "Compra").
 */
public final class BookingViews {

	private BookingViews() {
	}

	/** Estado que ve el cliente; un bloqueo vencido ya se muestra como disponible. */
	public enum SeatStatus {
		AVAILABLE, HELD, SOLD, UNAVAILABLE
	}

	/** Asiento de la función. {@code id} es el de {@code screening_seats}, el que se envía a {@code POST /holds}. */
	public record Seat(UUID id, String row, int number, String seatType, SeatStatus status) {
	}

	/** Mapa de {@code GET /screenings/{screeningId}/seats}. */
	public record ScreeningSeats(UUID screeningId, UUID movieId, String movieTitle, Instant startsAt, String roomName,
			BigDecimal price, String currency, int maxSeatsPerOrder, long holdMinutes, List<Seat> seats) {
	}

	/** Respuesta de {@code POST /holds}. En V1.0 {@code holdId} es el mismo {@code orderId}. */
	public record Hold(UUID holdId, UUID orderId, Instant expiresAt, BigDecimal amount, String currency,
			String paymentUrl) {
	}
}
