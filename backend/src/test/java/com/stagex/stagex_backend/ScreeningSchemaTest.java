package com.stagex.stagex_backend;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-012: restricciones de funciones y asientos por función (V4).
 */
@SpringBootTest(properties = "spring.datasource.url=${STAGEX_TEST_DATABASE_URL}")
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "STAGEX_TEST_DATABASE_URL", matches = ".+")
class ScreeningSchemaTest {

    private static final OffsetDateTime START = OffsetDateTime.of(2030, 1, 1, 18, 0, 0, 0, ZoneOffset.UTC);
    private static final String SCREENING = """
            INSERT INTO screenings (movie_id, room_id, starts_at, ends_at, price)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SCREENING_SEAT =
            "INSERT INTO screening_seats (screening_id, seat_id) VALUES (?, ?)";

    @Autowired
    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void beginTransaction() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(false);
    }

    @AfterEach
    void discardTestData() throws SQLException {
        try (Connection transaction = connection) {
            transaction.rollback();
        }
    }

    @Test
    void screeningsRejectInvalidTimesAndPrices() throws SQLException {
        UUID cinema = cinema();
        UUID movie = movie(cinema);
        UUID room = room(cinema, "Sala 1");
        UUID screening = insertId(SCREENING, movie, room, START, START.plusHours(2), price(12000));

        assertEquals("SCHEDULED", value("SELECT status FROM screenings WHERE id = ?", screening));
        assertEquals("COP", value("SELECT currency FROM screenings WHERE id = ?", screening));
        rejects("23514", SCREENING, movie, room, START.plusDays(1), START.plusDays(1), price(12000));
        rejects("23514", SCREENING, movie, room, START.plusDays(1), START.plusDays(1).minusHours(1), price(12000));
        rejects("23514", SCREENING, movie, room, START.plusDays(1), START.plusDays(1).plusHours(2), price(-1));
        rejects("23514", "UPDATE screenings SET status = 'UNKNOWN' WHERE id = ?", screening);
        rejects("23514", "UPDATE screenings SET currency = 'cop' WHERE id = ?", screening);
        rejects("23503", SCREENING, UUID.randomUUID(), room, START.plusDays(1), START.plusDays(1).plusHours(2), price(1));
        rejects("23503", SCREENING, movie, UUID.randomUUID(), START.plusDays(1), START.plusDays(1).plusHours(2), price(1));
    }

    @Test
    void scheduledScreeningsCannotOverlapInTheSameRoom() throws SQLException {
        UUID cinema = cinema();
        UUID movie = movie(cinema);
        UUID room = room(cinema, "Sala 1");
        UUID otherRoom = room(cinema, "Sala 2");
        UUID first = insertId(SCREENING, movie, room, START, START.plusHours(2), price(12000));

        rejects("23P01", SCREENING, movie, room, START.plusHours(1), START.plusHours(3), price(12000));
        rejects("23P01", SCREENING, movie, room, START.minusMinutes(30), START.plusMinutes(30), price(12000));
        // Termina justo cuando empieza la siguiente: no hay solapamiento.
        insertId(SCREENING, movie, room, START.plusHours(2), START.plusHours(4), price(12000));
        insertId(SCREENING, movie, otherRoom, START, START.plusHours(2), price(12000));

        // Una función cancelada libera el horario, pero no puede reactivarse si ya se ocupó.
        execute("UPDATE screenings SET status = 'CANCELLED' WHERE id = ?", first);
        insertId(SCREENING, movie, room, START, START.plusHours(2), price(15000));
        rejects("23P01", "UPDATE screenings SET status = 'SCHEDULED' WHERE id = ?", first);
    }

    @Test
    void screeningSeatsKeepAnIndependentStatePerScreening() throws SQLException {
        UUID cinema = cinema();
        UUID movie = movie(cinema);
        UUID room = room(cinema, "Sala 1");
        UUID otherRoom = room(cinema, "Sala 2");
        UUID seat = seat(room, "A", 1);
        UUID foreignSeat = seat(otherRoom, "A", 1);
        UUID first = insertId(SCREENING, movie, room, START, START.plusHours(2), price(12000));
        UUID second = insertId(SCREENING, movie, room, START.plusHours(3), START.plusHours(5), price(12000));

        UUID firstSeat = insertId(SCREENING_SEAT, first, seat);
        insertId(SCREENING_SEAT, second, seat);
        assertEquals("AVAILABLE", value("SELECT status FROM screening_seats WHERE id = ?", firstSeat));
        assertEquals(0L, value("SELECT version FROM screening_seats WHERE id = ?", firstSeat));
        assertNull(value("SELECT hold_expires_at FROM screening_seats WHERE id = ?", firstSeat));

        rejects("23505", SCREENING_SEAT, first, seat);
        rejects("23514", SCREENING_SEAT, first, foreignSeat);
        rejects("23503", SCREENING_SEAT, UUID.randomUUID(), seat);
        rejects("23503", SCREENING_SEAT, first, UUID.randomUUID());
    }

    @Test
    void onlyHeldSeatsHaveAnExpiration() throws SQLException {
        UUID cinema = cinema();
        UUID room = room(cinema, "Sala 1");
        UUID screening = insertId(SCREENING, movie(cinema), room, START, START.plusHours(2), price(12000));
        UUID screeningSeat = insertId(SCREENING_SEAT, screening, seat(room, "A", 1));

        rejects("23514", "UPDATE screening_seats SET status = 'HELD' WHERE id = ?", screeningSeat);
        rejects("23514", "UPDATE screening_seats SET hold_expires_at = ? WHERE id = ?", START, screeningSeat);
        execute("UPDATE screening_seats SET status = 'HELD', hold_expires_at = ? WHERE id = ?",
                START.minusMinutes(10), screeningSeat);
        rejects("23514", "UPDATE screening_seats SET status = 'SOLD' WHERE id = ?", screeningSeat);
        execute("UPDATE screening_seats SET status = 'SOLD', hold_expires_at = NULL, version = version + 1 WHERE id = ?",
                screeningSeat);
        assertEquals(1L, value("SELECT version FROM screening_seats WHERE id = ?", screeningSeat));
        rejects("23514", "UPDATE screening_seats SET status = 'UNKNOWN' WHERE id = ?", screeningSeat);
    }

    private UUID cinema() throws SQLException {
        return insertId("INSERT INTO cinemas (name, address, timezone) VALUES ('Cine', 'Calle 1', 'America/Bogota')");
    }

    private UUID movie(UUID cinema) throws SQLException {
        return insertId("""
                INSERT INTO movies (cinema_id, title, synopsis, duration_minutes, genre,
                                    age_rating, language, format, poster_url)
                VALUES (?, 'Stage X', 'Sinopsis', 110, 'Drama', 'PG', 'es', '2D', '/poster.jpg')
                """, cinema);
    }

    private UUID room(UUID cinema, String name) throws SQLException {
        return insertId("INSERT INTO rooms (cinema_id, name, room_type) VALUES (?, ?, '2D')", cinema, name);
    }

    private UUID seat(UUID room, String row, int number) throws SQLException {
        return insertId("INSERT INTO seats (room_id, row_label, seat_number) VALUES (?, ?, ?)", room, row, number);
    }

    private static BigDecimal price(long value) {
        return BigDecimal.valueOf(value);
    }

    private UUID insertId(String sql, Object... args) throws SQLException {
        return (UUID) value(sql + " RETURNING id", args);
    }

    private Object value(String sql, Object... args) throws SQLException {
        try (var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            try (var result = statement.executeQuery()) {
                assertTrue(result.next());
                return result.getObject(1);
            }
        }
    }

    private void execute(String sql, Object... args) throws SQLException {
        try (var statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            statement.executeUpdate();
        }
    }

    private void rejects(String sqlState, String sql, Object... args) throws SQLException {
        Savepoint before = connection.setSavepoint();
        var error = assertThrows(SQLException.class, () -> execute(sql, args));
        assertEquals(sqlState, error.getSQLState());
        connection.rollback(before);
        connection.releaseSavepoint(before);
    }
}
