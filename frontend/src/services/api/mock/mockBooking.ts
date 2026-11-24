import type { HoldRequest, HoldResponse, ScreeningSeats, SeatStatus, SeatView } from '../../../types/booking';
import { ApiError } from '../httpClient';
import { findScreeningById } from './mockCatalog';

/**
 * Simulación de disponibilidad y reservas (T-040, T-041) para desarrollar sin backend.
 * Replica las respuestas del servidor; el frontend real nunca decide disponibilidad.
 */

export const MOCK_MAX_SEATS = 10;
export const MOCK_HOLD_MINUTES = 10;

const ROWS = ['A', 'B', 'C', 'D', 'E', 'F'];
const SEATS_PER_ROW = 10;

interface MockHold {
  screeningId: string;
  seatIds: string[];
  expiresAt: number;
}

const holds = new Map<string, MockHold>();
let holdSequence = 0;

/** Estado fijo de la función antes de las reservas de la sesión: algunos vendidos y bloqueados. */
function baseStatus(row: number, seat: number): SeatStatus {
  if (row === ROWS.length - 1 && seat === SEATS_PER_ROW) return 'UNAVAILABLE';
  if ((row * 7 + seat * 3) % 11 === 0) return 'SOLD';
  if ((row * 5 + seat) % 13 === 0) return 'HELD';
  return 'AVAILABLE';
}

function activeHeldIds(screeningId: string, now: number): Set<string> {
  const ids = new Set<string>();
  for (const [holdId, hold] of holds) {
    if (hold.expiresAt <= now) {
      holds.delete(holdId);
    } else if (hold.screeningId === screeningId) {
      hold.seatIds.forEach((id) => ids.add(id));
    }
  }
  return ids;
}

function buildSeats(screeningId: string, now: number): SeatView[] {
  const held = activeHeldIds(screeningId, now);
  return ROWS.flatMap((row, rowIndex) =>
    Array.from({ length: SEATS_PER_ROW }, (_, index) => {
      const number = index + 1;
      const id = `${screeningId}-${row}${number}`;
      const base = baseStatus(rowIndex, number);
      const status: SeatStatus = base === 'AVAILABLE' && held.has(id) ? 'HELD' : base;
      return { id, row, number, seatType: 'STANDARD', status };
    }),
  );
}

function screeningNotFound(): ApiError {
  return new ApiError(404, 'NOT_FOUND', 'Esta función ya no está disponible.');
}

export function mockSeatMap(screeningId: string, now: number = Date.now()): ScreeningSeats {
  const found = findScreeningById(screeningId, new Date(now));
  if (!found) throw screeningNotFound();
  const { screening, movie } = found;
  return {
    screeningId,
    movieId: movie.id,
    movieTitle: movie.title,
    startsAt: screening.startsAt,
    roomName: screening.roomName,
    price: screening.price,
    currency: screening.currency,
    maxSeatsPerOrder: MOCK_MAX_SEATS,
    holdMinutes: MOCK_HOLD_MINUTES,
    seats: buildSeats(screeningId, now),
  };
}

export function mockCreateHold(request: HoldRequest, loggedIn: boolean, now: number = Date.now()): HoldResponse {
  const map = mockSeatMap(request.screeningId, now);
  const ids = request.screeningSeatIds;
  if (ids.length === 0 || ids.length > MOCK_MAX_SEATS || new Set(ids).size !== ids.length) {
    throw new ApiError(400, 'VALIDATION_ERROR', 'La selección de asientos no es válida.');
  }
  if (!loggedIn && !request.guestEmail) {
    throw new ApiError(400, 'VALIDATION_ERROR', 'Escribe tu correo para enviarte las entradas o inicia sesión.');
  }
  const byId = new Map(map.seats.map((seat) => [seat.id, seat]));
  if (ids.some((id) => byId.get(id)?.status !== 'AVAILABLE')) {
    throw new ApiError(409, 'SEAT_NOT_AVAILABLE', 'Uno o más asientos ya no están disponibles. Elige otros asientos.');
  }
  holdSequence += 1;
  const holdId = `ord-${String(holdSequence).padStart(4, '0')}`;
  const expiresAt = now + MOCK_HOLD_MINUTES * 60_000;
  holds.set(holdId, { screeningId: request.screeningId, seatIds: [...ids], expiresAt });
  return {
    holdId,
    orderId: holdId,
    expiresAt: new Date(expiresAt).toISOString(),
    amount: map.price * ids.length,
    currency: map.currency,
    paymentUrl: null,
  };
}

export function mockReleaseHold(holdId: string): void {
  holds.delete(holdId);
}

/** Limpia las reservas simuladas entre pruebas. */
export function resetMockHolds(): void {
  holds.clear();
  holdSequence = 0;
}
