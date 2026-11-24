package com.stagex.stagex_backend.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.stagex.stagex_backend.auth.domain.AccountToken;
import com.stagex.stagex_backend.auth.domain.RefreshToken;
import com.stagex.stagex_backend.auth.domain.Role;
import com.stagex.stagex_backend.auth.domain.TokenPurpose;
import com.stagex.stagex_backend.auth.domain.User;
import com.stagex.stagex_backend.auth.domain.UserStatus;
import com.stagex.stagex_backend.auth.repository.AccountTokenRepository;
import com.stagex.stagex_backend.auth.repository.RefreshTokenRepository;
import com.stagex.stagex_backend.auth.repository.UserRepository;
import com.stagex.stagex_backend.shared.mail.MailMessage;
import com.stagex.stagex_backend.shared.mail.SandboxMailService;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Reglas de T-015, T-016 y T-017 sin base de datos: los repositorios son almacenes en memoria.
 */
class AuthServiceTest {

	private static final String SECRET = "secreto-de-prueba-con-mas-de-32-bytes!!";
	private static final Pattern TOKEN_IN_LINK = Pattern.compile("token=([A-Za-z0-9_-]+)");

	private final MutableClock clock = new MutableClock(Instant.parse("2026-11-23T15:00:00Z"));
	private final Map<UUID, User> userStore = new HashMap<>();
	private final List<AccountToken> accountTokenStore = new ArrayList<>();
	private final List<RefreshToken> refreshTokenStore = new ArrayList<>();
	private final SandboxMailService mail = new SandboxMailService();
	private final OpaqueTokens tokens = new OpaqueTokens();
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
	private final AuthProperties properties = new AuthProperties(SECRET, Duration.ofMinutes(15), Duration.ofDays(7),
			Duration.ofDays(1), Duration.ofHours(1), URI.create("http://localhost:5173/"));
	private final NimbusJwtDecoder decoder = NimbusJwtDecoder
			.withSecretKey(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
			.macAlgorithm(MacAlgorithm.HS256)
			.build();

	private AuthService service;

	@BeforeEach
	void setUp() {
		UserRepository users = mock(UserRepository.class);
		when(users.existsByEmail(anyString()))
				.thenAnswer(inv -> userStore.values().stream().anyMatch(u -> u.getEmail().equals(inv.getArgument(0))));
		when(users.findByEmail(anyString())).thenAnswer(inv -> userStore.values().stream()
				.filter(u -> u.getEmail().equals(inv.getArgument(0))).findFirst());
		when(users.findById(any())).thenAnswer(inv -> Optional.ofNullable(userStore.get(inv.getArgument(0))));
		when(users.saveAndFlush(any())).thenAnswer(inv -> {
			User user = inv.getArgument(0);
			userStore.put(user.getId(), user);
			return user;
		});

		AccountTokenRepository accountTokens = mock(AccountTokenRepository.class);
		when(accountTokens.save(any())).thenAnswer(inv -> {
			accountTokenStore.add(inv.getArgument(0));
			return inv.getArgument(0);
		});
		when(accountTokens.findByTokenHashAndPurpose(anyString(), any())).thenAnswer(inv -> accountTokenStore.stream()
				.filter(t -> t.getTokenHash().equals(inv.getArgument(0)) && t.getPurpose() == inv.getArgument(1))
				.findFirst());
		when(accountTokens.invalidatePending(any(), any(), any())).thenAnswer(inv -> {
			int count = 0;
			for (AccountToken token : accountTokenStore) {
				if (token.getUserId().equals(inv.getArgument(0)) && token.getPurpose() == inv.getArgument(1)
						&& token.getUsedAt() == null) {
					token.markUsed(inv.getArgument(2));
					count++;
				}
			}
			return count;
		});

		RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
		when(refreshTokens.save(any())).thenAnswer(inv -> {
			refreshTokenStore.add(inv.getArgument(0));
			return inv.getArgument(0);
		});
		when(refreshTokens.findByTokenHash(anyString())).thenAnswer(inv -> refreshTokenStore.stream()
				.filter(t -> t.getTokenHash().equals(inv.getArgument(0))).findFirst());
		when(refreshTokens.revokeAllForUser(any(), any())).thenAnswer(inv -> {
			int count = 0;
			for (RefreshToken token : refreshTokenStore) {
				if (token.getUserId().equals(inv.getArgument(0)) && token.getRevokedAt() == null) {
					token.revoke(inv.getArgument(1));
					count++;
				}
			}
			return count;
		});

		NimbusJwtEncoder encoder = NimbusJwtEncoder
				.withSecretKey(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
				.algorithm(MacAlgorithm.HS256)
				.build();
		JwtService jwt = new JwtService(encoder, properties, clock);
		service = new AuthService(users, accountTokens, refreshTokens, passwordEncoder, tokens, jwt, mail, properties,
				clock);
	}

	@Test
	void registraClientePendienteConCorreoNormalizadoYEnlaceDeVerificacion() {
		User user = service.register("  Ana Pérez ", " Ana@Correo.COM ", "Segura2026");

		assertEquals("ana@correo.com", user.getEmail());
		assertEquals("Ana Pérez", user.getName());
		assertEquals(Role.CUSTOMER, user.getRole());
		assertEquals(UserStatus.PENDING_VERIFICATION, user.getStatus());
		assertNotEquals("Segura2026", user.getPasswordHash());
		assertTrue(passwordEncoder.matches("Segura2026", user.getPasswordHash()));

		MailMessage message = lastMail();
		assertEquals("ana@correo.com", message.to());
		assertTrue(message.body().contains("http://localhost:5173/verify-email?token="));
		String token = tokenFrom(message);
		AccountToken stored = accountTokenStore.getFirst();
		assertEquals(TokenPurpose.EMAIL_VERIFICATION, stored.getPurpose());
		assertNotEquals(token, stored.getTokenHash());
		assertEquals(tokens.hash(token), stored.getTokenHash());
	}

	@Test
	void rechazaCorreoYaRegistradoSinImportarMayusculas() {
		service.register("Ana", "ana@correo.com", "Segura2026");

		ApiException error = assertThrows(ApiException.class,
				() -> service.register("Otra", "ANA@correo.com", "Segura2026"));

		assertEquals(HttpStatus.CONFLICT, error.getStatus());
		assertEquals("EMAIL_ALREADY_REGISTERED", error.getCode());
	}

	@Test
	void verificaElCorreoUnaSolaVez() {
		User user = service.register("Ana", "ana@correo.com", "Segura2026");
		String token = tokenFrom(lastMail());

		service.verifyEmail(token);

		assertEquals(UserStatus.ACTIVE, user.getStatus());
		assertEquals("INVALID_OR_EXPIRED_TOKEN",
				assertThrows(ApiException.class, () -> service.verifyEmail(token)).getCode());
		assertEquals("INVALID_OR_EXPIRED_TOKEN",
				assertThrows(ApiException.class, () -> service.verifyEmail("token-inexistente")).getCode());
	}

	@Test
	void rechazaEnlaceDeVerificacionVencido() {
		User user = service.register("Ana", "ana@correo.com", "Segura2026");
		String token = tokenFrom(lastMail());

		clock.advance(Duration.ofDays(1));

		assertEquals("INVALID_OR_EXPIRED_TOKEN",
				assertThrows(ApiException.class, () -> service.verifyEmail(token)).getCode());
		assertEquals(UserStatus.PENDING_VERIFICATION, user.getStatus());
	}

	@Test
	void iniciaSesionConJwtDeCortaDuracionYRefreshTokenHasheado() {
		User user = activeUser("ana@correo.com", "Segura2026");

		AuthenticatedSession session = service.login(" ANA@correo.com", "Segura2026");

		assertEquals(900, session.expiresInSeconds());
		assertEquals(user.getId(), session.user().getId());
		Jwt jwt = decodeAt(session.accessToken(), clock.instant());
		assertEquals(user.getId().toString(), jwt.getSubject());
		assertEquals("CUSTOMER", jwt.getClaimAsString(JwtService.ROLE_CLAIM));
		assertEquals("stagex", jwt.getClaimAsString("iss"));
		assertNull(jwt.getClaims().get("email"));
		assertEquals(Instant.parse("2026-11-23T15:15:00Z"), jwt.getExpiresAt());

		RefreshToken stored = refreshTokenStore.getFirst();
		assertEquals(tokens.hash(session.refreshToken()), stored.getTokenHash());
		assertNotEquals(session.refreshToken(), stored.getTokenHash());
	}

	@Test
	void credencialesIncorrectasNoRevelanSiExisteElCorreo() {
		activeUser("ana@correo.com", "Segura2026");

		ApiException wrongPassword = assertThrows(ApiException.class, () -> service.login("ana@correo.com", "Otra2026"));
		ApiException unknownEmail = assertThrows(ApiException.class, () -> service.login("nadie@correo.com", "Segura2026"));

		assertEquals(HttpStatus.UNAUTHORIZED, wrongPassword.getStatus());
		assertEquals("INVALID_CREDENTIALS", wrongPassword.getCode());
		assertEquals(wrongPassword.getCode(), unknownEmail.getCode());
		assertEquals(wrongPassword.getMessage(), unknownEmail.getMessage());
		assertTrue(refreshTokenStore.isEmpty());
	}

	@Test
	void cuentaPendienteSoloSeInformaConCredencialesCorrectas() {
		service.register("Ana", "ana@correo.com", "Segura2026");

		assertEquals("INVALID_CREDENTIALS",
				assertThrows(ApiException.class, () -> service.login("ana@correo.com", "Otra2026")).getCode());
		ApiException pending = assertThrows(ApiException.class, () -> service.login("ana@correo.com", "Segura2026"));
		assertEquals(HttpStatus.FORBIDDEN, pending.getStatus());
		assertEquals("ACCOUNT_PENDING_VERIFICATION", pending.getCode());
	}

	@Test
	void cuentaDeshabilitadaNoPuedeEntrar() {
		User user = activeUser("ana@correo.com", "Segura2026");
		ReflectionTestUtils.setField(user, "status", UserStatus.DISABLED);

		assertEquals("INVALID_CREDENTIALS",
				assertThrows(ApiException.class, () -> service.login("ana@correo.com", "Segura2026")).getCode());
	}

	@Test
	void renovarRotaElRefreshTokenYRevocaElAnterior() {
		activeUser("ana@correo.com", "Segura2026");
		AuthenticatedSession first = service.login("ana@correo.com", "Segura2026");

		AuthenticatedSession second = service.refresh(first.refreshToken());

		assertNotEquals(first.refreshToken(), second.refreshToken());
		assertNotNull(refreshTokenStore.get(0).getRevokedAt());
		assertNull(refreshTokenStore.get(1).getRevokedAt());
		assertNotNull(decodeAt(second.accessToken(), clock.instant()).getSubject());
	}

	@Test
	void reutilizarUnRefreshTokenRevocadoCierraTodasLasSesiones() {
		activeUser("ana@correo.com", "Segura2026");
		AuthenticatedSession first = service.login("ana@correo.com", "Segura2026");
		AuthenticatedSession rotated = service.refresh(first.refreshToken());

		assertEquals("UNAUTHORIZED",
				assertThrows(ApiException.class, () -> service.refresh(first.refreshToken())).getCode());
		assertEquals("UNAUTHORIZED",
				assertThrows(ApiException.class, () -> service.refresh(rotated.refreshToken())).getCode());
		assertTrue(refreshTokenStore.stream().allMatch(token -> token.getRevokedAt() != null));
	}

	@Test
	void refreshTokenVencidoOInexistenteNoRenueva() {
		activeUser("ana@correo.com", "Segura2026");
		AuthenticatedSession session = service.login("ana@correo.com", "Segura2026");

		assertEquals("UNAUTHORIZED", assertThrows(ApiException.class, () -> service.refresh("no-existe")).getCode());
		clock.advance(Duration.ofDays(7));
		assertEquals("UNAUTHORIZED",
				assertThrows(ApiException.class, () -> service.refresh(session.refreshToken())).getCode());
	}

	@Test
	void cerrarSesionRevocaElRefreshTokenYNoFallaSiNoExiste() {
		activeUser("ana@correo.com", "Segura2026");
		AuthenticatedSession session = service.login("ana@correo.com", "Segura2026");

		service.logout(session.refreshToken());
		service.logout(session.refreshToken());
		service.logout("no-existe");

		assertNotNull(refreshTokenStore.getFirst().getRevokedAt());
		assertThrows(ApiException.class, () -> service.refresh(session.refreshToken()));
	}

	@Test
	void recuperarContrasenaNoEnviaNadaSiElCorreoNoExiste() {
		service.requestPasswordReset("nadie@correo.com");

		assertTrue(mail.sent().isEmpty());
		assertTrue(accountTokenStore.isEmpty());
	}

	@Test
	void cambiaLaContrasenaConEnlaceDeUnSoloUsoYCierraLasSesiones() {
		User user = activeUser("ana@correo.com", "Segura2026");
		AuthenticatedSession session = service.login("ana@correo.com", "Segura2026");

		service.requestPasswordReset("ANA@correo.com");
		MailMessage message = lastMail();
		assertTrue(message.body().contains("http://localhost:5173/reset-password?token="));
		String token = tokenFrom(message);

		service.resetPassword(token, "Nueva2026");

		assertTrue(passwordEncoder.matches("Nueva2026", user.getPasswordHash()));
		assertNotNull(refreshTokenStore.getFirst().getRevokedAt());
		assertThrows(ApiException.class, () -> service.refresh(session.refreshToken()));
		assertEquals("INVALID_OR_EXPIRED_TOKEN",
				assertThrows(ApiException.class, () -> service.resetPassword(token, "Otra2026")).getCode());
		assertEquals("INVALID_CREDENTIALS",
				assertThrows(ApiException.class, () -> service.login("ana@correo.com", "Segura2026")).getCode());
		assertNotNull(service.login("ana@correo.com", "Nueva2026").accessToken());
	}

	@Test
	void unNuevoEnlaceDeRecuperacionInvalidaElAnterior() {
		activeUser("ana@correo.com", "Segura2026");
		service.requestPasswordReset("ana@correo.com");
		String first = tokenFrom(lastMail());
		service.requestPasswordReset("ana@correo.com");
		String second = tokenFrom(lastMail());

		assertThrows(ApiException.class, () -> service.resetPassword(first, "Nueva2026"));
		service.resetPassword(second, "Nueva2026");
	}

	@Test
	void enlaceDeRecuperacionVenceEnUnaHora() {
		activeUser("ana@correo.com", "Segura2026");
		service.requestPasswordReset("ana@correo.com");
		String token = tokenFrom(lastMail());

		clock.advance(Duration.ofHours(1));

		assertThrows(ApiException.class, () -> service.resetPassword(token, "Nueva2026"));
	}

	@Test
	void elEnlaceDeVerificacionNoSirveParaCambiarLaContrasena() {
		service.register("Ana", "ana@correo.com", "Segura2026");
		String verificationToken = tokenFrom(lastMail());

		assertEquals("INVALID_OR_EXPIRED_TOKEN",
				assertThrows(ApiException.class, () -> service.resetPassword(verificationToken, "Nueva2026")).getCode());
		assertFalse(passwordEncoder.matches("Nueva2026", userStore.values().iterator().next().getPasswordHash()));
	}

	private User activeUser(String email, String password) {
		User user = service.register("Ana", email, password);
		service.verifyEmail(tokenFrom(lastMail()));
		mail.clear();
		return user;
	}

	private MailMessage lastMail() {
		List<MailMessage> sent = mail.sent();
		assertFalse(sent.isEmpty(), "Se esperaba un correo");
		return sent.getLast();
	}

	private static String tokenFrom(MailMessage message) {
		Matcher matcher = TOKEN_IN_LINK.matcher(message.body());
		assertTrue(matcher.find(), "El correo no contiene un enlace con token");
		return matcher.group(1);
	}

	private Jwt decodeAt(String token, Instant at) {
		// El decoder valida la expiración con el reloj del sistema; los tokens de prueba se emiten
		// con un reloj fijo, así que se valida la firma y se comprueban las fechas aparte.
		NimbusJwtDecoder lenient = decoder;
		lenient.setJwtValidator(jwt -> org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success());
		Jwt jwt = lenient.decode(token);
		assertTrue(jwt.getIssuedAt().equals(at));
		return jwt;
	}

	/** Reloj controlable para probar vencimientos. */
	static final class MutableClock extends Clock {

		private Instant now;

		MutableClock(Instant start) {
			this.now = start;
		}

		void advance(Duration duration) {
			now = now.plus(duration);
		}

		@Override
		public java.time.ZoneId getZone() {
			return ZoneOffset.UTC;
		}

		@Override
		public Clock withZone(java.time.ZoneId zone) {
			return this;
		}

		@Override
		public Instant instant() {
			return now;
		}
	}
}
