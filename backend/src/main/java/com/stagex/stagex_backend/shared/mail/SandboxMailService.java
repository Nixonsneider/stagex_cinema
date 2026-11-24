package com.stagex.stagex_backend.shared.mail;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Correo simulado: no envía nada. Guarda en memoria los últimos mensajes para pruebas
 * y registra solo el destinatario enmascarado y el asunto. El cuerpo puede llevar
 * enlaces con tokens, así que nunca se escribe en el log.
 */
public class SandboxMailService implements MailService {

	private static final Logger log = LoggerFactory.getLogger(SandboxMailService.class);
	private static final int CAPACITY = 50;

	private final Deque<MailMessage> outbox = new ArrayDeque<>();

	@Override
	public synchronized void send(MailMessage message) {
		if (outbox.size() == CAPACITY) {
			outbox.removeFirst();
		}
		outbox.addLast(message);
		log.info("Correo sandbox a {}: {}", mask(message.to()), message.subject());
	}

	/** Mensajes enviados, del más antiguo al más reciente. */
	public synchronized List<MailMessage> sent() {
		return List.copyOf(outbox);
	}

	public synchronized void clear() {
		outbox.clear();
	}

	static String mask(String email) {
		int at = email.indexOf('@');
		if (at <= 1) {
			return "***" + (at >= 0 ? email.substring(at) : "");
		}
		return email.charAt(0) + "***" + email.substring(at);
	}
}
