/** Película tal como la devuelve `GET /movies` (docs/API.md). */
export interface MovieSummary {
  id: string;
  title: string;
  genre: string;
  /** Clasificación por edad: TODOS, 7+, 12+, 15+ o 18+. */
  ageRating: string;
  durationMinutes: number;
  posterUrl: string | null;
}

/** Ficha completa de `GET /movies/{movieId}`. */
export interface MovieDetail extends MovieSummary {
  synopsis: string;
}

/** Función disponible de `GET /movies/{movieId}/screenings`. */
export interface Screening {
  id: string;
  /** Fecha y hora ISO 8601. */
  startsAt: string;
  roomName: string;
  /** Precio calculado por el backend; el frontend solo lo muestra. */
  price: number;
  currency: string;
  availableSeats: number;
}

export interface MovieQuery {
  query?: string;
  genre?: string;
  ageRating?: string;
  page?: number;
}

/** Formato de los listados paginados del backend. */
export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
