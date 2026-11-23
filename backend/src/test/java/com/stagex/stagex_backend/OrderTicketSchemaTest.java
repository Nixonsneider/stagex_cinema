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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-013: restricciones de órdenes, entradas y auditoría (V5).
 */
@SpringBootTest(properties = "spring.datasource.url=${STAGEX_TEST_DATABASE_URL}")
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "STAGEX_TEST_DATABASE_URL", matches = ".+")
class OrderTicketSchemaTest {

    private static final OffsetDateTime START = OffsetDateTime.of(2030, 1, 1, 18, 0, 0, 0, ZoneOffset.UTC);
    private static final String USER_ORDER =
            "INSERT INTO orders (user_id, screening_id, total) VALUES (?, ?, ?)";
    private static final String GUEST_ORDER =
            "INSERT INTO orders (guest_email, screening_id, total) VALUES (?, ?, ?)";
    private static final String ITEM =
            "INSERT INTO order_items (order_id, screening_seat_id, unit_price) VALUES (?, ?, ?)";
    private static final String TICKET = "INSERT INTO tickets (order_item_id, qr_token_hash) VALUES (?, ?)";
    private static final String HASH_A = "a".repeat(64);
    private static final String HASH_B = "0123456789abcdef".repeat(4);

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
    void ordersBelongToExactlyOneBuyerAndKeepTheirPaymentReference() throws SQLException {
        UUID user = user();
        UUID screening = screening(START);
        UUID order = insertId(USER_ORDER, user, screening, money(24000));
        assertEquals("PENDING", value("SELECT status FROM orders WHERE id = ?", order));
        assertEquals("COP", value("SELECT currency FROM orders WHERE id = ?", order));
        insertId(GUEST_ORDER, "invitado@example.test", screening, money(12000));

        rejects("23514", "INSERT INTO orders (screening_id, total) VALUES (?, ?)", screening, money(1));
        rejects("23514", "INSERT INTO orders (user_id, guest_email, screening_id, total) VALUES (?, ?, ?, ?)",
                user, "invitado@example.test", screening, money(1));
        rejects("23514", GUEST_ORDER, "Invitado@Example.test", screening, money(1));
        rejects("23514", GUEST_ORDER, " invitado@example.test", screening, money(1));
        rejects("23514", USER_ORDER, user, screening, money(-1));
        rejects("23503", USER_ORDER, user, UUID.randomUUID(), money(1));
        rejects("23503", USER_ORDER, UUID.randomUUID(), screening, money(1));
        rejects("23514", "UPDATE orders SET status = 'UNKNOWN' WHERE id = ?", order);

        // Una orden solo pasa a pagada con la referencia del proveedor, y la referencia no se repite.
        rejects("23514", "UPDATE orders SET status = 'PAID' WHERE id = ?", order);
        execute("UPDATE orders SET status = 'PAID', payment_reference = 'sbx_ref_1' WHERE id = ?", order);
        UUID other = insertId(USER_ORDER, user, screening, money(12000));
        rejects("23505", "UPDATE orders SET payment_reference = 'sbx_ref_1' WHERE id = ?", other);
    }

    @Test
    void orderItemsOnlyContainSeatsOfTheOrderScreening() throws SQLException {
        UUID room = room();
        UUID screening = screening(room, START);
        UUID otherScreening = screening(room, START.plusHours(3));
        UUID seat = seat(room, "A", 1);
        UUID screeningSeat = screeningSeat(screening, seat);
        UUID foreignScreeningSeat = screeningSeat(otherScreening, seat);
        UUID order = insertId(USER_ORDER, user(), screening, money(12000));

        insertId(ITEM, order, screeningSeat, money(12000));
        rejects("23505", ITEM, order, screeningSeat, money(12000));
        rejects("23514", ITEM, order, foreignScreeningSeat, money(12000));
        rejects("23514", ITEM, order, screeningSeat(screening, seat(room, "A", 2)), money(-1));
        rejects("23503", ITEM, UUID.randomUUID(), screeningSeat, money(12000));
        rejects("23503", ITEM, order, UUID.randomUUID(), money(12000));
    }

