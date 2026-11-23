import { useEffect, useMemo, useRef, useState, type FormEvent } from 'react';
import { Link, useLocation, useParams } from 'react-router';
import { StatusMessage } from '../components/StatusMessage';
import { useAuth } from '../features/auth/AuthContext';
import { validateEmail } from '../features/auth/validation';
import { formatDayHeading, formatPrice, formatTime } from '../features/movies/format';
import { SeatMap, seatName } from '../features/seats/SeatMap';
import { useAsync } from '../hooks/useAsync';
import { formatCountdown, useCountdown } from '../hooks/useCountdown';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { createHold, getScreeningSeats, releaseHold } from '../services/api/bookingApi';
import { ApiError } from '../services/api/httpClient';
import type { HoldResponse, ScreeningSeats, SeatView } from '../types/booking';

/** A partir de estos segundos se avisa que la reserva está por vencer. */
const WARNING_SECONDS = 120;

function errorMessage(error: unknown): string {
  return error instanceof ApiError ? error.message : 'Ocurrió un problema. Intenta de nuevo en unos minutos.';
}

/**
 * Mapa y selección de asientos (`/screenings/:screeningId/seats`, T-044). El backend decide
 * disponibilidad, total y vencimiento; aquí solo se muestran y se envía la selección.
 */
