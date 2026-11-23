package com.stagex.stagex_backend.shared.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import org.springframework.http.HttpStatus;

/**
 * Lectura de parámetros de ruta y consulta con errores en el formato de docs/API.md.
 * Un identificador mal formado responde 404 (no existe) en lugar de un error interno.
 */
public final class RequestParams {

	private RequestParams() {
	}

	public static UUID id(String value, String notFoundMessage) {
		try {
			return UUID.fromString(value);
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", notFoundMessage);
		}
	}

	public static LocalDate optionalDate(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(value.strip());
		} catch (DateTimeParseException ex) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
					"La fecha debe tener el formato AAAA-MM-DD.");
		}
	}

	public static int page(String value) {
		if (value == null || value.isBlank()) {
			return 0;
		}
		try {
			int page = Integer.parseInt(value.strip());
			if (page >= 0) {
				return page;
			}
		} catch (NumberFormatException ex) {
			// Se responde con el mismo error de validación.
		}
		throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La página debe ser un número desde 0.");
	}
}
