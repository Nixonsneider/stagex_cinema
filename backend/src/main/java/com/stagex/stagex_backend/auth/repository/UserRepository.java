package com.stagex.stagex_backend.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stagex.stagex_backend.auth.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	/** El correo llega normalizado (ver {@link User#normalizeEmail(String)}). */
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
