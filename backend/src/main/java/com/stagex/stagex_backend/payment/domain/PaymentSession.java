package com.stagex.stagex_backend.payment.domain;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

/**
 * Sesión de pago creada en el proveedor.
 *
 * @param providerReference referencia única del proveedor; se guarda en {@code orders.payment_reference}
 * @param paymentUrl        URL a la que el frontend redirige al cliente
 * @param expiresAt         instante a partir del cual la sesión deja de ser válida
 */
public record PaymentSession(String providerReference, URI paymentUrl, Instant expiresAt) {

	public PaymentSession {
		Objects.requireNonNull(providerReference, "providerReference es obligatorio");
		Objects.requireNonNull(paymentUrl, "paymentUrl es obligatorio");
		Objects.requireNonNull(expiresAt, "expiresAt es obligatorio");
	}
}
