package com.stagex.stagex_backend.catalog.repository;

import static com.stagex.stagex_backend.shared.db.SqlTimes.instant;
import static com.stagex.stagex_backend.shared.db.SqlTimes.utc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieDetail;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieFilter;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieSummary;
import com.stagex.stagex_backend.catalog.application.CatalogViews.ScreeningSummary;

/**
 * Consultas de la cartelera pública. Solo se publican películas activas de cines activos
 * con al menos una función programada, futura y en una sala habilitada (T-033).
 */
@Repository
public class CatalogRepository {

	/** Letras con tilde que se igualan a su forma simple para buscar sin distinguir acentos. */
	public static final String ACCENTS_FROM = "áàäâéèëêíìïîóòöôúùüûñ";
	public static final String ACCENTS_TO = "aaaaeeeeiiiioooouuuun";

	/** Condición de función visible sobre el alias {@code s} (screenings). */
	private static final String VISIBLE_SCREENING = """
			s.status = 'SCHEDULED' AND s.starts_at > :now
			AND EXISTS (SELECT 1 FROM rooms r WHERE r.id = s.room_id AND r.status = 'ACTIVE')""";

	/** Condición de película publicada sobre el alias {@code m} (movies). */
	private static final String PUBLISHED_MOVIE = """
			m.status = 'ACTIVE'
			AND EXISTS (SELECT 1 FROM cinemas c WHERE c.id = m.cinema_id AND c.status = 'ACTIVE')""";

	/** Asientos que se pueden reservar: libres o con un bloqueo ya vencido. */
	public static final String SEAT_IS_FREE = """
			(ss.status = 'AVAILABLE' OR (ss.status = 'HELD' AND ss.hold_expires_at <= :now))""";

	private final JdbcClient jdbc;

	public CatalogRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	public long countMovies(MovieFilter filter, Instant now) {
		Map<String, Object> params = new HashMap<>();
		String where = movieConditions(filter, now, params);
		return jdbc.sql("SELECT count(*) FROM movies m WHERE " + where)
				.params(params)
				.query(Long.class)
				.single();
	}

	public List<MovieSummary> findMovies(MovieFilter filter, Instant now, int limit, long offset) {
		Map<String, Object> params = new HashMap<>();
		String where = movieConditions(filter, now, params);
		params.put("limit", limit);
		params.put("offset", offset);
		return jdbc.sql("""
				SELECT m.id, m.title, m.genre, m.age_rating, m.duration_minutes, m.poster_url
				FROM movies m
				WHERE %s
				ORDER BY translate(lower(m.title), :accentsFrom, :accentsTo), m.id
				LIMIT :limit OFFSET :offset""".formatted(where))
				.params(params)
				.param("accentsFrom", ACCENTS_FROM)
				.param("accentsTo", ACCENTS_TO)
				.query((rs, rowNum) -> new MovieSummary(
						rs.getObject("id", UUID.class),
						rs.getString("title"),
						rs.getString("genre"),
						rs.getString("age_rating"),
						rs.getInt("duration_minutes"),
						rs.getString("poster_url")))
				.list();
	}

	public Optional<MovieDetail> findPublishedMovie(UUID movieId) {
		return jdbc.sql("""
				SELECT m.id, m.title, m.genre, m.age_rating, m.duration_minutes, m.poster_url, m.synopsis,
				       m.language, m.format, m.cast_members, m.trailer_url
				FROM movies m
				WHERE m.id = :movieId AND %s""".formatted(PUBLISHED_MOVIE))
				.param("movieId", movieId)
				.query((rs, rowNum) -> new MovieDetail(
						rs.getObject("id", UUID.class),
						rs.getString("title"),
						rs.getString("genre"),
						rs.getString("age_rating"),
						rs.getInt("duration_minutes"),
						rs.getString("poster_url"),
						rs.getString("synopsis"),
						rs.getString("language"),
						rs.getString("format"),
						rs.getString("cast_members"),
						rs.getString("trailer_url")))
				.optional();
	}

	/**
	 * Funciones visibles de la película, en orden cronológico. {@code from} y {@code to} son
	 * opcionales y acotan el rango {@code [from, to)} de inicio.
	 */
	public List<ScreeningSummary> findVisibleScreenings(UUID movieId, Instant now, Instant from, Instant to) {
		StringBuilder sql = new StringBuilder("""
				SELECT s.id, s.starts_at, r.name AS room_name, s.price, s.currency,
				       (SELECT count(*)
				        FROM screening_seats ss
				        JOIN seats se ON se.id = ss.seat_id
				        WHERE ss.screening_id = s.id AND se.status = 'ACTIVE' AND %s) AS available_seats
				FROM screenings s
				JOIN rooms r ON r.id = s.room_id
				WHERE s.movie_id = :movieId AND %s""".formatted(SEAT_IS_FREE, VISIBLE_SCREENING));
		Map<String, Object> params = new HashMap<>();
		params.put("movieId", movieId);
		params.put("now", utc(now));
		if (from != null) {
			sql.append(" AND s.starts_at >= :from");
			params.put("from", utc(from));
		}
		if (to != null) {
			sql.append(" AND s.starts_at < :to");
			params.put("to", utc(to));
		}
		sql.append(" ORDER BY s.starts_at, r.name");
		return jdbc.sql(sql.toString())
				.params(params)
				.query((rs, rowNum) -> new ScreeningSummary(
						rs.getObject("id", UUID.class),
						instant(rs, "starts_at"),
						rs.getString("room_name"),
						rs.getBigDecimal("price"),
						rs.getString("currency").strip(),
						rs.getInt("available_seats")))
				.list();
	}

	/** Arma el WHERE de la cartelera con solo los filtros presentes (sin parámetros nulos). */
	private static String movieConditions(MovieFilter filter, Instant now, Map<String, Object> params) {
		StringBuilder where = new StringBuilder(PUBLISHED_MOVIE)
				.append(" AND EXISTS (SELECT 1 FROM screenings s WHERE s.movie_id = m.id AND ")
				.append(VISIBLE_SCREENING)
				.append(')');
		params.put("now", utc(now));
		if (filter.query() != null) {
			where.append(" AND translate(lower(m.title), :accentsFrom, :accentsTo) LIKE :pattern ESCAPE '\\'");
			params.put("accentsFrom", ACCENTS_FROM);
			params.put("accentsTo", ACCENTS_TO);
			params.put("pattern", "%" + escapeLike(filter.query()) + "%");
		}
		if (filter.genre() != null) {
			where.append(" AND m.genre = :genre");
			params.put("genre", filter.genre());
		}
		if (filter.ageRating() != null) {
			where.append(" AND m.age_rating = :ageRating");
			params.put("ageRating", filter.ageRating());
		}
		return where.toString();
	}

	static String escapeLike(String text) {
		return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}
}
