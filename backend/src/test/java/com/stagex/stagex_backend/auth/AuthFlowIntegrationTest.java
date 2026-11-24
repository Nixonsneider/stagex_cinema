package com.stagex.stagex_backend.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;

import com.stagex.stagex_backend.auth.application.AuthService;
import com.stagex.stagex_backend.auth.application.AuthenticatedSession;
import com.stagex.stagex_backend.auth.domain.User;
import com.stagex.stagex_backend.shared.mail.MailMessage;
import com.stagex.stagex_backend.shared.mail.SandboxMailService;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Flujo completo de acceso contra PostgreSQL (V6 y mapeo JPA validado por Hibernate).
 * Se ejecuta solo si {@code STAGEX_TEST_DATABASE_URL} apunta a una base desechable.
 */
@SpringBootTest(properties = "spring.datasource.url=${STAGEX_TEST_DATABASE_URL}")
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "STAGEX_TEST_DATABASE_URL", matches = ".+")
class AuthFlowIntegrationTest {

	private static final Pattern TOKEN_IN_LINK = Pattern.compile("token=([A-Za-z0-9_-]+)");

	@Autowired
	private AuthService auth;

	@Autowired
	private SandboxMailService mail;

	@Autowired
	private JdbcClient jdbc;

	@Test
	void registroVerificacionSesionYRecuperacion() {
		String email = UUID.randomUUID() + "@Example.test";
		User user = auth.register("Ana", email, "Segura2026");
		assertEquals(email.toLowerCase(java.util.Locale.ROOT), user.getEmail());
		assertEquals("ACCOUNT_PENDING_VERIFICATION",
				assertThrows(ApiException.class, () -> auth.login(email, "Segura2026")).getCode());

		auth.verifyEmail(lastToken(user.getEmail()));
		AuthenticatedSession session = auth.login(email, "Segura2026");
		AuthenticatedSession renewed = auth.refresh(session.refreshToken());
		assertNotEquals(session.refreshToken(), renewed.refreshToken());
		assertThrows(ApiException.class, () -> auth.refresh(session.refreshToken()));

		AuthenticatedSession again = auth.login(email, "Segura2026");
		auth.requestPasswordReset(email);
		auth.resetPassword(lastToken(user.getEmail()), "Nueva2026");
		assertThrows(ApiException.class, () -> auth.refresh(again.refreshToken()));
		assertEquals("INVALID_CREDENTIALS",
				assertThrows(ApiException.class, () -> auth.login(email, "Segura2026")).getCode());
		auth.logout(auth.login(email, "Nueva2026").refreshToken());

		// En la base solo quedan hashes: ningún token en claro.
		List<String> hashes = jdbc.sql("""
				SELECT token_hash FROM account_tokens WHERE user_id = ?
				UNION ALL SELECT token_hash FROM refresh_tokens WHERE user_id = ?
				""").param(user.getId()).param(user.getId()).query(String.class).list();
		assertTrue(hashes.size() >= 5);
		assertTrue(hashes.stream().allMatch(hash -> hash.matches("[0-9a-f]{64}")));
		String passwordHash = jdbc.sql("SELECT password_hash FROM users WHERE id = ?").param(user.getId())
				.query(String.class).single();
		assertTrue(passwordHash.startsWith("$2"));
	}

	@Test
	void correoDuplicadoSeRechazaAunqueCambienLasMayusculas() {
		String email = UUID.randomUUID() + "@example.test";
		auth.register("Ana", email, "Segura2026");

		assertEquals("EMAIL_ALREADY_REGISTERED",
				assertThrows(ApiException.class, () -> auth.register("Otra", email.toUpperCase(java.util.Locale.ROOT),
						"Segura2026")).getCode());
	}

	private String lastToken(String email) {
		MailMessage message = mail.sent().stream()
				.filter(candidate -> candidate.to().equals(email))
				.reduce((first, second) -> second)
				.orElseThrow();
		Matcher matcher = TOKEN_IN_LINK.matcher(message.body());
		assertTrue(matcher.find());
		return matcher.group(1);
	}
}
