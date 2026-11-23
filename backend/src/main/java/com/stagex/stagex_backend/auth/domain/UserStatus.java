package com.stagex.stagex_backend.auth.domain;

/**
 * Estados de una cuenta. Solo {@link #ACTIVE} puede iniciar sesión.
 */
public enum UserStatus {
	PENDING_VERIFICATION,
	ACTIVE,
	DISABLED
}
