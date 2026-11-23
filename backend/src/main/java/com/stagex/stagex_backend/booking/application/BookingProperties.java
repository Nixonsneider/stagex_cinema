package com.stagex.stagex_backend.booking.application;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de reservas ({@code stagex.booking.*} en application.properties).
 *
 * @param holdTtl tiempo que un asiento queda bloqueado mientras se paga (RN-02, 10 minutos por defecto)
 * @param maxSeatsPerOrder máximo de asientos por compra
 * @param expirationInterval cada cuánto el job libera los bloqueos vencidos (T-042)
 */
@ConfigurationProperties(prefix = "stagex.booking")
public record BookingProperties(Duration holdTtl, int maxSeatsPerOrder, Duration expirationInterval) {

	public BookingProperties {
		if (holdTtl == null || holdTtl.isNegative() || holdTtl.isZero()) {
			throw new IllegalStateException("stagex.booking.hold-ttl debe ser positivo");
		}
		if (maxSeatsPerOrder < 1) {
			throw new IllegalStateException("stagex.booking.max-seats-per-order debe ser al menos 1");
		}
		if (expirationInterval == null || expirationInterval.isNegative() || expirationInterval.isZero()) {
			throw new IllegalStateException("stagex.booking.expiration-interval debe ser positivo");
		}
	}
}