    @Test
    void ticketsHaveUniqueHashedQrAndAreValidatedOnlyOnce() throws SQLException {
        UUID room = room();
        UUID screening = screening(room, START);
        UUID order = insertId(USER_ORDER, user(), screening, money(24000));
        UUID firstItem = insertId(ITEM, order, screeningSeat(screening, seat(room, "A", 1)), money(12000));
        UUID secondItem = insertId(ITEM, order, screeningSeat(screening, seat(room, "A", 2)), money(12000));
        UUID cashier = user();

        UUID ticket = insertId(TICKET, firstItem, HASH_A);
        assertEquals("ISSUED", value("SELECT status FROM tickets WHERE id = ?", ticket));
        rejects("23505", TICKET, firstItem, HASH_B);
        rejects("23505", TICKET, secondItem, HASH_A);
        rejects("23514", TICKET, secondItem, HASH_A.toUpperCase(java.util.Locale.ROOT));
        rejects("23514", TICKET, secondItem, "abc");
        rejects("23503", TICKET, UUID.randomUUID(), HASH_B);

        // USED exige fecha y responsable; ningún otro estado los admite.
        rejects("23514", "UPDATE tickets SET status = 'USED' WHERE id = ?", ticket);
        rejects("23514", "UPDATE tickets SET status = 'USED', validated_at = ? WHERE id = ?", START, ticket);
        rejects("23514", "UPDATE tickets SET validated_at = ?, validated_by = ? WHERE id = ?", START, cashier, ticket);
        execute("UPDATE tickets SET status = 'USED', validated_at = ?, validated_by = ? WHERE id = ?",
                START, cashier, ticket);
        rejects("23514", "UPDATE tickets SET status = 'UNKNOWN' WHERE id = ?", ticket);
    }

    @Test
    void auditEventsAreAppendOnly() throws SQLException {
        String event = """
                INSERT INTO audit_events (actor_id, action, entity_type, entity_id, metadata_json)
                VALUES (?, ?, 'ticket', ?, ?::jsonb)
                """;
        UUID actor = user();
        UUID id = insertId(event, actor, "TICKET_VALIDATED", UUID.randomUUID(), "{\"seat\": \"A-1\"}");
        insertId("INSERT INTO audit_events (action, entity_type) VALUES ('HOLD_EXPIRED', 'screening_seat')");

        rejects("23514", event, actor, "ticket validated", UUID.randomUUID(), "{}");
        rejects("23514", event, actor, "TICKET_VALIDATED", UUID.randomUUID(), "[]");
        rejects("23503", event, UUID.randomUUID(), "TICKET_VALIDATED", UUID.randomUUID(), "{}");
        rejects("23001", "UPDATE audit_events SET action = 'OTHER' WHERE id = ?", id);
        rejects("23001", "DELETE FROM audit_events WHERE id = ?", id);
    }

    private UUID user() throws SQLException {
        return insertId("INSERT INTO users (name, email, password_hash) VALUES ('Ana', ?, 'test-hash')",
                UUID.randomUUID() + "@example.test");
    }

    private UUID room() throws SQLException {
        UUID cinema = insertId(
                "INSERT INTO cinemas (name, address, timezone) VALUES ('Cine', 'Calle 1', 'America/Bogota')");
        return insertId("INSERT INTO rooms (cinema_id, name, room_type) VALUES (?, 'Sala 1', '2D')", cinema);
    }

    private UUID screening(OffsetDateTime startsAt) throws SQLException {
        return screening(room(), startsAt);
    }

    private UUID screening(UUID room, OffsetDateTime startsAt) throws SQLException {
        UUID cinema = (UUID) value("SELECT cinema_id FROM rooms WHERE id = ?", room);
        UUID movie = insertId("""
                INSERT INTO movies (cinema_id, title, synopsis, duration_minutes, genre,
                                    age_rating, language, format, poster_url)
                VALUES (?, 'Stage X', 'Sinopsis', 110, 'Drama', 'PG', 'es', '2D', '/poster.jpg')
                """, cinema);
        return insertId("""
                INSERT INTO screenings (movie_id, room_id, starts_at, ends_at, price)
                VALUES (?, ?, ?, ?, 12000)
                """, movie, room, startsAt, startsAt.plusHours(2));
    }

    private UUID seat(UUID room, String row, int number) throws SQLException {
        return insertId("INSERT INTO seats (room_id, row_label, seat_number) VALUES (?, ?, ?)", room, row, number);
    }

    private UUID screeningSeat(UUID screening, UUID seat) throws SQLException {
        return insertId("INSERT INTO screening_seats (screening_id, seat_id) VALUES (?, ?)", screening, seat);
    }

    private static BigDecimal money(long value) {
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
