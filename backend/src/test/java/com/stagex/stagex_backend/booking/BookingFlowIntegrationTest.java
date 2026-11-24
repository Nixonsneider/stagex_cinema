package com.stagex.stagex_backend.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import com.stagex.stagex_backend.booking.application.BookingService;
import com.stagex.stagex_backend.booking.application.BookingViews.Hold;
import com.stagex.stagex_backend.booking.application.BookingViews.Seat;
import com.stagex.stagex_backend.booking.application.BookingViews.SeatStatus;
import com.stagex.stagex_backend.catalog.application.CatalogService;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieSummary;
import com.stagex.stagex_backend.catalog.application.CatalogViews.PageResult;
import com.stagex.stagex_backend.catalog.application.CatalogViews.ScreeningSummary;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Cartelera, mapa de asientos y reservas contra PostgreSQL con los datos demo (T-019).
 * Se ejecuta solo si {@code STAGEX_TEST_DATABASE_URL} apunta a una base desechable.
 */
@SpringBootTest(properties = {
		"spring.datasource.url=${STAGEX_TEST_DATABASE_URL}",
		"stagex.booking.expiration-job-enabled=false"
})
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "STAGEX_TEST_DATABASE_URL", matches = ".+")
class BookingFlowIntegrationTest {

	@Autowired
	private CatalogService catalog;

	@Autowired
	private BookingService booking;

	@Autowired
	private JdbcClient jdbc;

	private UUID screeningId;

	@BeforeEach
	void findBookableScreening() {
		screeningId = jdbc.sql("""
				SELECT s.id FROM screenings s
				WHERE s.status = 'SCHEDULED' AND s.starts_at > now() + interval '1 hour'
				ORDER BY s.starts_at
				LIMIT 1""")
				.query(UUID.class)
				.optional()
				.orElse(null);
		assumeTrue(screeningId != null, "No hay funciones futuras en los datos demo; reinicia la base de pruebas");
	}

	@Test
	void laCarteleraPublicaPeliculasConFuncionesYBuscaSinTildes() {
		PageResult<MovieSummary> page = catalog.searchMovies(null, null, null, 0);
		assertFalse(page.content().isEmpty());
		assertEquals(page.content().size(), page.totalElements());

		PageResult<MovieSummary> search = catalog.searchMovies("ORBITA", null, null, 0);
		assertEquals(List.of("Órbita Cero"), search.content().stream().map(MovieSummary::title).toList());

		UUID movieId = jdbc.sql("SELECT movie_id FROM screenings WHERE id = :id").param("id", screeningId)
				.query(UUID.class).single();
		List<ScreeningSummary> screenings = catalog.getScreenings(movieId, null);
		assertTrue(screenings.stream().anyMatch(s -> s.id().equals(screeningId)));
		assertTrue(screenings.stream().allMatch(s -> s.availableSeats() >= 0 && s.price().signum() > 0));

		assertEquals("NOT_FOUND", assertThrows(ApiException.class, () -> catalog.getMovie(UUID.randomUUID())).getCode());
	}

	@Test
	void reservarBloqueaYLiberarDevuelveLosAsientos() {
		List<UUID> seats = freeSeats(2);
		Hold hold = booking.createHold(screeningId, seats, null, "Invitado@Example.test");

		assertNotNull(hold.paymentUrl());
		assertEquals(SeatStatus.HELD, statusOf(seats.get(0)));
		assertEquals("PENDING", orderStatus(hold.orderId()));
		assertEquals("invitado@example.test", jdbc.sql("SELECT guest_email FROM orders WHERE id = :id")
				.param("id", hold.orderId()).query(String.class).single());

		ApiException taken = assertThrows(ApiException.class,
				() -> booking.createHold(screeningId, List.of(seats.get(1)), null, "otro@example.test"));
		assertEquals("SEAT_NOT_AVAILABLE", taken.getCode());

		booking.releaseHold(hold.holdId(), null);
		assertEquals(SeatStatus.AVAILABLE, statusOf(seats.get(0)));
		assertEquals(SeatStatus.AVAILABLE, statusOf(seats.get(1)));
		assertEquals("CANCELLED", orderStatus(hold.orderId()));
	}

	@Test
	void elJobLiberaLosBloqueosVencidos() {
		List<UUID> seats = freeSeats(1);
		Hold hold = booking.createHold(screeningId, seats, null, "invitado@example.test");
		jdbc.sql("UPDATE screening_seats SET hold_expires_at = now() - interval '1 minute' WHERE id = :id")
				.param("id", seats.get(0)).update();

		// Antes del job, el mapa ya lo muestra disponible.
		assertEquals(SeatStatus.AVAILABLE, statusOf(seats.get(0)));
		// Otro contexto de pruebas con el job activo pudo liberarlo antes; se verifica el resultado final.
		booking.releaseExpiredHolds();
		assertEquals("EXPIRED", orderStatus(hold.orderId()));
		assertEquals("AVAILABLE", jdbc.sql("SELECT status FROM screening_seats WHERE id = :id")
				.param("id", seats.get(0)).query(String.class).single());
	}

	@Test
	void dosClientesNoPuedenReservarElMismoAsientoALaVez() throws Exception {
		List<UUID> seats = freeSeats(1);
		int clients = 4;
		ExecutorService executor = Executors.newFixedThreadPool(clients);
		CountDownLatch start = new CountDownLatch(1);
		List<Future<Hold>> results = new ArrayList<>();
		try {
			for (int i = 0; i < clients; i++) {
				String email = "cliente" + i + "@example.test";
				Callable<Hold> attempt = () -> {
					start.await();
					return booking.createHold(screeningId, seats, null, email);
				};
				results.add(executor.submit(attempt));
			}
			start.countDown();
			List<Hold> holds = new ArrayList<>();
			int conflicts = 0;
			for (Future<Hold> result : results) {
				try {
					holds.add(result.get(30, TimeUnit.SECONDS));
				} catch (java.util.concurrent.ExecutionException ex) {
					ApiException error = (ApiException) ex.getCause();
					assertEquals("SEAT_NOT_AVAILABLE", error.getCode());
					conflicts++;
				}
			}
			assertEquals(1, holds.size());
			assertEquals(clients - 1, conflicts);
			booking.releaseHold(holds.get(0).holdId(), null);
		} finally {
			executor.shutdownNow();
		}
	}

	private List<UUID> freeSeats(int count) {
		List<UUID> seats = jdbc.sql("""
				SELECT ss.id FROM screening_seats ss
				JOIN seats se ON se.id = ss.seat_id
				WHERE ss.screening_id = :screeningId AND ss.status = 'AVAILABLE' AND se.status = 'ACTIVE'
				ORDER BY ss.id
				LIMIT :count""")
				.param("screeningId", screeningId)
				.param("count", count)
				.query(UUID.class)
				.list();
		assumeTrue(seats.size() == count, "La función de prueba no tiene asientos libres suficientes");
		return seats;
	}

	private SeatStatus statusOf(UUID screeningSeatId) {
		return booking.getSeatMap(screeningId).seats().stream()
				.filter(seat -> seat.id().equals(screeningSeatId))
				.map(Seat::status)
				.findFirst()
				.orElseThrow();
	}

	private String orderStatus(UUID orderId) {
		return jdbc.sql("SELECT status FROM orders WHERE id = :id").param("id", orderId).query(String.class).single();
	}
}
