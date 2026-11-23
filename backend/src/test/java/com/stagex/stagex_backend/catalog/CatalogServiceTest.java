package com.stagex.stagex_backend.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.stagex.stagex_backend.catalog.application.CatalogService;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieDetail;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieFilter;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieSummary;
import com.stagex.stagex_backend.catalog.application.CatalogViews.PageResult;
import com.stagex.stagex_backend.catalog.repository.CatalogRepository;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Cartelera pública sin base de datos: filtros, paginación y errores.
 */
class CatalogServiceTest {

	private static final Instant NOW = Instant.parse("2026-11-23T20:00:00Z");

	private final CatalogRepository repository = mock(CatalogRepository.class);
	private final CatalogService service = new CatalogService(repository, Clock.fixed(NOW, ZoneOffset.UTC));

	@Test
	void normalizaLaBusquedaSinTildesNiMayusculas() {
		MovieFilter expected = new MovieFilter("orbita cero", "Drama", null);
		when(repository.countMovies(expected, NOW)).thenReturn(21L);
		MovieSummary movie = new MovieSummary(UUID.randomUUID(), "Órbita Cero", "Drama", "12+", 132, null);
		when(repository.findMovies(expected, NOW, 20, 20L)).thenReturn(List.of(movie));

		PageResult<MovieSummary> page = service.searchMovies("  ÓRBITA   Cero ", " Drama ", "  ", 1);

		assertEquals(List.of(movie), page.content());
		assertEquals(1, page.page());
		assertEquals(20, page.size());
		assertEquals(21, page.totalElements());
		assertEquals(2, page.totalPages());
	}

	@Test
	void sinResultadosNoConsultaLaPagina() {
		when(repository.countMovies(any(), any())).thenReturn(0L);

		PageResult<MovieSummary> page = service.searchMovies(null, null, null, 0);

		assertTrue(page.content().isEmpty());
		assertEquals(0, page.totalPages());
		verify(repository, never()).findMovies(any(), any(), anyInt(), anyLong());
	}

	@Test
	void unaPeliculaInexistenteResponde404() {
		UUID movieId = UUID.randomUUID();
		when(repository.findPublishedMovie(movieId)).thenReturn(Optional.empty());

		assertEquals("NOT_FOUND", assertThrows(ApiException.class, () -> service.getMovie(movieId)).getCode());
		assertEquals("NOT_FOUND",
				assertThrows(ApiException.class, () -> service.getScreenings(movieId, null)).getCode());
		assertEquals("VALIDATION_ERROR",
				assertThrows(ApiException.class, () -> service.searchMovies(null, null, null, -1)).getCode());
	}

	@Test
	void elFiltroDeFechaUsaElDiaLocalDelCine() {
		UUID movieId = UUID.randomUUID();
		when(repository.findPublishedMovie(movieId)).thenReturn(Optional.of(new MovieDetail(movieId, "Faro", "Drama",
				"12+", 118, null, "Sinopsis", "es", "2D", null, null)));

		service.getScreenings(movieId, LocalDate.parse("2026-11-24"));

		// Medianoche en Bogotá (UTC-5) son las 05:00 UTC.
		verify(repository).findVisibleScreenings(eq(movieId), eq(NOW), eq(Instant.parse("2026-11-24T05:00:00Z")),
				eq(Instant.parse("2026-11-25T05:00:00Z")));
	}
}
