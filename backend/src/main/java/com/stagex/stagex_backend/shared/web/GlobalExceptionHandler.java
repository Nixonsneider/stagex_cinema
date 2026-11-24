package com.stagex.stagex_backend.shared.web;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Traduce las excepciones al formato de error de docs/API.md. Nunca devuelve trazas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	ResponseEntity<ApiError> handleApi(ApiException ex) {
		return ResponseEntity.status(ex.getStatus())
				.body(ApiError.of(ex.getStatus().value(), ex.getCode(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		List<ApiError.FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
				.toList();
		ApiError body = new ApiError(Instant.now(), 400, "VALIDATION_ERROR", "Revisa los datos enviados.", fields);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
				.body(ApiError.of(400, "VALIDATION_ERROR", "El cuerpo de la petición no es válido."));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	ResponseEntity<ApiError> handleMethod(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(ApiError.of(405, "METHOD_NOT_ALLOWED", "Método no permitido."));
	}

	/** Rechazos de {@code @PreAuthorize}, por ejemplo un cine que no pertenece al administrador. */
	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ApiError> handleDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiError.of(403, "FORBIDDEN", "No tienes permiso para realizar esta acción."));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	ResponseEntity<ApiError> handleNotFound(NoResourceFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiError.of(404, "NOT_FOUND", "No encontramos lo que buscas."));
	}

	/** Errores de Spring MVC que ya traen su código HTTP (tipo de contenido, parámetros, etc.). */
	@ExceptionHandler(ErrorResponseException.class)
	ResponseEntity<ApiError> handleSpring(ErrorResponseException ex) {
		int status = ex.getStatusCode().value();
		String code = status == 404 ? "NOT_FOUND" : "HTTP_" + status;
		return ResponseEntity.status(status).body(ApiError.of(status, code, "No se pudo procesar la petición."));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> handleUnexpected(Exception ex) {
		// Errores de Spring MVC con código propio (tipo de contenido, parámetro faltante, etc.).
		if (ex instanceof ErrorResponse errorResponse && errorResponse.getStatusCode().value() < 500) {
			int status = errorResponse.getStatusCode().value();
			return ResponseEntity.status(status).body(ApiError.of(status, "HTTP_" + status,
					"No se pudo procesar la petición."));
		}
		log.error("Error no controlado", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiError.of(500, "INTERNAL_ERROR", "Ocurrió un problema. Intenta de nuevo en unos minutos."));
	}
}
