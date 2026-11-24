package com.stagex.stagex_backend.auth.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

import org.springframework.stereotype.Component;

/**
 * Tokens opacos para enlaces de correo y refresh tokens: 256 bits de {@link SecureRandom}
 * en Base64 URL sin relleno. Solo se persiste su hash SHA-256 en hexadecimal; el token en
 * claro viaja al cliente y nunca se registra en logs.
 */
@Component
public class OpaqueTokens {

	private static final int TOKEN_BYTES = 32;

	private final SecureRandom random;

	public OpaqueTokens() {
		this(new SecureRandom());
	}

	OpaqueTokens(SecureRandom random) {
		this.random = Objects.requireNonNull(random, "random");
	}

	public String newToken() {
		byte[] bytes = new byte[TOKEN_BYTES];
		random.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public String hash(String token) {
		Objects.requireNonNull(token, "token");
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
		}
		catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 no está disponible", ex);
		}
	}
}
