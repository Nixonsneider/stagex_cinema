package com.stagex.stagex_backend.payment.gateway.sandbox;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.stagex.stagex_backend.payment.domain.InvalidWebhookException;
import com.stagex.stagex_backend.payment.domain.PaymentGateway;
import com.stagex.stagex_backend.payment.domain.PaymentNotification;
import com.stagex.stagex_backend.payment.domain.PaymentRequest;
import com.stagex.stagex_backend.payment.domain.PaymentSession;
import com.stagex.stagex_backend.payment.domain.PaymentStatus;

/**
 * Proveedor de pagos simulado para desarrollo y demostración.
 *
 * <p>No cobra dinero ni recibe datos de tarjeta. Genera una referencia
 * {@code sbx_<uuid>} y una URL de checkout simulada. El webhook se autentica con
 * HMAC-SHA256 del cuerpo crudo usando el secreto configurado.
 *
 * <p>Formato del cuerpo del webhook ({@code application/x-www-form-urlencoded}):
 * {@code eventId=...&reference=...&orderId=...&status=APPROVED|REJECTED}.
 * Cabecera de firma: {@value #SIGNATURE_HEADER} con el HMAC en hexadecimal.
 */
public class SandboxPaymentGateway implements PaymentGateway {

	public static final String SIGNATURE_HEADER = "X-Sandbox-Signature";
	static final String REFERENCE_PREFIX = "sbx_";
	private static final String HMAC_ALGORITHM = "HmacSHA256";

	private final byte[] secret;
	private final URI checkoutBaseUrl;
	private final Duration sessionTtl;
	private final Clock clock;

	public SandboxPaymentGateway(String secret, URI checkoutBaseUrl, Duration sessionTtl, Clock clock) {
		if (secret == null || secret.isBlank()) {
			throw new IllegalStateException("stagex.payment.sandbox.secret es obligatorio para el proveedor sandbox");
		}
		Objects.requireNonNull(checkoutBaseUrl, "checkoutBaseUrl es obligatorio");
		Objects.requireNonNull(sessionTtl, "sessionTtl es obligatorio");
		if (sessionTtl.isNegative() || sessionTtl.isZero()) {
			throw new IllegalArgumentException("sessionTtl debe ser positivo");
		}
		this.secret = secret.getBytes(StandardCharsets.UTF_8);
		this.checkoutBaseUrl = checkoutBaseUrl;
		this.sessionTtl = sessionTtl;
		this.clock = Objects.requireNonNull(clock, "clock es obligatorio");
	}

	@Override
	public PaymentSession createSession(PaymentRequest request) {
		Objects.requireNonNull(request, "request es obligatorio");
		String reference = REFERENCE_PREFIX + UUID.randomUUID();
		String base = checkoutBaseUrl.toString().replaceAll("/+$", "");
		URI paymentUrl = URI.create(base + "/checkout/" + reference);
		return new PaymentSession(reference, paymentUrl, clock.instant().plus(sessionTtl));
	}

	@Override
	public PaymentNotification parseWebhook(String rawPayload, String signature) {
		if (rawPayload == null || rawPayload.isBlank() || signature == null || signature.isBlank()) {
			throw new InvalidWebhookException("Webhook sin cuerpo o sin firma");
		}
		byte[] provided;
		try {
			provided = HexFormat.of().parseHex(signature.trim().toLowerCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidWebhookException("Firma con formato inválido");
		}
		// Comparación en tiempo constante para no filtrar información por tiempos de respuesta.
		if (!MessageDigest.isEqual(hmac(rawPayload), provided)) {
			throw new InvalidWebhookException("Firma de webhook inválida");
		}

		Map<String, String> fields = parseForm(rawPayload);
		String eventId = required(fields, "eventId");
		String reference = required(fields, "reference");
		if (!reference.startsWith(REFERENCE_PREFIX)) {
			throw new InvalidWebhookException("Referencia de pago desconocida");
		}
		UUID orderId;
		PaymentStatus status;
		try {
			orderId = UUID.fromString(required(fields, "orderId"));
			status = PaymentStatus.valueOf(required(fields, "status"));
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidWebhookException("Contenido de webhook inválido");
		}
		return new PaymentNotification(eventId, reference, orderId, status);
	}

	/**
	 * Firma un cuerpo de webhook. Lo usan las pruebas y la simulación del checkout
	 * sandbox para producir notificaciones válidas.
	 */
	public String sign(String rawPayload) {
		return HexFormat.of().formatHex(hmac(Objects.requireNonNull(rawPayload, "rawPayload es obligatorio")));
	}

	/**
	 * Construye el cuerpo de un webhook sandbox con el formato esperado por
	 * {@link #parseWebhook(String, String)}.
	 */
	public static String buildWebhookPayload(String eventId, String reference, UUID orderId, PaymentStatus status) {
		return "eventId=" + encode(eventId)
				+ "&reference=" + encode(reference)
				+ "&orderId=" + encode(orderId.toString())
				+ "&status=" + encode(status.name());
	}

	private byte[] hmac(String payload) {
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
			return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		}
		catch (NoSuchAlgorithmException | InvalidKeyException ex) {
			throw new IllegalStateException("No fue posible calcular la firma HMAC", ex);
		}
	}

	private static Map<String, String> parseForm(String payload) {
		Map<String, String> fields = new HashMap<>();
		for (String pair : payload.split("&")) {
			int separator = pair.indexOf('=');
			if (separator <= 0) {
				throw new InvalidWebhookException("Contenido de webhook inválido");
			}
			String key = URLDecoder.decode(pair.substring(0, separator), StandardCharsets.UTF_8);
			String value = URLDecoder.decode(pair.substring(separator + 1), StandardCharsets.UTF_8);
			if (fields.putIfAbsent(key, value) != null) {
				throw new InvalidWebhookException("Campo de webhook repetido");
			}
		}
		return fields;
	}

	private static String required(Map<String, String> fields, String name) {
		String value = fields.get(name);
		if (value == null || value.isBlank()) {
			throw new InvalidWebhookException("Falta un campo obligatorio del webhook");
		}
		return value;
	}

	private static String encode(String value) {
		return URLEncoder.encode(Objects.requireNonNull(value), StandardCharsets.UTF_8);
	}
}
