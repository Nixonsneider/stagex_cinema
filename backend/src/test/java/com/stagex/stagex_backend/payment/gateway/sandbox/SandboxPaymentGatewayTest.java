package com.stagex.stagex_backend.payment.gateway.sandbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.stagex.stagex_backend.payment.domain.InvalidWebhookException;
import com.stagex.stagex_backend.payment.domain.PaymentNotification;
import com.stagex.stagex_backend.payment.domain.PaymentRequest;
import com.stagex.stagex_backend.payment.domain.PaymentSession;
import com.stagex.stagex_backend.payment.domain.PaymentStatus;

class SandboxPaymentGatewayTest {

	private static final Instant NOW = Instant.parse("2026-11-12T20:00:00Z");
	private static final UUID ORDER_ID = UUID.fromString("7d3f1b8e-1f7a-4b8e-9a51-2a3c4d5e6f70");

	private final SandboxPaymentGateway gateway = new SandboxPaymentGateway("secreto-de-prueba",
			URI.create("http://localhost:8080/sandbox/"), Duration.ofMinutes(10), Clock.fixed(NOW, ZoneOffset.UTC));

	@Test
	void creaSesionConReferenciaUnicaYVencimiento() {
		PaymentRequest request = new PaymentRequest(ORDER_ID, new BigDecimal("24000"), "COP");

		PaymentSession first = gateway.createSession(request);
		PaymentSession second = gateway.createSession(request);

		assertTrue(first.providerReference().startsWith("sbx_"));
		assertNotEquals(first.providerReference(), second.providerReference());
		assertEquals(URI.create("http://localhost:8080/sandbox/checkout/" + first.providerReference()), first.paymentUrl());
		assertEquals(NOW.plus(Duration.ofMinutes(10)), first.expiresAt());
	}

	@Test
	void aceptaWebhookAprobadoConFirmaValida() {
		String payload = SandboxPaymentGateway.buildWebhookPayload("evt-1", "sbx_abc", ORDER_ID, PaymentStatus.APPROVED);

		PaymentNotification notification = gateway.parseWebhook(payload, gateway.sign(payload));

		assertEquals("evt-1", notification.eventId());
		assertEquals("sbx_abc", notification.providerReference());
		assertEquals(ORDER_ID, notification.orderId());
		assertEquals(PaymentStatus.APPROVED, notification.status());
	}

	@Test
	void aceptaWebhookRechazado() {
		String payload = SandboxPaymentGateway.buildWebhookPayload("evt-2", "sbx_abc", ORDER_ID, PaymentStatus.REJECTED);

		assertEquals(PaymentStatus.REJECTED, gateway.parseWebhook(payload, gateway.sign(payload)).status());
	}

	@Test
	void laFirmaNoDistingueMayusculas() {
		String payload = SandboxPaymentGateway.buildWebhookPayload("evt-3", "sbx_abc", ORDER_ID, PaymentStatus.APPROVED);

		assertEquals("evt-3", gateway.parseWebhook(payload, gateway.sign(payload).toUpperCase()).eventId());
	}

	@Test
	void rechazaCuerpoAlteradoDespuesDeFirmar() {
		String original = SandboxPaymentGateway.buildWebhookPayload("evt-4", "sbx_abc", ORDER_ID, PaymentStatus.REJECTED);
		String tampered = original.replace("REJECTED", "APPROVED");

		assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook(tampered, gateway.sign(original)));
	}

	@Test
	void rechazaFirmaDeOtroSecreto() {
		SandboxPaymentGateway other = new SandboxPaymentGateway("otro-secreto", URI.create("http://localhost"),
				Duration.ofMinutes(10), Clock.systemUTC());
		String payload = SandboxPaymentGateway.buildWebhookPayload("evt-5", "sbx_abc", ORDER_ID, PaymentStatus.APPROVED);

		assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook(payload, other.sign(payload)));
	}

	@Test
	void rechazaFirmaAusenteOConFormatoInvalido() {
		String payload = SandboxPaymentGateway.buildWebhookPayload("evt-6", "sbx_abc", ORDER_ID, PaymentStatus.APPROVED);

		assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook(payload, null));
		assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook(payload, " "));
		assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook(payload, "no-es-hex"));
	}

	@Test
	void rechazaContenidoInvalidoAunqueEsteFirmado() {
		String[] payloads = {
				"eventId=evt-7&reference=sbx_abc&orderId=no-uuid&status=APPROVED",
				"eventId=evt-7&reference=sbx_abc&orderId=" + ORDER_ID + "&status=PENDING",
				"eventId=evt-7&reference=otro_abc&orderId=" + ORDER_ID + "&status=APPROVED",
				"eventId=evt-7&orderId=" + ORDER_ID + "&status=APPROVED",
				"eventId=evt-7&eventId=evt-8&reference=sbx_abc&orderId=" + ORDER_ID + "&status=APPROVED",
				"basura" };

		for (String payload : payloads) {
			assertThrows(InvalidWebhookException.class, () -> gateway.parseWebhook(payload, gateway.sign(payload)), payload);
		}
	}

	@Test
	void exigeSecretoConfigurado() {
		assertThrows(IllegalStateException.class, () -> new SandboxPaymentGateway(" ", URI.create("http://localhost"),
				Duration.ofMinutes(10), Clock.systemUTC()));
	}

	@Test
	void solicitudDePagoValidaMontoYMoneda() {
		assertThrows(IllegalArgumentException.class, () -> new PaymentRequest(ORDER_ID, BigDecimal.ZERO, "COP"));
		assertThrows(IllegalArgumentException.class, () -> new PaymentRequest(ORDER_ID, new BigDecimal("-1"), "COP"));
		assertThrows(IllegalArgumentException.class, () -> new PaymentRequest(ORDER_ID, BigDecimal.TEN, "cop"));
	}
}
