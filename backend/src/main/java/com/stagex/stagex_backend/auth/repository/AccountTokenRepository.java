package com.stagex.stagex_backend.auth.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stagex.stagex_backend.auth.domain.AccountToken;
import com.stagex.stagex_backend.auth.domain.TokenPurpose;

public interface AccountTokenRepository extends JpaRepository<AccountToken, UUID> {

	Optional<AccountToken> findByTokenHashAndPurpose(String tokenHash, TokenPurpose purpose);

	/** Invalida los enlaces pendientes anteriores cuando se envía uno nuevo. */
	@Modifying
	@Query("""
			update AccountToken t set t.usedAt = :now
			where t.userId = :userId and t.purpose = :purpose and t.usedAt is null
			""")
	int invalidatePending(@Param("userId") UUID userId, @Param("purpose") TokenPurpose purpose,
			@Param("now") Instant now);
}
