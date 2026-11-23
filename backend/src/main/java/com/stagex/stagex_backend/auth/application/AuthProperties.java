package com.stagex.stagex_backend.auth.application;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de autenticación ({@code stagex.auth.*} en application.properties).
 *
 * @param jwtSecret secreto HMAC del access token; mínimo 32 bytes (HS256)
 * @param accessTokenTtl vida del access token
 * @param refreshTokenTtl vida del refresh token
 * @param verificationTokenTtl vida del enlace de verificación de correo
 * @param passwordResetTokenTtl vida del enlace de recuperación de contraseña
 * @param frontendUrl URL del frontend, base de los enlaces enviados por correo
 */
@ConfigurationProperties(prefix = "stagex.auth")
public record AuthProperties(
		String jwtSecret,
		Duration accessTokenTtl,
		Duration refreshTokenTtl,
		Duration verificationTokenTtl,
		Duration passwordResetTokenTtl,
		URI frontendUrl) {

	static final int MIN_SECRET_BYTES = 32;

	public AuthProperties {
		if (jwtSecret == null || jwtSecret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
			throw new IllegalStateException(
					"stagex.auth.jwt-secret (JWT_SECRET) debe tener al menos " + MIN_SECRET_BYTES + " bytes");
		}
		requirePositive(accessTokenTtl, "access-token-ttl");
		requirePositive(refreshTokenTtl, "refresh-token-ttl");
		requirePositive(verificationTokenTtl, "verification-token-ttl");
		requirePositive(passwordResetTokenTtl, "password-reset-token-ttl");
		Objects.requireNonNull(frontendUrl, "stagex.auth.frontend-url es obligatorio");
	}

	private static void requirePositive(Duration value, String name) {
		if (value == null || value.isNegative() || value.isZero()) {
			throw new IllegalStateException("stagex.auth." + name + " debe ser positivo");
		}
	}

	/** Enlace del frontend con el token como parámetro, por ejemplo {@code /verify-email?token=...}. */
	public String frontendLink(String path, String token) {
		String base = frontendUrl.toString().replaceAll("/+$", "");
		return base + path + "?token=" + token;
	}
}