export function SeatSelectionPage() {
  const { screeningId = '' } = useParams();
  const location = useLocation();
  const { user } = useAuth();
  const seatsResult = useAsync(() => getScreeningSeats(screeningId), [screeningId]);
  const { reload } = seatsResult;
  // Último mapa cargado: al refrescar se sigue mostrando en vez de parpadear con "Cargando…".
  const lastMap = useRef<ScreeningSeats | null>(null);
  if (seatsResult.status === 'success') {
    lastMap.current = seatsResult.data;
  } else if (lastMap.current?.screeningId !== screeningId) {
    lastMap.current = null;
  }

  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [hold, setHold] = useState<HoldResponse | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [guestEmail, setGuestEmail] = useState('');
  const [emailError, setEmailError] = useState<string | undefined>();
  const remaining = useCountdown(hold?.expiresAt ?? null);

  useDocumentTitle(
    seatsResult.status === 'success' ? `Asientos · ${seatsResult.data.movieTitle}` : 'Selección de asientos',
  );

  // Si la reserva vence, se limpia la selección y se refresca el mapa (docs/FRONTEND.md).
  useEffect(() => {
    if (hold && remaining === 0) {
      setHold(null);
      setSelectedIds([]);
      setNotice('Tu reserva venció y los asientos se liberaron. Elige tus asientos de nuevo.');
      reload();
    }
  }, [hold, remaining, reload]);

  const selected = useMemo(() => new Set(selectedIds), [selectedIds]);

  const map = lastMap.current;

  if (!map && seatsResult.status === 'loading') {
    return <StatusMessage kind="loading">Cargando asientos…</StatusMessage>;
  }

  if (!map || seatsResult.status === 'error') {
    const failed = seatsResult.status === 'error' ? seatsResult : null;
    return (
      <section>
        <h1>{failed?.notFound ? 'Función no disponible' : 'No pudimos cargar los asientos'}</h1>
        <StatusMessage kind="error" onRetry={failed?.notFound ? undefined : reload}>
          {failed?.message ?? 'Ocurrió un problema. Intenta de nuevo en unos minutos.'}
        </StatusMessage>
        <p>
          <Link to="/">Volver a la cartelera</Link>
        </p>
      </section>
    );
  }

  const selectedSeats = map.seats.filter((seat) => selected.has(seat.id));

  const toggleSeat = (seat: SeatView) => {
    setNotice(null);
    if (selected.has(seat.id)) {
      setSelectedIds((ids) => ids.filter((id) => id !== seat.id));
      setError(null);
      return;
    }
    if (selectedIds.length >= map.maxSeatsPerOrder) {
      setError(`Puedes elegir máximo ${map.maxSeatsPerOrder} asientos por compra.`);
      return;
    }
    setError(null);
    setSelectedIds((ids) => [...ids, seat.id]);
  };

  const handleReserve = async (event: FormEvent) => {
    event.preventDefault();
    if (submitting || selectedIds.length === 0) return;
    if (!user) {
      const problem = validateEmail(guestEmail);
      setEmailError(problem);
      if (problem) return;
    }
    setSubmitting(true);
    setError(null);
    setNotice(null);
    try {
      const response = await createHold(
        {
          screeningId: map.screeningId,
          screeningSeatIds: selectedIds,
          guestEmail: user ? undefined : guestEmail.trim().toLowerCase(),
        },
        Boolean(user),
      );
      setHold(response);
      reload();
    } catch (caught) {
      setError(errorMessage(caught));
      if (caught instanceof ApiError && caught.code === 'SEAT_NOT_AVAILABLE') {
        setSelectedIds([]);
        reload();
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleRelease = async () => {
    if (!hold || submitting) return;
    setSubmitting(true);
    setError(null);
    try {
      await releaseHold(hold.holdId);
      setHold(null);
      setSelectedIds([]);
      setNotice('Liberamos tus asientos. Puedes elegir otros.');
      reload();
    } catch (caught) {
      setError(errorMessage(caught));
    } finally {
      setSubmitting(false);
    }
  };

  const seatList = selectedSeats.map(seatName).join(', ');

  return (
    <section className="seat-page" aria-labelledby="titulo-asientos">
      <p>
        <Link to={`/movies/${encodeURIComponent(map.movieId)}`}>← Volver a la película</Link>
      </p>
      <h1 id="titulo-asientos">Elige tus asientos</h1>
      <p className="movie-meta">
        <strong>{map.movieTitle}</strong> · {formatDayHeading(map.startsAt)} · {formatTime(map.startsAt)} ·{' '}
        {map.roomName} · {formatPrice(map.price, map.currency)} por asiento
      </p>

      {notice && (
        <p className="auth-notice" role="status">
          {notice}
        </p>
      )}
      {error && (
        <p className="form-error" role="alert">
          {error}
        </p>
      )}

      <div className="seat-layout">
        <SeatMap seats={map.seats} selected={selected} locked={hold !== null || submitting} onToggle={toggleSeat} />

        <aside className="seat-summary" aria-labelledby="titulo-resumen">
          <h2 id="titulo-resumen">{hold ? 'Asientos reservados' : 'Tu selección'}</h2>

          {hold ? (
            <>
              <p>
                <strong>{seatList}</strong>
              </p>
              <p>
                Total: <strong>{formatPrice(hold.amount, hold.currency)}</strong>
              </p>
              <p className={remaining !== null && remaining <= WARNING_SECONDS ? 'hold-timer hold-timer-warning' : 'hold-timer'}>
                Tus asientos están reservados por{' '}
                <span role="timer" aria-label="Tiempo restante de la reserva">
                  {formatCountdown(remaining ?? 0)}
                </span>
              </p>
              {remaining !== null && remaining <= WARNING_SECONDS && (
                <p className="field-error" role="status">
                  Tu reserva está por vencer. Continúa al pago para no perder tus asientos.
                </p>
              )}
              <div className="seat-actions">
                <Link className="button" to={`/checkout/${encodeURIComponent(hold.orderId)}`}>
                  Continuar al pago
                </Link>
                <button type="button" className="button button-secondary" onClick={handleRelease} disabled={submitting}>
                  Cambiar asientos
                </button>
              </div>
            </>
          ) : (
            <form onSubmit={handleReserve} noValidate>
              {selectedSeats.length === 0 ? (
                <p>Toca un asiento disponible para elegirlo. Máximo {map.maxSeatsPerOrder} por compra.</p>
              ) : (
                <>
                  <p>
                    {selectedSeats.length === 1 ? '1 asiento' : `${selectedSeats.length} asientos`}:{' '}
                    <strong>{seatList}</strong>
                  </p>
                  <p>
                    Total estimado: <strong>{formatPrice(map.price * selectedSeats.length, map.currency)}</strong>
                  </p>
                </>
              )}

              {!user && (
                <div className="field">
                  <label htmlFor="guest-email">Correo para recibir tus entradas</label>
                  <input
                    id="guest-email"
                    type="email"
                    autoComplete="email"
                    value={guestEmail}
                    onChange={(event) => setGuestEmail(event.target.value)}
                    aria-invalid={emailError ? true : undefined}
                    aria-describedby={emailError ? 'guest-email-error' : 'guest-email-hint'}
                  />
                  {emailError ? (
                    <p id="guest-email-error" className="field-error">
                      {emailError}
                    </p>
                  ) : (
                    <p id="guest-email-hint" className="field-hint">
                      ¿Tienes cuenta?{' '}
                      <Link to="/login" state={{ from: location.pathname }}>
                        Inicia sesión
                      </Link>
                    </p>
                  )}
                </div>
              )}

              <p className="field-hint">
                Al reservar, guardamos tus asientos por {map.holdMinutes} minutos mientras pagas.
              </p>
              <button type="submit" className="button" disabled={submitting || selectedSeats.length === 0}>
                {submitting ? 'Reservando…' : 'Reservar asientos'}
              </button>
            </form>
          )}
        </aside>
      </div>
    </section>
  );
}
