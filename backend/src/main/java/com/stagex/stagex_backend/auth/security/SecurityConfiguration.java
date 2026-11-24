package com.stagex.stagex_backend.auth.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.stagex.stagex_backend.auth.application.JwtService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Autorización por rol (T-018) según la columna "Acceso" de docs/API.md. El backend es la
 * fuente de verdad: el frontend solo oculta pantallas. Las rutas que no aparecen aquí se
 * niegan por defecto. El alcance por cine se valida en los servicios con {@link CinemaScope}.
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

	private static final String API = "/api/v1";

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
			@Value("${stagex.auth.frontend-url}") String frontendUrl) throws Exception {
		http
				// API sin estado con token Bearer: no usa cookies de sesión, así que no aplica CSRF.
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource(frontendUrl)))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(basic -> basic.disable())
				.formLogin(form -> form.disable())
				.logout(logout -> logout.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/actuator/health", "/actuator/health/**", "/error").permitAll()
						// Acceso (CU-01 a CU-03)
						.requestMatchers(HttpMethod.POST, API + "/auth/register", API + "/auth/verify-email",
								API + "/auth/login", API + "/auth/refresh", API + "/auth/forgot-password",
								API + "/auth/reset-password")
						.permitAll()
						.requestMatchers(HttpMethod.POST, API + "/auth/logout").authenticated()
						// Cartelera pública
						.requestMatchers(HttpMethod.GET, API + "/movies", API + "/movies/**", API + "/screenings/**")
						.permitAll()
						// Compra: pública o autenticada; la propiedad de la orden se valida en el servicio.
						.requestMatchers(API + "/holds", API + "/holds/**").permitAll()
						.requestMatchers(HttpMethod.POST, API + "/orders").permitAll()
						.requestMatchers(HttpMethod.GET, API + "/orders/**").permitAll()
						// El webhook se autentica con su firma HMAC, no con JWT.
						.requestMatchers(HttpMethod.POST, API + "/payments/webhook").permitAll()
						// Cliente
						.requestMatchers(API + "/me/**").hasRole("CUSTOMER")
						// Taquilla
						.requestMatchers(API + "/access/**").hasRole("CASHIER")
						.requestMatchers(HttpMethod.GET, API + "/admin/sales", API + "/admin/sales/**")
						.hasAnyRole("CASHIER", "CINEMA_ADMIN")
						// Administración
						.requestMatchers(API + "/admin/**").hasRole("CINEMA_ADMIN")
						.anyRequest().denyAll())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter()))
						.authenticationEntryPoint(unauthorizedEntryPoint())
						.accessDeniedHandler(forbiddenHandler()))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(unauthorizedEntryPoint())
						.accessDeniedHandler(forbiddenHandler()))
				.headers(Customizer.withDefaults());
		return http.build();
	}

	/** Toma el rol del claim {@code role} y lo expone como {@code ROLE_<rol>}. */
	static JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName(JwtService.ROLE_CLAIM);
		authorities.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authorities);
		return converter;
	}

	/**
	 * CORS limitado al frontend configurado (docs/ARQUITECTURA.md, "Seguridad"). No se registra
	 * como bean porque Spring MVC ya expone otro {@link CorsConfigurationSource}.
	 */
	static CorsConfigurationSource corsConfigurationSource(String frontendUrl) {
		CorsConfiguration cors = new CorsConfiguration();
		cors.setAllowedOrigins(List.of(frontendUrl.replaceAll("/+$", "")));
		cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
		cors.setAllowCredentials(true);
		cors.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(API + "/**", cors);
		return source;
	}

	private static AuthenticationEntryPoint unauthorizedEntryPoint() {
		return (request, response, ex) -> writeError(response, 401, "UNAUTHORIZED",
				"Tu sesión expiró o no iniciaste sesión. Inicia sesión de nuevo.");
	}

	private static AccessDeniedHandler forbiddenHandler() {
		return (request, response, ex) -> writeError(response, 403, "FORBIDDEN",
				"No tienes permiso para realizar esta acción.");
	}

	/** Mismo formato que {@code ApiError}; los textos son fijos, así que no hace falta escaparlos. */
	private static void writeError(HttpServletResponse response, int status, String code, String message)
			throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("""
				{"timestamp":"%s","status":%d,"code":"%s","message":"%s","fieldErrors":[]}"""
				.formatted(Instant.now(), status, code, message));
	}
}
