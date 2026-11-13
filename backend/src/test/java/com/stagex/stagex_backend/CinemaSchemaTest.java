package com.stagex.stagex_backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=${STAGEX_TEST_DATABASE_URL}")
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "STAGEX_TEST_DATABASE_URL", matches = ".+")
class CinemaSchemaTest {

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
    void usersHaveUniqueEmailsValidRolesAndExplicitCinemaAssignments() throws SQLException {
        String email = UUID.randomUUID() + "@example.test";
        UUID user = insertId("INSERT INTO users (name, email, password_hash) VALUES ('Ana', ?, 'test-hash')", email);
        assertEquals("PENDING_VERIFICATION", value("SELECT status FROM users WHERE id = ?", user));
        assertEquals("CUSTOMER", value("SELECT role FROM users WHERE id = ?", user));
        rejects("23505", "INSERT INTO users (name, email, password_hash) VALUES ('Otra', ?, 'test-hash')",
                email.toUpperCase(java.util.Locale.ROOT));
        rejects("23514", "UPDATE users SET role = 'UNKNOWN' WHERE id = ?", user);
        rejects("23514", "UPDATE users SET status = 'UNKNOWN' WHERE id = ?", user);

        execute("UPDATE users SET role = 'CASHIER' WHERE id = ?", user);
        execute("UPDATE users SET role = 'CINEMA_ADMIN' WHERE id = ?", user);
        UUID cinema = cinema();
        String assign = "INSERT INTO user_cinemas (user_id, cinema_id) VALUES (?, ?)";
        execute(assign, user, cinema);
        execute(assign, user, cinema());
        rejects("23505", assign, user, cinema);
        rejects("23503", assign, user, UUID.randomUUID());
        rejects("23503", assign, UUID.randomUUID(), cinema);
    }

    @Test
    void roomsAndSeatCoordinatesAreUniqueWithinTheirParent() throws SQLException {
        UUID cinema = cinema();
        String room = "INSERT INTO rooms (cinema_id, name, room_type) VALUES (?, ?, '2D')";
        UUID first = insertId(room, cinema, "Sala 1");
        rejects("23505", room, cinema, "sala 1");
        rejects("23503", room, UUID.randomUUID(), "Sala 2");
        UUID second = insertId(room, cinema(), "Sala 1");

        String seat = "INSERT INTO seats (room_id, row_label, seat_number) VALUES (?, ?, ?)";
        execute(seat, first, "A", 1);
        execute(seat, first, "A", 2);
        execute(seat, first, "B", 1);
        execute(seat, second, "A", 1);
        rejects("23505", seat, first, "A", 1);
        rejects("23514", seat, first, "", 3);
        rejects("23514", seat, first, "A", 0);
        rejects("23503", seat, UUID.randomUUID(), "A", 1);
        rejects("23514", "UPDATE seats SET status = 'SOLD' WHERE room_id = ?", first);
        rejects("23503", "DELETE FROM rooms WHERE id = ?", first);
    }

    @Test
    void moviesRequireAnExistingCinemaAndPositiveDuration() throws SQLException {
        String movie = """
                INSERT INTO movies (cinema_id, title, synopsis, duration_minutes, genre,
                                    age_rating, language, format, poster_url)
                VALUES (?, 'Stage X', 'Sinopsis', ?, 'Drama', 'PG', 'es', '2D', '/poster.jpg')
                """;
        UUID cinema = cinema();
        UUID id = insertId(movie, cinema, 90);
        execute("UPDATE movies SET status = 'INACTIVE' WHERE id = ?", id);
        rejects("23514", movie, cinema, 0);
        rejects("23503", movie, UUID.randomUUID(), 90);
        rejects("23514", "UPDATE movies SET status = 'UNKNOWN' WHERE id = ?", id);
    }

    private UUID cinema() throws SQLException {
        return insertId("INSERT INTO cinemas (name, address, timezone) VALUES ('Cine', 'Calle 1', 'America/Bogota')");
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
