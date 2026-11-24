package com.stagex.stagex_backend.shared.web;

import java.util.Objects;

import org.springframework.http.HttpStatus;

/**
 * Error de negocio con el código y mensaje que recibe el cliente (docs/API.md, "Errores").
 * El mensaje debe ser apto para mostrarse al usuario: sin trazas ni datos internos.
 */
public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus status;
	private final String code;

	public ApiException(HttpStatus status, String code, String message) {
		super(message);
		this.status = Objects.requireNonNull(status, "status");
		this.code = Objects.requireNonNull(code, "code");
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}
}
