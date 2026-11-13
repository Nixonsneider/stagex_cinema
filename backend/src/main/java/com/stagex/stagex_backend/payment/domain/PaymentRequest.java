package com.stagex.stagex_backend.payment.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Datos mínimos para abrir una sesión de pago. El monto lo calcula siempre el
 * backend; nunca se toma del frontend. No incluye datos de tarjeta.
 */
public record PaymentRequest(UUID orderId, BigDecimal amount, String currency) {

	public PaymentRequest {
		Objects.requireNonNull(orderId, "orderId es obligatorio");
		Objects.requireNonNull(amount, "amount es obligatorio");
		Objects.requireNonNull(currency, "currency es obligatorio");
		if (amount.signum() <= 0) {
			throw new IllegalArgumentException("El monto del pago debe ser mayor que cero");
		}
		if (!currency.matches("[A-Z]{3}")) {
			throw new IllegalArgumentException("La moneda debe ser un código ISO 4217 de tres letras");
		}
	}
}
