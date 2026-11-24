package com.stagex.stagex_backend.auth.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stagex.stagex_backend.auth.domain.AccountToken;
import com.stagex.stagex_backend.auth.domain.RefreshToken;
import com.stagex.stagex_backend.auth.domain.TokenPurpose;
import com.stagex.stagex_backend.auth.domain.User;
import com.stagex.stagex_backend.auth.domain.UserStatus;
import com.stagex.stagex_backend.auth.repository.AccountTokenRepository;
import com.stagex.stagex_backend.auth.repository.RefreshTokenRepository;
import com.stagex.stagex_backend.auth.repository.UserRepository;
import com.stagex.stagex_backend.shared.mail.MailMessage;
import com.stagex.stagex_backend.shared.mail.MailService;

/**
 * Casos de uso de acceso (CU-01 a CU-03): registro con verificación de correo (T-015),
 * inicio y cierre de sesión con JWT y refresh token rotado (T-016) y recuperación de
 * contraseña (T-017).
 */
@Service
public class AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	private final UserRepository users;
	private final AccountTokenRepository accountTokens;
	private final RefreshTokenRepository refreshTokens;
	private final PasswordEncoder passwordEncoder;
	private final OpaqueTokens tokens;
	private final JwtService jwt;
	private final MailService mail;
	private final AuthProperties properties;
	private final Clock clock;
	/** Hash de referencia para gastar el mismo tiempo cuando el correo no existe. */
	private final String dummyPasswordHash;

	public AuthService(UserRepository users, AccountTokenRepository accountTokens,
			RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder, OpaqueTokens tokens,
			JwtService jwt, MailService mail, AuthProperties properties, Clock clock) {
		this.users = users;
		this.accountTokens = accountTokens;
		this.refreshTokens = refreshTokens;
		this.passwordEncoder = passwordEncoder;
		this.tokens = tokens;
		this.jwt = jwt;
		this.mail = mail;
		this.properties = properties;
		this.clock = clock;
		this.dummyPasswordHash = passwordEncoder.encode("stagex-dummy-password");
	}

	/**
	 * Crea un cliente pendiente de verificación y le envía el enlace de confirmación.
	 *
	 * @throws com.stagex.stagex_backend.shared.web.ApiException {@code 409 EMAIL_ALREADY_REGISTERED}
	 */
	@Transactional
	public User register(String name, String email, String password) {
		String normalized = User.normalizeEmail(email);
		if (users.existsByEmail(normalized)) {
			throw AuthErrors.emailAlreadyRegistered();
		}
		Instant now = clock.instant();
		User user;
		try {
			user = users.saveAndFlush(User.newCustomer(name, normalized, passwordEncoder.encode(password), now));
		}
		catch (DataIntegrityViolationException ex) {
			// Dos registros simultáneos con el mismo correo: gana el primero (índice único).
			throw AuthErrors.emailAlreadyRegistered();
		}
		String token = issueAccountToken(user, TokenPurpose.EMAIL_VERIFICATION, now);
		mail.send(new MailMessage(user.getEmail(), "Confirma tu cuenta de Stage X", """
				Hola %s,

				Para activar tu cuenta abre este enlace:
				%s

				El enlace vence en %d horas. Si no creaste esta cuenta, ignora este correo.
				""".formatted(user.getName(), properties.frontendLink("/verify-email", token),
				properties.verificationTokenTtl().toHours())));
		log.info("Cliente registrado {}", user.getId());
		return user;
	}

	/**
	 * Activa la cuenta con el enlace de verificación (un solo uso y temporal).
	 *
	 * @throws com.stagex.stagex_backend.shared.web.ApiException {@code 400 INVALID_OR_EXPIRED_TOKEN}
	 */
	@Transactional
	public void verifyEmail(String token) {
		Instant now = clock.instant();
		AccountToken accountToken = consume(token, TokenPurpose.EMAIL_VERIFICATION, now);
		User user = users.findById(accountToken.getUserId()).orElseThrow(AuthErrors::invalidOrExpiredToken);
		user.activate(now);
	}

	/**
	 * Valida las credenciales y abre una sesión. Correo inexistente y contraseña incorrecta
	 * responden igual; solo con credenciales correctas se informa que la cuenta está pendiente.
	 */
	@Transactional
	public AuthenticatedSession login(String email, String password) {
		Optional<User> found = users.findByEmail(User.normalizeEmail(email));
		if (found.isEmpty()) {
			passwordEncoder.matches(password, dummyPasswordHash);
			throw AuthErrors.invalidCredentials();
		}
		User user = found.get();
		if (!passwordEncoder.matches(password, user.getPasswordHash())) {
			throw AuthErrors.invalidCredentials();
		}
		if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
			throw AuthErrors.accountPending();
		}
		if (!user.isActive()) {
			// Una cuenta deshabilitada no revela su estado.
			throw AuthErrors.invalidCredentials();
		}
		return openSession(user, clock.instant());
	}

	/**
	 * Rota el refresh token: el usado queda revocado y se entrega uno nuevo. Si llega un
	 * token ya revocado se asume robo y se cierran todas las sesiones del usuario.
	 */
	@Transactional(noRollbackFor = com.stagex.stagex_backend.shared.web.ApiException.class)
	public AuthenticatedSession refresh(String refreshToken) {
		Instant now = clock.instant();
		RefreshToken current = refreshTokens.findByTokenHash(tokens.hash(refreshToken))
				.orElseThrow(AuthErrors::invalidRefreshToken);
		if (current.getRevokedAt() != null) {
			refreshTokens.revokeAllForUser(current.getUserId(), now);
			log.warn("Refresh token reutilizado; se cerraron las sesiones del usuario {}", current.getUserId());
			throw AuthErrors.invalidRefreshToken();
		}
		if (!current.isActive(now)) {
			throw AuthErrors.invalidRefreshToken();
		}
		User user = users.findById(current.getUserId())
				.filter(User::isActive)
				.orElseThrow(AuthErrors::invalidRefreshToken);
		current.revoke(now);
		return openSession(user, now);
	}

	/** Revoca el refresh token. Responde igual aunque el token no exista o ya esté revocado. */
	@Transactional
	public void logout(String refreshToken) {
		refreshTokens.findByTokenHash(tokens.hash(refreshToken)).ifPresent(token -> token.revoke(clock.instant()));
	}

	/**
	 * Envía un enlace temporal de recuperación si el correo pertenece a una cuenta activa o
	 * pendiente. Quien llama responde siempre lo mismo, exista o no el correo.
	 */
	@Transactional
	public void requestPasswordReset(String email) {
		Optional<User> found = users.findByEmail(User.normalizeEmail(email))
				.filter(user -> user.getStatus() != UserStatus.DISABLED);
		if (found.isEmpty()) {
			return;
		}
		User user = found.get();
		String token = issueAccountToken(user, TokenPurpose.PASSWORD_RESET, clock.instant());
		mail.send(new MailMessage(user.getEmail(), "Recupera tu contraseña de Stage X", """
				Hola %s,

				Recibimos una solicitud para cambiar tu contraseña. Para crear una nueva abre este enlace:
				%s

				El enlace vence en %d minutos y solo se puede usar una vez. Si no lo pediste, ignora este correo.
				""".formatted(user.getName(), properties.frontendLink("/reset-password", token),
				properties.passwordResetTokenTtl().toMinutes())));
	}

	/**
	 * Cambia la contraseña con el enlace de recuperación y cierra todas las sesiones abiertas.
	 * Una cuenta pendiente queda activa, porque el enlace prueba que el correo es suyo.
	 */
	@Transactional
	public void resetPassword(String token, String newPassword) {
		Instant now = clock.instant();
		AccountToken accountToken = consume(token, TokenPurpose.PASSWORD_RESET, now);
		User user = users.findById(accountToken.getUserId())
				.filter(candidate -> candidate.getStatus() != UserStatus.DISABLED)
				.orElseThrow(AuthErrors::invalidOrExpiredToken);
		user.changePassword(passwordEncoder.encode(newPassword), now);
		user.activate(now);
		refreshTokens.revokeAllForUser(user.getId(), now);
		log.info("Contraseña cambiada para el usuario {}", user.getId());
	}

	private AuthenticatedSession openSession(User user, Instant now) {
		String refreshToken = tokens.newToken();
		refreshTokens.save(new RefreshToken(user.getId(), tokens.hash(refreshToken), now, properties.refreshTokenTtl()));
		return new AuthenticatedSession(jwt.issue(user), jwt.accessTokenTtlSeconds(), refreshToken, user);
	}

	private String issueAccountToken(User user, TokenPurpose purpose, Instant now) {
		accountTokens.invalidatePending(user.getId(), purpose, now);
		String token = tokens.newToken();
		var ttl = purpose == TokenPurpose.EMAIL_VERIFICATION
				? properties.verificationTokenTtl()
				: properties.passwordResetTokenTtl();
		accountTokens.save(new AccountToken(user.getId(), purpose, tokens.hash(token), now, ttl));
		return token;
	}

	private AccountToken consume(String token, TokenPurpose purpose, Instant now) {
		AccountToken accountToken = accountTokens.findByTokenHashAndPurpose(tokens.hash(token), purpose)
				.filter(candidate -> candidate.isUsable(now))
				.orElseThrow(AuthErrors::invalidOrExpiredToken);
		accountToken.markUsed(now);
		return accountToken;
	}
}
