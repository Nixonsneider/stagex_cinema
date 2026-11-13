package com.stagex.stagex_backend.payment.gateway;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.stagex.stagex_backend.payment.domain.InvalidWebhookException;
import com.stagex.stagex_backend.payment.domain.PaymentGateway;
import com.stagex.stagex_backend.payment.domain.PaymentNotification;
import com.stagex.stagex_backend.payment.domain.PaymentRequest;
import com.stagex.stagex_backend.payment.domain.PaymentSession;

/**
 * Doble de prueba de {@link PaymentGateway}. Las pruebas de compra (T-048, T-049,
 * T-062) lo usan para controlar el resultado del pago sin depender del sandbox.
 */
public class FakePaymentGateway implements PaymentGateway {

	public static final String VALID_SIGNATURE = "fake-valid-signature";

	private final List<PaymentRequest> requests = new ArrayList<>();
	private PaymentNotification nextNotification;
	private RuntimeException nextFailure;
	private int sequence;

	@Override
	public PaymentSession createSession(PaymentRequest request) {
		if (nextFailure != null) {
			RuntimeException failure = nextFailure;
			nextFailure = null;
			throw failure;
		}
		requests.add(Objects.requireNonNull(request));
		String reference = "fake_" + (++sequence);
		return new PaymentSession(reference, URI.create("https://fake.local/checkout/" + reference),
				Instant.parse("2030-01-01T00:00:00Z"));
	}

	@Override
	public PaymentNotification parseWebhook(String rawPayload, String signature) {
		if (!VALID_SIGNATURE.equals(signature)) {
			throw new InvalidWebhookException("Firma inválida");
		}
		if (nextNotification == null) {
			throw new IllegalStateException("Configure la notificación con willNotify(...) antes de usar el fake");
		}
		return nextNotification;
	}

	/** Define la notificación que devolverá el siguiente webhook. */
	public FakePaymentGateway willNotify(PaymentNotification notification) {
		this.nextNotification = notification;
		return this;
	}

	/** Hace que la siguiente creación de sesión falle (proveedor caído). */
	public FakePaymentGateway willFailOnNextSession(RuntimeException failure) {
		this.nextFailure = failure;
		return this;
	}

	public List<PaymentRequest> requests() {
		return List.copyOf(requests);
	}
}
