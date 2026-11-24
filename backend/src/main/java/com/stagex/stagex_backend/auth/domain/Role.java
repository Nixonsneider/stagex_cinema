package com.stagex.stagex_backend.auth.domain;

/**
 * Roles de Stage X V1.0 (docs/V1.0_ALCANCE_Y_BACKLOG.md). Se guardan como texto en
 * {@code users.role}; Spring Security los expone como {@code ROLE_<nombre>}.
 */
public enum Role {
	CUSTOMER,
	CASHIER,
	CINEMA_ADMIN
}
