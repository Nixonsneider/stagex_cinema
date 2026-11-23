import type { HoldRequest, HoldResponse, ScreeningSeats } from '../../types/booking';
import { USE_MOCKS, apiDelete, apiGet, apiPost } from './httpClient';
import { mockCreateHold, mockReleaseHold, mockSeatMap } from './mock/mockBooking';

/** Simula la latencia de red en desarrollo para poder ver los estados de carga y envío. */
const MOCK_DELAY_MS = import.meta.env.MODE === 'test' ? 0 : 250;

function fromMock<T>(produce: () => T): Promise<T> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        resolve(produce());
      } catch (error) {
        reject(error);
      }
    }, MOCK_DELAY_MS);
  });
}

/** Mapa de asientos con el estado vigente (T-040). */
export function getScreeningSeats(screeningId: string): Promise<ScreeningSeats> {
  if (USE_MOCKS) return fromMock(() => mockSeatMap(screeningId));
  return apiGet<ScreeningSeats>(`/screenings/${encodeURIComponent(screeningId)}/seats`);
}

/**
 * Bloquea los asientos elegidos (T-041). Si hay sesión, el backend toma al cliente del
 * access token; si no, usa `guestEmail`.
 */
export function createHold(request: HoldRequest, loggedIn: boolean): Promise<HoldResponse> {
  if (USE_MOCKS) return fromMock(() => mockCreateHold(request, loggedIn));
  return apiPost<HoldResponse>('/holds', request);
}

/** Libera una reserva pendiente para elegir otros asientos. */
export function releaseHold(holdId: string): Promise<void> {
  if (USE_MOCKS) return fromMock(() => mockReleaseHold(holdId));
  return apiDelete<void>(`/holds/${encodeURIComponent(holdId)}`);
}
