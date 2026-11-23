package com.stagex.stagex_backend.auth.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Refresh token opaco de una sesión (T-016). Se rota en cada uso y se puede
 * revocar; solo se guarda su hash.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	private UUID id;

	@Column(name = "user_id", nullable = false, updatable = false)
	private UUID userId;

	@Column(name = "token_hash", nullable = false, updatable = false)
	private String tokenHash;

	@Column(name = "expires_at", nullable = false, updatable = false)
	private Instant expiresAt;

	@Column(name = "revoked_at")
	private Instant revokedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected RefreshToken() {
		// Requerido por JPA.
	}

	public RefreshToken(UUID userId, String tokenHash, Instant now, Duration ttl) {
		this.id = UUID.randomUUID();
		this.userId = Objects.requireNonNull(userId, "userId");
		this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
		this.createdAt = Objects.requireNonNull(now, "now");
		this.expiresAt = now.plus(Objects.requireNonNull(ttl, "ttl"));
	}

	public boolean isActive(Instant now) {
		return revokedAt == null && now.isBefore(expiresAt);
	}

	public void revoke(Instant now) {
		if (revokedAt == null) {
			revokedAt = now;
		}
	}

	public UUID getId() {
		return id;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getRevokedAt() {
		return revokedAt;
	}
}
