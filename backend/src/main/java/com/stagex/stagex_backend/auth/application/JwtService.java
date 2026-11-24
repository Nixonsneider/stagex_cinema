package com.stagex.stagex_backend.auth.application;

import java.time.Clock;
import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.stagex.stagex_backend.auth.domain.User;

/**
 * Emite el access token (JWT firmado con HS256). Contiene solo el id, el nombre y el rol:
 * nunca el correo ni datos sensibles.
 */
@Service
public class JwtService {

	public static final String ISSUER = "stagex";
	public static final String ROLE_CLAIM = "role";
	public static final String NAME_CLAIM = "name";

	private final JwtEncoder encoder;
	private final AuthProperties properties;
	private final Clock clock;

	public JwtService(JwtEncoder encoder, AuthProperties properties, Clock clock) {
		this.encoder = encoder;
		this.properties = properties;
		this.clock = clock;
	}

	public String issue(User user) {
		Instant now = clock.instant();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(user.getId().toString())
				.issuedAt(now)
				.expiresAt(now.plus(properties.accessTokenTtl()))
				.claim(ROLE_CLAIM, user.getRole().name())
				.claim(NAME_CLAIM, user.getName())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long accessTokenTtlSeconds() {
		return properties.accessTokenTtl().toSeconds();
	}
}
