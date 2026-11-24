package com.stagex.stagex_backend.booking.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stagex.stagex_backend.auth.security.CurrentUser;
import com.stagex.stagex_backend.booking.application.BookingService;
import com.stagex.stagex_backend.booking.application.BookingViews.Hold;
import com.stagex.stagex_backend.booking.application.BookingViews.ScreeningSeats;
import com.stagex.stagex_backend.shared.web.RequestParams;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Disponibilidad y reservas de docs/API.md: {@code GET /screenings/{screeningId}/seats},
 * {@code POST /holds} y {@code DELETE /holds/{holdId}}. Solo delega en {@link BookingService}.
 */
@RestController
@RequestMapping("/api/v1")
public class BookingController {

	/** Cuerpo de {@code POST /holds}. {@code guestEmail} solo se usa si no hay sesión. */
	public record HoldRequest(
			@NotNull(message = "Indica la función.") UUID screeningId,
			@NotEmpty(message = "Elige al menos un asiento.") List<@NotNull UUID> screeningSeatIds,
			@Email(message = "Escribe un correo válido.") @Size(max = 254) String guestEmail) {
	}

	private final BookingService booking;

	public BookingController(BookingService booking) {
		this.booking = booking;
	}

	@GetMapping("/screenings/{screeningId}/seats")
	public ScreeningSeats seats(@PathVariable String screeningId) {
		return booking.getSeatMap(RequestParams.id(screeningId, "Esta función ya no está disponible."));
	}

	@PostMapping("/holds")
	@ResponseStatus(HttpStatus.CREATED)
	public Hold hold(@Valid @RequestBody HoldRequest request, Authentication authentication) {
		return booking.createHold(request.screeningId(), request.screeningSeatIds(), userId(authentication),
				request.guestEmail());
	}

	@DeleteMapping("/holds/{holdId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void release(@PathVariable String holdId, Authentication authentication) {
		booking.releaseHold(RequestParams.id(holdId, "No encontramos esta reserva."), userId(authentication));
	}

	/** Cliente autenticado con un access token válido, o nulo si compra como invitado. */
	private static UUID userId(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
			return CurrentUser.from(authentication).id();
		}
		return null;
	}
}
