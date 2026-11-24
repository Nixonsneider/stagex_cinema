package com.stagex.stagex_backend.booking.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * T-042: libera periódicamente los asientos cuyo bloqueo venció. La lectura del mapa y la
 * reserva ya tratan un bloqueo vencido como disponible, así que el job solo ordena la base.
 * Se puede apagar con {@code stagex.booking.expiration-job-enabled=false} (por ejemplo, en pruebas).
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@ConditionalOnProperty(name = "stagex.booking.expiration-job-enabled", havingValue = "true", matchIfMissing = true)
public class HoldExpirationJob {

	private static final Logger log = LoggerFactory.getLogger(HoldExpirationJob.class);

	private final BookingService booking;

	public HoldExpirationJob(BookingService booking) {
		this.booking = booking;
	}

	@Scheduled(initialDelayString = "${stagex.booking.expiration-interval}",
			fixedDelayString = "${stagex.booking.expiration-interval}")
	public void releaseExpiredHolds() {
		try {
			booking.releaseExpiredHolds();
		} catch (RuntimeException ex) {
			// Un fallo puntual (por ejemplo, base de datos caída) no debe detener el job.
			log.warn("No se pudieron liberar las reservas vencidas: {}", ex.getMessage());
		}
	}
}
