package com.stagex.stagex_backend.auth.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Enlace temporal y de un solo uso enviado por correo: verificación de cuenta
 * (T-015) o recuperación de contraseña (T-017). Solo se guarda el hash del token.
 */
@Entity
@Table(name = "account_tokens")
public class AccountToken {

	@Id
	private UUID id;

	@Column(name = "user_id", nullable = false, updatable = false)
	private UUID userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private TokenPurpose purpose;

	@Column(name = "token_hash", nullable = false, updatable = false)
	private String tokenHash;

	@Column(name = "expires_at", nullable = false, updatable = false)
	private Instant expiresAt;

	@Column(name = "used_at")
	private Instant usedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected AccountToken() {
		// Requerido por JPA.
	}

	public AccountToken(UUID userId, TokenPurpose purpose, String tokenHash, Instant now, Duration ttl) {
		this.id = UUID.randomUUID();
		this.userId = Objects.requireNonNull(userId, "userId");
		this.purpose = Objects.requireNonNull(purpose, "purpose");
		this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
		this.createdAt = Objects.requireNonNull(now, "now");
		this.expiresAt = now.plus(Objects.requireNonNull(ttl, "ttl"));
	}

	/** Un token sirve una sola vez y solo antes de vencer. */
	public boolean isUsable(Instant now) {
		return usedAt == null && now.isBefore(expiresAt);
	}

	public void markUsed(Instant now) {
		if (usedAt == null) {
			usedAt = now;
		}
	}

	public UUID getId() {
		return id;
	}

	public UUID getUserId() {
		return userId;
	}

	public TokenPurpose getPurpose() {
		return purpose;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public Instant getUsedAt() {
		return usedAt;
	}
}
