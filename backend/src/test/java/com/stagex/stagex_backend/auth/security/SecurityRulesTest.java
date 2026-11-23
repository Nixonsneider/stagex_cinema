package com.stagex.stagex_backend.auth.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.stagex.stagex_backend.auth.application.AuthErrors;
import com.stagex.stagex_backend.auth.application.AuthService;
import com.stagex.stagex_backend.auth.application.AuthenticatedSession;
import com.stagex.stagex_backend.auth.application.JwtService;
import com.stagex.stagex_backend.auth.domain.Role;
import com.stagex.stagex_backend.auth.domain.User;
import com.stagex.stagex_backend.auth.repository.AccountTokenRepository;
import com.stagex.stagex_backend.auth.repository.RefreshTokenRepository;
import com.stagex.stagex_backend.auth.repository.UserCinemaRepository;
import com.stagex.stagex_backend.auth.repository.UserRepository;

/**
 * T-018: cada ruta de docs/API.md solo responde al rol que corresponde. Una respuesta 404
 * significa que la petición pasó la seguridad y no existe todavía el controlador.
 */
@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration,"
				+ "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
		"stagex.auth.frontend-url=http://localhost:5173"
})
class SecurityRulesTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private JwtEncoder jwtEncoder;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private AccountTokenRepository accountTokenRepository;

	@MockitoBean
	private RefreshTokenRepository refreshTokenRepository;

	@MockitoBean
	private UserCinemaRepository userCinemaRepository;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	void lasRutasDeAccesoSonPublicas() throws Exception {
		User user = user(Role.CUSTOMER);
		when(authService.login(anyString(), anyString()))
				.thenReturn(new AuthenticatedSession("jwt", 900, "refresh", user));

		mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"ana@correo.com\",\"password\":\"Segura2026\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value("jwt"))
				.andExpect(jsonPath("$.refreshToken").value("refresh"))
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.expiresIn").value(900))
				.andExpect(jsonPath("$.user.role").value("CUSTOMER"))
				.andExpect(jsonPath("$.user.name").value("Ana"));
	}

	@Test
	void credencialesInvalidasUsanElFormatoDeError() throws Exception {
		when(authService.login(anyString(), anyString())).thenThrow(AuthErrors.invalidCredentials());

		mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"ana@correo.com\",\"password\":\"x\"}"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
				.andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos."))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void validaElRegistroAntesDeLlegarAlServicio() throws Exception {
		mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"\",\"email\":\"no-es-correo\",\"password\":\"corta\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.fieldErrors.length()").value(3));
	}

	@Test
	void registroExitosoResponde201ConEstadoPendiente() throws Exception {
		User user = user(Role.CUSTOMER);
		when(authService.register(anyString(), anyString(), anyString())).thenReturn(user);

		mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ana\",\"email\":\"ana@correo.com\",\"password\":\"Segura2026\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.userId").value(user.getId().toString()))
				.andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));
	}

	@Test
	void recuperarContrasenaSiempreResponde202() throws Exception {
		mvc.perform(post("/api/v1/auth/forgot-password").contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"nadie@correo.com\"}"))
				.andExpect(status().isAccepted());
	}

	@Test
	void cerrarSesionExigeAccessToken() throws Exception {
		String body = "{\"refreshToken\":\"refresh\"}";
		mvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
		mvc.perform(post("/api/v1/auth/logout").header(HttpHeaders.AUTHORIZATION, bearer(Role.CUSTOMER))
				.contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isNoContent());
	}

	@Test
	void laCarteleraEsPublica() throws Exception {
		mvc.perform(get("/api/v1/movies")).andExpect(status().isNotFound());
		mvc.perform(get("/api/v1/screenings/" + UUID.randomUUID() + "/seats")).andExpect(status().isNotFound());
	}

	@Test
	void lasRutasAdministrativasExigenRolDeAdministrador() throws Exception {
		mvc.perform(get("/api/v1/admin/movies"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
		mvc.perform(get("/api/v1/admin/movies").header(HttpHeaders.AUTHORIZATION, bearer(Role.CUSTOMER)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("FORBIDDEN"));
		mvc.perform(get("/api/v1/admin/movies").header(HttpHeaders.AUTHORIZATION, bearer(Role.CASHIER)))
				.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/admin/movies").header(HttpHeaders.AUTHORIZATION, bearer(Role.CINEMA_ADMIN)))
				.andExpect(status().isNotFound());
	}

	@Test
	void lasVentasSonParaTaquilleroYAdministrador() throws Exception {
		mvc.perform(get("/api/v1/admin/sales").header(HttpHeaders.AUTHORIZATION, bearer(Role.CASHIER)))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/v1/admin/sales").header(HttpHeaders.AUTHORIZATION, bearer(Role.CINEMA_ADMIN)))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/v1/admin/sales").header(HttpHeaders.AUTHORIZATION, bearer(Role.CUSTOMER)))
				.andExpect(status().isForbidden());
		// El taquillero consulta ventas pero no modifica el catálogo.
		mvc.perform(post("/api/v1/admin/movies").header(HttpHeaders.AUTHORIZATION, bearer(Role.CASHIER)))
				.andExpect(status().isForbidden());
	}

	@Test
	void laValidacionDeEntradasEsSoloParaTaquillero() throws Exception {
		mvc.perform(post("/api/v1/access/tickets/validate").header(HttpHeaders.AUTHORIZATION, bearer(Role.CASHIER)))
				.andExpect(status().isNotFound());
		mvc.perform(post("/api/v1/access/tickets/validate").header(HttpHeaders.AUTHORIZATION, bearer(Role.CUSTOMER)))
				.andExpect(status().isForbidden());
		mvc.perform(post("/api/v1/access/tickets/validate")
				.header(HttpHeaders.AUTHORIZATION, bearer(Role.CINEMA_ADMIN)))
				.andExpect(status().isForbidden());
	}

	@Test
	void misEntradasSonSoloDelCliente() throws Exception {
		mvc.perform(get("/api/v1/me/tickets").header(HttpHeaders.AUTHORIZATION, bearer(Role.CUSTOMER)))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/v1/me/tickets").header(HttpHeaders.AUTHORIZATION, bearer(Role.CINEMA_ADMIN)))
				.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/me/tickets")).andExpect(status().isUnauthorized());
	}

	@Test
	void rechazaTokensInvalidosVencidosODeOtroEmisor() throws Exception {
		mvc.perform(get("/api/v1/me/tickets").header(HttpHeaders.AUTHORIZATION, "Bearer no-es-un-jwt"))
				.andExpect(status().isUnauthorized());

		Instant past = Instant.now().minusSeconds(3600);
		mvc.perform(get("/api/v1/me/tickets")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token("stagex", past, past.plusSeconds(60))))
				.andExpect(status().isUnauthorized());

		Instant now = Instant.now();
		mvc.perform(get("/api/v1/me/tickets")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token("otro-emisor", now, now.plusSeconds(600))))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void lasRutasNoDeclaradasSeNieganPorDefecto() throws Exception {
		mvc.perform(get("/api/v1/ruta-desconocida")).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/v1/ruta-desconocida").header(HttpHeaders.AUTHORIZATION, bearer(Role.CINEMA_ADMIN)))
				.andExpect(status().isForbidden());
	}

	@Test
	void corsSoloPermiteElFrontendConfigurado() throws Exception {
		mvc.perform(options("/api/v1/auth/login")
				.header(HttpHeaders.ORIGIN, "http://localhost:5173")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
		mvc.perform(options("/api/v1/auth/login")
				.header(HttpHeaders.ORIGIN, "https://sitio-malicioso.example")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
				.andExpect(status().isForbidden());
	}

	@Test
	void elAlcancePorCineSoloAceptaCinesAsignados() {
		CinemaScope scope = context.getBean(CinemaScope.class);
		UUID admin = UUID.randomUUID();
		UUID assigned = UUID.randomUUID();
		UUID other = UUID.randomUUID();
		when(userCinemaRepository.isAssigned(eq(admin), eq(assigned))).thenReturn(true);
		when(userCinemaRepository.isAssigned(eq(admin), eq(other))).thenReturn(false);

		var adminAuth = authentication(admin, Role.CINEMA_ADMIN);
		org.junit.jupiter.api.Assertions.assertTrue(scope.canAccess(adminAuth, assigned));
		org.junit.jupiter.api.Assertions.assertFalse(scope.canAccess(adminAuth, other));
		org.junit.jupiter.api.Assertions.assertFalse(scope.canAccess(authentication(admin, Role.CUSTOMER), assigned));
		org.junit.jupiter.api.Assertions.assertFalse(scope.canAccess(null, assigned));
		org.junit.jupiter.api.Assertions.assertThrows(com.stagex.stagex_backend.shared.web.ApiException.class,
				() -> scope.requireAccess(adminAuth, other));
	}

	private String bearer(Role role) {
		return "Bearer " + jwtService.issue(user(role));
	}

	private String token(String issuer, Instant issuedAt, Instant expiresAt) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.subject(UUID.randomUUID().toString())
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.claim(JwtService.ROLE_CLAIM, "CUSTOMER")
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
				.getTokenValue();
	}

	private org.springframework.security.core.Authentication authentication(UUID userId, Role role) {
		var jwt = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("token")
				.header("alg", "HS256")
				.subject(userId.toString())
				.claim(JwtService.ROLE_CLAIM, role.name())
				.build();
		return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt,
				java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
						"ROLE_" + role.name())));
	}

	private static User user(Role role) {
		User user = User.newCustomer("Ana", UUID.randomUUID() + "@correo.com", "hash", Instant.now());
		ReflectionTestUtils.setField(user, "role", role);
		return user;
	}
}
