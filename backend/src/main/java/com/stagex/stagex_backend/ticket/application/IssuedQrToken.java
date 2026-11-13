package com.stagex.stagex_backend.ticket.application;

import java.util.Objects;

/**
 * Token QR recién emitido.
 *
 * @param plainToken valor opaco que va dentro del QR; se entrega al cliente una sola vez y NO se persiste
 * @param tokenHash  hash SHA-256 en hexadecimal; es lo único que se guarda en {@code tickets.qr_token_hash}
 */
public record IssuedQrToken(String plainToken, String tokenHash) {

	public IssuedQrToken {
		Objects.requireNonNull(plainToken, "plainToken es obligatorio");
		Objects.requireNonNull(tokenHash, "tokenHash es obligatorio");
	}

	/** Evita que el token aparezca completo en logs. */
	@Override
	public String toString() {
		return "IssuedQrToken[plainToken=***, tokenHash=" + tokenHash + "]";
	}
}
