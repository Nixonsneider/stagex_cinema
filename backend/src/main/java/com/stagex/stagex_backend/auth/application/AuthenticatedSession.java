package com.stagex.stagex_backend.auth.application;

import com.stagex.stagex_backend.auth.domain.User;

/**
 * Resultado de iniciar o renovar una sesión. El refresh token va en claro solo aquí,
 * para entregarlo al cliente; en la base de datos queda su hash.
 */
public record AuthenticatedSession(String accessToken, long expiresInSeconds, String refreshToken, User user) {
}
