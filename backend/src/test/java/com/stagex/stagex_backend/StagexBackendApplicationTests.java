package com.stagex.stagex_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.stagex.stagex_backend.auth.repository.AccountTokenRepository;
import com.stagex.stagex_backend.auth.repository.RefreshTokenRepository;
import com.stagex.stagex_backend.auth.repository.UserCinemaRepository;
import com.stagex.stagex_backend.auth.repository.UserRepository;

/**
 * Verifica que el contexto arranca sin PostgreSQL. Los repositorios se simulan porque la
 * prueba excluye la configuración de base de datos y JPA.
 */
@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration,"
				+ "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
class StagexBackendApplicationTests {

	@MockitoBean
	UserRepository userRepository;

	@MockitoBean
	AccountTokenRepository accountTokenRepository;

	@MockitoBean
	RefreshTokenRepository refreshTokenRepository;

	@MockitoBean
	UserCinemaRepository userCinemaRepository;

	@Test
	void contextLoads() {
	}

}
