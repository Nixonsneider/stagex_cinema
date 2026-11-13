package com.stagex.stagex_backend.payment.domain;

/**
 * El webhook no tiene una firma válida o su contenido está mal formado. El
 * controlador debe responder {@code 400}/{@code 401} sin revelar el motivo exacto.
 */
public class InvalidWebhookException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidWebhookException(String message) {
		super(message);
	}
}
