package com.stagex.stagex_backend.booking.application;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Registra {@link BookingProperties} ({@code stagex.booking.*}).
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BookingProperties.class)
public class BookingConfiguration {
}
