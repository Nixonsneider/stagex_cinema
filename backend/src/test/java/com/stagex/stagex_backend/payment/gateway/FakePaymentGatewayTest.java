package com.stagex.stagex_backend.payment.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.stagex.stagex_backend.payment.domain.InvalidWebhookException;
import com.stagex.stagex_backend.payment.domain.PaymentGateway;
import com.stagex.stagex_backend.payment.domain.PaymentNotification;
import com.stagex.stagex_backend.payment.domain.PaymentRequest;
import com.stagex.stagex_backend.payment.domain.PaymentStatus;

/**
 * Demuestra que el proveedor se sustituye por un fake a través del puerto
 * {@link PaymentGateway} (criterio de aceptación de T-046).
 */
class FakePaymentGatewayTest {

	private final FakePaymentGateway fake = new FakePaymentGateway();
	private final PaymentGateway gateway = fake;

	@Test
	void registraLasSolicitudesDePago() {
		PaymentRequest request = new PaymentRequest(UUID.randomUUID(), new BigDecimal("24000"), "COP");

		gateway.createSession(request);

		assertEquals(1, fake.requests().size());
		assertSame(request, fake.requests().getFirst());
	}

	@Test
	void devuelveLaNotificacionConfigurada() {
		PaymentNotification rejected = new PaymentNotification("evt-1", "fake_1", UUID.randomUUID(), PaymentStatus.REJECTED);
		fake.willNotify(rejected);

		assertSame(rejected, gateway.parseWebhook("{}", FakePaymentGateway.VALID_SIGNATURE));
	}

	@Test
	void rechazaFirmaInvalida() {
		assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook("{}", "otra"));
	}

	@Test
	void simulaProveedorCaido() {
		fake.willFailOnNextSession(new IllegalStateException("timeout"));
		PaymentRequest request = new PaymentRequest(UUID.randomUUID(), BigDecimal.TEN, "COP");

		assertThrows(IllegalStateException.class, () -> gateway.createSession(request));
	}
}
