package com.stagex.stagex_backend.shared.mail;

/**
 * Puerto de envío de correo (docs/ARQUITECTURA.md, "Integraciones"). La implementación
 * concreta se elige por configuración; en desarrollo se usa {@link SandboxMailService}.
 */
public interface MailService {

	void send(MailMessage message);
}
