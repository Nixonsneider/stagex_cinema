package com.stagex.stagex_backend.payment.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Notificación de pago ya autenticada (firma verificada) y normalizada.
 *
 * @param eventId           identificador único del evento; sirve como llave de idempotencia (T-047)
 * @param providerReference referencia de la sesión de pago
 * @param orderId           orden asociada
 * @param status            resultado del pago
 */
public record PaymentNotification(String eventId, String providerReference, UUID orderId, PaymentStatus status) {

	public PaymentNotification {
		Objects.requireNonNull(eventId, "eventId es obligatorio");
		Objects.requireNonNull(providerReference, "providerReference es obligatorio");
		Objects.requireNonNull(orderId, "orderId es obligatorio");
		Objects.requireNonNull(status, "status es obligatorio");
	}
}
