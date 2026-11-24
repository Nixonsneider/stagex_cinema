package com.stagex.stagex_backend.shared.mail;

import java.util.Objects;

/**
 * Correo de texto plano. Nunca debe contener contraseñas ni datos de pago.
 */
public record MailMessage(String to, String subject, String body) {

	public MailMessage {
		Objects.requireNonNull(to, "to");
		Objects.requireNonNull(subject, "subject");
		Objects.requireNonNull(body, "body");
	}
}
