package com.stagex.stagex_backend.auth.web;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cuerpos de petición y respuesta de {@code /api/v1/auth} (docs/API.md, "Payloads críticos").
 */
public final class AuthDtos {

	/** Mínimo 8 caracteres con letras y números; máximo 72 bytes por el límite de BCrypt. */
	static final String PASSWORD_PATTERN = "^(?=.*\\p{L})(?=.*\\d).{8,72}$";
	static final String PASSWORD_MESSAGE = "La contraseña debe tener entre 8 y 72 caracteres, con letras y números.";

	private AuthDtos() {
	}

	public record RegisterRequest(
			@NotBlank(message = "Escribe tu nombre.") @Size(max = 120, message = "El nombre es demasiado largo.") String name,
			@NotBlank(message = "Escribe tu correo electrónico.") @Email(message = "Escribe un correo válido.")
			@Size(max = 254, message = "El correo es demasiado largo.") String email,
			@NotBlank(message = "Escribe una contraseña.") @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE)
			String password) {
	}

	public record RegisterResponse(UUID userId, String status) {
	}

	public record TokenRequest(@NotBlank(message = "El token es obligatorio.") @Size(max = 200) String token) {
	}

	public record LoginRequest(
			@NotBlank(message = "Escribe tu correo electrónico.") @Size(max = 254) String email,
			@NotBlank(message = "Escribe tu contraseña.") @Size(max = 200) String password) {
	}

	public record RefreshRequest(
			@NotBlank(message = "El refresh token es obligatorio.") @Size(max = 200) String refreshToken) {
	}

	public record ForgotPasswordRequest(
			@NotBlank(message = "Escribe tu correo electrónico.") @Email(message = "Escribe un correo válido.")
			@Size(max = 254) String email) {
	}

	public record ResetPasswordRequest(
			@NotBlank(message = "El token es obligatorio.") @Size(max = 200) String token,
			@NotBlank(message = "Escribe una contraseña.") @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE)
			String newPassword) {
	}

	public record SessionUser(UUID id, String name, String role) {
	}

	public record LoginResponse(String accessToken, String refreshToken, String tokenType, long expiresIn,
			SessionUser user) {
	}
}
