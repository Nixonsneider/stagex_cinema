package com.stagex.stagex_backend.ticket.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class QrTokenServiceTest {

	private final QrTokenService service = new QrTokenService();

	@Test
	void emiteTokenOpacoDe256Bits() {
		IssuedQrToken issued = service.issue();

		assertEquals(QrTokenService.TOKEN_LENGTH, issued.plainToken().length());
		assertTrue(service.isWellFormed(issued.plainToken()));
		assertTrue(issued.plainToken().matches("[A-Za-z0-9_-]+"), "Debe ser Base64 URL sin relleno");
	}

	@Test
	void losTokensNoSeRepiten() {
		Set<String> tokens = new HashSet<>();
		for (int i = 0; i < 10_000; i++) {
			tokens.add(service.issue().plainToken());
		}
		assertEquals(10_000, tokens.size());
	}

	@Test
	void guardaSoloElHashYEsDeterministico() {
		IssuedQrToken issued = service.issue();

		assertNotEquals(issued.plainToken(), issued.tokenHash());
		assertEquals(64, issued.tokenHash().length());
		assertTrue(issued.tokenHash().matches("[0-9a-f]{64}"));
		assertEquals(issued.tokenHash(), service.hash(issued.plainToken()), "Al validar se debe encontrar el mismo hash");
	}

	@Test
	void tokensDistintosProducenHashesDistintos() {
		assertNotEquals(service.issue().tokenHash(), service.issue().tokenHash());
	}

	@Test
	void rechazaTokensMalFormados() {
		assertFalse(service.isWellFormed(null));
		assertFalse(service.isWellFormed(""));
		assertFalse(service.isWellFormed("corto"));
		assertFalse(service.isWellFormed("a".repeat(42) + "="));
		assertFalse(service.isWellFormed("a".repeat(44)));
		assertThrows(IllegalArgumentException.class, () -> service.hash("<script>"));
	}

	@Test
	void noExponeElTokenEnLogs() {
		IssuedQrToken issued = service.issue();

		assertFalse(issued.toString().contains(issued.plainToken()));
	}
}
