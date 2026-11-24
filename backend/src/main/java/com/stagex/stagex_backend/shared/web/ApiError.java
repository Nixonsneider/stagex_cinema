package com.stagex.stagex_backend.shared.web;

import java.time.Instant;
import java.util.List;

/**
 * Formato único de error de la API (docs/API.md).
 */
public record ApiError(Instant timestamp, int status, String code, String message, List<FieldError> fieldErrors) {

	public record FieldError(String field, String message) {
	}

	public static ApiError of(int status, String code, String message) {
		return new ApiError(Instant.now(), status, code, message, List.of());
	}
}
