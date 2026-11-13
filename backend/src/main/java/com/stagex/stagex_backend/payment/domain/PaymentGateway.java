package com.stagex.stagex_backend.payment.domain;

/**
 * Puerto hacia el proveedor de pagos. La lógica de compra depende solo de esta
 * interfaz, de modo que el proveedor se sustituye por configuración
 * ({@code stagex.payment.provider}) o por un fake en las pruebas.
 */
public interface PaymentGateway {

	/**
	 * Abre una sesión de pago para la orden indicada.
	 */
	PaymentSession createSession(PaymentRequest request);

	/**
	 * Verifica la firma del webhook sobre el cuerpo crudo y lo convierte en una
	 * notificación normalizada.
	 *
	 * @param rawPayload cuerpo HTTP exactamente como llegó
	 * @param signature  valor de la cabecera de firma enviada por el proveedor
	 * @throws InvalidWebhookException si la firma no coincide o el contenido es inválido
	 */
	PaymentNotification parseWebhook(String rawPayload, String signature);
}
