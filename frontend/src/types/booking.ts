/** Estado de un asiento tal como lo decide el backend (`GET /screenings/{id}/seats`). */
export type SeatStatus = 'AVAILABLE' | 'HELD' | 'SOLD' | 'UNAVAILABLE';

export interface SeatView {
  /** Identificador del asiento en esta función; es el que se envía a `POST /holds`. */
  id: string;
  row: string;
  number: number;
  seatType: string;
  status: SeatStatus;
}

/** Mapa de asientos de una función. */
export interface ScreeningSeats {
  screeningId: string;
  movieId: string;
  movieTitle: string;
  /** Fecha y hora ISO 8601. */
  startsAt: string;
  roomName: string;
  /** Precio por asiento calculado por el backend. */
  price: number;
  currency: string;
  maxSeatsPerOrder: number;
  /** Minutos que dura el bloqueo de los asientos. */
  holdMinutes: number;
  seats: SeatView[];
}

/** Cuerpo de `POST /holds`. `guestEmail` solo se envía si no hay sesión. */
export interface HoldRequest {
  screeningId: string;
  screeningSeatIds: string[];
  guestEmail?: string;
}

/** Respuesta de `POST /holds`. El total lo calcula el backend. */
export interface HoldResponse {
  holdId: string;
  orderId: string;
  /** Vencimiento del bloqueo, ISO 8601. */
  expiresAt: string;
  amount: number;
  currency: string;
  paymentUrl: string | null;
}
