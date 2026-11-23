package com.stagex.stagex_backend.auth.application;

import org.springframework.http.HttpStatus;

import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Errores de autenticación con los códigos de docs/API.md. Los mensajes no revelan si
 * un correo existe (REQ-005).
 */
public final class AuthErrors {

	private AuthErrors() {
	}

	public static ApiException invalidCredentials() {
		return new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Correo o contraseña incorrectos.");
	}

	public static ApiException accountPending() {
		return new ApiException(HttpStatus.FORBIDDEN, "ACCOUNT_PENDING_VERIFICATION",
				"Tu cuenta aún no está verificada. Revisa tu correo y abre el enlace de confirmación.");
	}

	public static ApiException emailAlreadyRegistered() {
		return new ApiException(HttpStatus.CONFLICT, "EMAIL_ALREADY_REGISTERED",
				"Ya existe una cuenta con este correo. Inicia sesión o recupera tu contraseña.");
	}

	public static ApiException invalidOrExpiredToken() {
		return new ApiException(HttpStatus.BAD_REQUEST, "INVALID_OR_EXPIRED_TOKEN",
				"El enlace no es válido o ya venció. Solicita uno nuevo.");
	}

	public static ApiException invalidRefreshToken() {
		return new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Tu sesión expiró. Inicia sesión de nuevo.");
	}
}
