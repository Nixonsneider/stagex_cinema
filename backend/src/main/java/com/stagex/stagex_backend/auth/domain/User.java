package com.stagex.stagex_backend.auth.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Cliente o miembro del personal (tabla {@code users}, migración V2).
 *
 * <p>El correo se guarda normalizado en minúsculas y la contraseña solo como hash
 * BCrypt. El rol de un cliente registrado siempre es {@link Role#CUSTOMER}; el
 * personal se crea por migración (docs/V1.0_ALCANCE_Y_BACKLOG.md).
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected User() {
		// Requerido por JPA.
	}

	private User(String name, String email, String passwordHash, Role role, UserStatus status, Instant now) {
		this.id = UUID.randomUUID();
		this.name = requireText(name, "name").strip();
		this.email = normalizeEmail(email);
		this.passwordHash = requireText(passwordHash, "passwordHash");
		this.role = Objects.requireNonNull(role, "role");
		this.status = Objects.requireNonNull(status, "status");
		this.createdAt = Objects.requireNonNull(now, "now");
		this.updatedAt = now;
	}

	/** Cliente recién registrado: queda pendiente hasta confirmar su correo (REQ-002). */
	public static User newCustomer(String name, String email, String passwordHash, Instant now) {
		return new User(name, email, passwordHash, Role.CUSTOMER, UserStatus.PENDING_VERIFICATION, now);
	}

	public static String normalizeEmail(String email) {
		return requireText(email, "email").strip().toLowerCase(Locale.ROOT);
	}

	public void activate(Instant now) {
		if (status == UserStatus.PENDING_VERIFICATION) {
			status = UserStatus.ACTIVE;
			updatedAt = now;
		}
	}

	public void changePassword(String newPasswordHash, Instant now) {
		this.passwordHash = requireText(newPasswordHash, "newPasswordHash");
		this.updatedAt = now;
	}

	public boolean isActive() {
		return status == UserStatus.ACTIVE;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	private static String requireText(String value, String field) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " es obligatorio");
		}
		return value;
	}
}
