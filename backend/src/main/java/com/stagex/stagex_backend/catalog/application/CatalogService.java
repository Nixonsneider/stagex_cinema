package com.stagex.stagex_backend.catalog.application;

import java.text.Normalizer;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieDetail;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieFilter;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieSummary;
import com.stagex.stagex_backend.catalog.application.CatalogViews.PageResult;
import com.stagex.stagex_backend.catalog.application.CatalogViews.ScreeningSummary;
import com.stagex.stagex_backend.catalog.repository.CatalogRepository;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Cartelera pública (T-033 en backend): listado con búsqueda y filtros, ficha y funciones.
 * Las reglas de publicación viven en {@link CatalogRepository}; el frontend no las repite.
 */
@Service
public class CatalogService {

	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int MAX_QUERY_LENGTH = 100;

	/** Zona del cine demo de V1.0; el filtro {@code date} se interpreta en esta hora local. */
	static final ZoneId CINEMA_ZONE = ZoneId.of("America/Bogota");

	private final CatalogRepository repository;
	private final Clock clock;

	public CatalogService(CatalogRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public PageResult<MovieSummary> searchMovies(String query, String genre, String ageRating, int page) {
		if (page < 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La página no puede ser negativa.");
		}
		String normalizedQuery = normalizeQuery(query);
		if (normalizedQuery != null && normalizedQuery.length() > MAX_QUERY_LENGTH) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La búsqueda es demasiado larga.");
		}
		MovieFilter filter = new MovieFilter(normalizedQuery, blankToNull(genre), blankToNull(ageRating));
		Instant now = clock.instant();
		long total = repository.countMovies(filter, now);
		List<MovieSummary> content = total == 0 ? List.of()
				: repository.findMovies(filter, now, DEFAULT_PAGE_SIZE, (long) page * DEFAULT_PAGE_SIZE);
		return PageResult.of(content, page, DEFAULT_PAGE_SIZE, total);
	}

	@Transactional(readOnly = true)
	public MovieDetail getMovie(UUID movieId) {
		return repository.findPublishedMovie(movieId).orElseThrow(CatalogService::movieNotFound);
	}

	/** Funciones visibles de una película publicada; {@code date} es opcional (día local del cine). */
	@Transactional(readOnly = true)
	public List<ScreeningSummary> getScreenings(UUID movieId, LocalDate date) {
		if (repository.findPublishedMovie(movieId).isEmpty()) {
			throw movieNotFound();
		}
		Instant from = date == null ? null : date.atStartOfDay(CINEMA_ZONE).toInstant();
		Instant to = date == null ? null : date.plusDays(1).atStartOfDay(CINEMA_ZONE).toInstant();
		return repository.findVisibleScreenings(movieId, clock.instant(), from, to);
	}

	/**
	 * Minúsculas y sin tildes, igual que la columna comparada en SQL
	 * ({@code translate(lower(title), ...)}).
	 */
	static String normalizeQuery(String query) {
		String value = blankToNull(query);
		if (value == null) {
			return null;
		}
		String withoutMarks = Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "");
		return withoutMarks.replaceAll("\\s+", " ");
	}

	private static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.strip();
	}

	private static ApiException movieNotFound() {
		return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Esta película no existe o ya no está en cartelera.");
	}
}
