package com.stagex.stagex_backend.catalog.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Vistas de solo lectura de la cartelera pública (docs/API.md, "Catálogo y funciones").
 * Los nombres de los campos son los del contrato JSON que consume el frontend.
 */
public final class CatalogViews {

	private CatalogViews() {
	}

	/** Película en el listado de {@code GET /movies}. */
	public record MovieSummary(UUID id, String title, String genre, String ageRating, int durationMinutes,
			String posterUrl) {
	}

	/** Ficha de {@code GET /movies/{movieId}}. */
	public record MovieDetail(UUID id, String title, String genre, String ageRating, int durationMinutes,
			String posterUrl, String synopsis, String language, String format, String castMembers,
			String trailerUrl) {
	}

	/** Función de {@code GET /movies/{movieId}/screenings}; el precio lo define el backend. */
	public record ScreeningSummary(UUID id, Instant startsAt, String roomName, BigDecimal price, String currency,
			int availableSeats) {
	}

	/** Filtros de la cartelera. Los valores vacíos se tratan como ausentes. */
	public record MovieFilter(String query, String genre, String ageRating) {
	}

	/** Página de resultados con el formato de los listados paginados de docs/API.md. */
	public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

		public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
			int totalPages = size == 0 ? 0 : (int) ((totalElements + size - 1) / size);
			return new PageResult<>(List.copyOf(content), page, size, totalElements, totalPages);
		}
	}
}
