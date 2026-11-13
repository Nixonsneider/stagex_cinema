package com.stagex.stagex_backend.payment.gateway;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stagex.stagex_backend.payment.domain.PaymentGateway;
import com.stagex.stagex_backend.payment.gateway.sandbox.SandboxPaymentGateway;

/**
 * Selecciona la implementación de {@link PaymentGateway} según
 * {@code stagex.payment.provider}. En V1.0 solo existe {@code sandbox}.
 */
@Configuration(proxyBeanMethods = false)
public class PaymentGatewayConfiguration {

	@Bean
	@ConditionalOnProperty(name = "stagex.payment.provider", havingValue = "sandbox", matchIfMissing = true)
	PaymentGateway sandboxPaymentGateway(
			@Value("${stagex.payment.sandbox.secret}") String secret,
			@Value("${stagex.payment.sandbox.checkout-base-url}") URI checkoutBaseUrl,
			@Value("${stagex.payment.sandbox.session-ttl:PT10M}") Duration sessionTtl) {
		return new SandboxPaymentGateway(secret, checkoutBaseUrl, sessionTtl, Clock.systemUTC());
	}
}
