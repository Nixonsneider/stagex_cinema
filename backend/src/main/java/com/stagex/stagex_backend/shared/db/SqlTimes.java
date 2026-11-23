package com.stagex.stagex_backend.shared.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Conversión de instantes para consultas JDBC. El driver de PostgreSQL trabaja con
 * {@link OffsetDateTime} para columnas {@code timestamptz}, no con {@link Instant}.
 */
public final class SqlTimes {

	private SqlTimes() {
	}

	public static OffsetDateTime utc(Instant instant) {
		return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	public static Instant instant(ResultSet rs, String column) throws SQLException {
		OffsetDateTime value = rs.getObject(column, OffsetDateTime.class);
		return value == null ? null : value.toInstant();
	}
}
