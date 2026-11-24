package com.stagex.stagex_backend.auth.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import com.stagex.stagex_backend.auth.application.JwtService;
import com.stagex.stagex_backend.auth.domain.Role;

/**
 * Usuario autenticado tomado del access token validado.
 */
public record CurrentUser(UUID id, Role role) {

	public static CurrentUser from(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
			throw new IllegalStateException("La petición no tiene un access token válido");
		}
		return new CurrentUser(UUID.fromString(jwt.getSubject()),
				Role.valueOf(jwt.getClaimAsString(JwtService.ROLE_CLAIM)));
	}
}
