package com.stagex.stagex_backend.catalog.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stagex.stagex_backend.catalog.application.CatalogService;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieDetail;
import com.stagex.stagex_backend.catalog.application.CatalogViews.MovieSummary;
import com.stagex.stagex_backend.catalog.application.CatalogViews.PageResult;
import com.stagex.stagex_backend.catalog.application.CatalogViews.ScreeningSummary;
import com.stagex.stagex_backend.shared.web.RequestParams;

/**
 * Cartelera pública de docs/API.md: {@code GET /movies}, {@code GET /movies/{movieId}} y
 * {@code GET /movies/{movieId}/screenings}. Solo delega en {@link CatalogService}.
 */
@RestController
@RequestMapping("/api/v1/movies")
public class CatalogController {

	private static final String MOVIE_NOT_FOUND = "Esta película no existe o ya no está en cartelera.";

	private final CatalogService catalog;

	public CatalogController(CatalogService catalog) {
		this.catalog = catalog;
	}

	@GetMapping
	public PageResult<MovieSummary> search(@RequestParam(required = false) String query,
			@RequestParam(required = false) String genre, @RequestParam(required = false) String ageRating,
			@RequestParam(required = false) String page) {
		return catalog.searchMovies(query, genre, ageRating, RequestParams.page(page));
	}

	@GetMapping("/{movieId}")
	public MovieDetail movie(@PathVariable String movieId) {
		return catalog.getMovie(RequestParams.id(movieId, MOVIE_NOT_FOUND));
	}

	@GetMapping("/{movieId}/screenings")
	public List<ScreeningSummary> screenings(@PathVariable String movieId,
			@RequestParam(required = false) String date) {
		return catalog.getScreenings(RequestParams.id(movieId, MOVIE_NOT_FOUND), RequestParams.optionalDate(date));
	}
}
