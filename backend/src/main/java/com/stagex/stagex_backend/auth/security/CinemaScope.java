package com.stagex.stagex_backend.auth.security;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.stagex.stagex_backend.auth.domain.Role;
import com.stagex.stagex_backend.auth.repository.UserCinemaRepository;
import com.stagex.stagex_backend.shared.web.ApiException;

/**
 * Alcance por cine (RN 9 y 10 de docs/V1.0_ALCANCE_Y_BACKLOG.md): el administrador y el
 * taquillero solo operan los cines asignados en {@code user_cinemas}.
 *
 * <p>Se usa desde los servicios con {@link #requireAccess(Authentication, UUID)} o desde
 * una anotación: {@code @PreAuthorize("@cinemaScope.canAccess(authentication, #cinemaId)")}.
 */
@Component("cinemaScope")
public class CinemaScope {

	private final UserCinemaRepository userCinemas;

	public CinemaScope(UserCinemaRepository userCinemas) {
		this.userCinemas = userCinemas;
	}

	public boolean canAccess(Authentication authentication, UUID cinemaId) {
		if (cinemaId == null || authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		CurrentUser user;
		try {
			user = CurrentUser.from(authentication);
		}
		catch (RuntimeException ex) {
			return false;
		}
		if (user.role() == Role.CUSTOMER) {
			return false;
		}
		return userCinemas.isAssigned(user.id(), cinemaId);
	}

	/**
	 * @throws ApiException {@code 403 FORBIDDEN} si el cine no está asignado al usuario
	 */
	public void requireAccess(Authentication authentication, UUID cinemaId) {
		if (!canAccess(authentication, cinemaId)) {
			throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "No tienes permiso para operar este cine.");
		}
	}
}
