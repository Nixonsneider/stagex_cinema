package com.stagex.stagex_backend.ticket.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * Genera y procesa los tokens QR de las entradas (T-050).
 *
 * <ul>
 * <li>El token tiene 256 bits de {@link SecureRandom}: no es predecible ni contiene datos personales.</li>
 * <li>Se codifica en Base64 URL sin relleno (43 caracteres), apto para QR y URLs.</li>
 * <li>Solo se persiste su hash SHA-256; para validar se calcula el hash del token leído y se busca por él.</li>
 * </ul>
 */
@Service
public class QrTokenService {

	static final int TOKEN_BYTES = 32;
	static final int TOKEN_LENGTH = 43;
	private static final Pattern TOKEN_FORMAT = Pattern.compile("[A-Za-z0-9_-]{" + TOKEN_LENGTH + "}");

	private final SecureRandom random;

	public QrTokenService() {
		this(new SecureRandom());
	}

	QrTokenService(SecureRandom random) {
		this.random = Objects.requireNonNull(random, "random es obligatorio");
	}

	/**
	 * Emite un token nuevo para una entrada.
	 */
	public IssuedQrToken issue() {
		byte[] bytes = new byte[TOKEN_BYTES];
		random.nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		return new IssuedQrToken(token, hash(token));
	}

	/**
	 * Calcula el hash con el que se busca la entrada al validar un QR.
	 *
	 * @throws IllegalArgumentException si el token no tiene el formato esperado
	 */
	public String hash(String token) {
		if (!isWellFormed(token)) {
			throw new IllegalArgumentException("Token QR con formato inválido");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.US_ASCII)));
		}
		catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 no está disponible", ex);
		}
	}

	/**
	 * Permite rechazar de inmediato lecturas que no pueden ser un token emitido por Stage X,
	 * sin consultar la base de datos.
	 */
	public boolean isWellFormed(String token) {
		return token != null && TOKEN_FORMAT.matcher(token).matches();
	}
}
