package com.stagex.stagex_backend.auth.repository;

import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Consulta la asignación de cines del personal (tabla {@code user_cinemas}, V2).
 * Se usa JDBC porque la tabla de unión no tiene entidad propia.
 */
@Repository
public class UserCinemaRepository {

	private final JdbcClient jdbc;

	public UserCinemaRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	public boolean isAssigned(UUID userId, UUID cinemaId) {
		return jdbc.sql("SELECT EXISTS (SELECT 1 FROM user_cinemas WHERE user_id = ? AND cinema_id = ?)")
				.param(userId)
				.param(cinemaId)
				.query(Boolean.class)
				.single();
	}
}
