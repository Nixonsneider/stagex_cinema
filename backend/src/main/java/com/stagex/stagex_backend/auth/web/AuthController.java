package com.stagex.stagex_backend.auth.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stagex.stagex_backend.auth.application.AuthService;
import com.stagex.stagex_backend.auth.application.AuthenticatedSession;
import com.stagex.stagex_backend.auth.domain.User;
import com.stagex.stagex_backend.auth.web.AuthDtos.ForgotPasswordRequest;
import com.stagex.stagex_backend.auth.web.AuthDtos.LoginRequest;
import com.stagex.stagex_backend.auth.web.AuthDtos.LoginResponse;
import com.stagex.stagex_backend.auth.web.AuthDtos.RefreshRequest;
import com.stagex.stagex_backend.auth.web.AuthDtos.RegisterRequest;
import com.stagex.stagex_backend.auth.web.AuthDtos.RegisterResponse;
import com.stagex.stagex_backend.auth.web.AuthDtos.ResetPasswordRequest;
import com.stagex.stagex_backend.auth.web.AuthDtos.SessionUser;
import com.stagex.stagex_backend.auth.web.AuthDtos.TokenRequest;

import jakarta.validation.Valid;

/**
 * Endpoints de autenticación de docs/API.md. No contienen reglas de negocio: delegan en
 * {@link AuthService}.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService auth;

	public AuthController(AuthService auth) {
		this.auth = auth;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
		User user = auth.register(request.name(), request.email(), request.password());
		return new RegisterResponse(user.getId(), user.getStatus().name());
	}

	@PostMapping("/verify-email")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void verifyEmail(@Valid @RequestBody TokenRequest request) {
		auth.verifyEmail(request.token());
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return toResponse(auth.login(request.email(), request.password()));
	}

	@PostMapping("/refresh")
	public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
		return toResponse(auth.refresh(request.refreshToken()));
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@Valid @RequestBody RefreshRequest request) {
		auth.logout(request.refreshToken());
	}

	/** Responde siempre 202, exista o no el correo (REQ-005). */
	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		auth.requestPasswordReset(request.email());
	}

	@PostMapping("/reset-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		auth.resetPassword(request.token(), request.newPassword());
	}

	private static LoginResponse toResponse(AuthenticatedSession session) {
		User user = session.user();
		return new LoginResponse(session.accessToken(), session.refreshToken(), "Bearer", session.expiresInSeconds(),
				new SessionUser(user.getId(), user.getName(), user.getRole().name()));
	}
}
