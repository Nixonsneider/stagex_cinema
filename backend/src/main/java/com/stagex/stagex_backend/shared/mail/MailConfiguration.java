package com.stagex.stagex_backend.shared.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Selecciona el proveedor de correo según {@code stagex.mail.provider}. En V1.0 solo
 * existe {@code sandbox}; un proveedor real se agrega aquí sin tocar la lógica de negocio.
 */
@Configuration(proxyBeanMethods = false)
public class MailConfiguration {

	@Bean
	@ConditionalOnProperty(name = "stagex.mail.provider", havingValue = "sandbox", matchIfMissing = true)
	SandboxMailService sandboxMailService() {
		return new SandboxMailService();
	}
}
