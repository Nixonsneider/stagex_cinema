import type { MovieDetail, MovieQuery, MovieSummary, Page, Screening } from '../../../types/movie';

/**
 * Simulación del backend de catálogo para desarrollar sin API (T-033).
 * Replica las reglas del servidor: solo se publican películas activas con al menos
 * una función programada, futura y en una sala habilitada. El frontend real nunca
 * aplica estas reglas: las aplica el backend.
 */

interface MockMovie extends MovieDetail {
  status: 'ACTIVE' | 'INACTIVE';
}

interface MockScreening extends Screening {
  movieId: string;
  status: 'SCHEDULED' | 'CANCELLED';
  roomActive: boolean;
}

export const PAGE_SIZE = 12;

const MOVIES: MockMovie[] = [
  {
    id: 'mov-001',
    title: 'El Último Faro',
    genre: 'Drama',
    ageRating: '12+',
    durationMinutes: 118,
    posterUrl: null,
    synopsis: 'Una guardiana de faro en la costa Caribe descubre que las luces que ve cada noche no vienen del mar.',
    status: 'ACTIVE',
  },
  {
    id: 'mov-002',
    title: 'Órbita Cero',
    genre: 'Ciencia ficción',
    ageRating: '12+',
    durationMinutes: 132,
    posterUrl: null,
    synopsis: 'La tripulación de una estación abandonada debe decidir a quién creerle cuando la Tierra deja de responder.',
    status: 'ACTIVE',
  },
  {
    id: 'mov-003',
    title: 'Risas en el Barrio',
    genre: 'Comedia',
    ageRating: 'TODOS',
    durationMinutes: 95,
    posterUrl: null,
    synopsis: 'Una familia organiza el torneo de microfútbol más caótico que ha visto su conjunto residencial.',
    status: 'ACTIVE',
  },
  {
    id: 'mov-004',
    title: 'Sombras de Medianoche',
    genre: 'Terror',
    ageRating: '18+',
    durationMinutes: 104,
    posterUrl: null,
    synopsis: 'Cuatro amigos pasan la noche en una casona de Pamplona donde los relojes nunca marcan la misma hora.',
    status: 'ACTIVE',
  },
  {
    id: 'mov-005',
    title: 'Pequeños Exploradores',
    genre: 'Animación',
    ageRating: 'TODOS',
    durationMinutes: 88,
    posterUrl: null,
    synopsis: 'Tres hormigas emprenden un viaje por el jardín para salvar su colonia antes de la temporada de lluvias.',
    status: 'ACTIVE',
  },
  {
    id: 'mov-006',
    title: 'Ruta Fronteriza',
    genre: 'Acción',
    ageRating: '15+',
    durationMinutes: 121,
    posterUrl: null,
    synopsis: 'Un conductor de bus intermunicipal queda atrapado en medio de una persecución que no buscó.',
    status: 'ACTIVE',
  },
  // Activa pero sin funciones vigentes: no debe aparecer en la cartelera.
  {
    id: 'mov-007',
    title: 'Ecos del Pasado',
    genre: 'Drama',
    ageRating: '12+',
    durationMinutes: 110,
    posterUrl: null,
    synopsis: 'Una historiadora encuentra cartas que cambian la versión oficial de su pueblo.',
    status: 'ACTIVE',
  },
  // Inactiva: no debe aparecer aunque tenga funciones.
  {
    id: 'mov-008',
    title: 'Proyecto Retirado',
    genre: 'Acción',
    ageRating: '15+',
    durationMinutes: 99,
    posterUrl: null,
    synopsis: 'Película desactivada por el administrador.',
    status: 'INACTIVE',
  },
];

const ROOMS = ['Sala 1', 'Sala 2', 'Sala 3 VIP'];

/** Genera funciones relativas a `now` para que el catálogo de prueba siempre tenga fechas vigentes. */
function buildScreenings(now: Date): MockScreening[] {
  const base = new Date(now);
  base.setUTCMinutes(0, 0, 0);
  const at = (days: number, hourUtc: number) => {
    const date = new Date(base);
    date.setUTCDate(date.getUTCDate() + days);
    date.setUTCHours(hourUtc);
    return date.toISOString();
  };

  const screenings: MockScreening[] = [];
  let sequence = 0;
  const add = (movieId: string, startsAt: string, extra: Partial<MockScreening> = {}) => {
    sequence += 1;
    const room = ROOMS[sequence % ROOMS.length];
    screenings.push({
      id: `scr-${String(sequence).padStart(3, '0')}`,
      movieId,
      startsAt,
      roomName: room,
      price: room.includes('VIP') ? 24000 : 16000,
      currency: 'COP',
      availableSeats: 40 - ((sequence * 7) % 40),
      status: 'SCHEDULED',
      roomActive: true,
      ...extra,
    });
  };

  // Horas en UTC: 20, 23 y 1 (día siguiente) equivalen a 3:00 p. m., 6:00 p. m. y 8:00 p. m. en Bogotá.
  for (const movieId of ['mov-001', 'mov-002', 'mov-003', 'mov-004', 'mov-005', 'mov-006']) {
    add(movieId, at(1, 20));
    add(movieId, at(1, 23));
    add(movieId, at(2, 25));
  }
  add('mov-002', at(3, 23), { availableSeats: 0 });
  add('mov-007', at(-1, 23)); // ya pasó
  add('mov-007', at(2, 23), { status: 'CANCELLED' });
  add('mov-007', at(3, 23), { roomActive: false });
  add('mov-008', at(1, 23)); // película inactiva
  return screenings;
}

function normalize(text: string): string {
  return text
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
    .trim();
}

function visibleScreenings(now: Date): MockScreening[] {
  return buildScreenings(now).filter(
    (screening) => screening.status === 'SCHEDULED' && screening.roomActive && new Date(screening.startsAt) > now,
  );
}

function toSummary({ id, title, genre, ageRating, durationMinutes, posterUrl }: MockMovie): MovieSummary {
  return { id, title, genre, ageRating, durationMinutes, posterUrl };
}

export function searchMovies(filters: MovieQuery, now: Date = new Date()): Page<MovieSummary> {
  const withScreenings = new Set(visibleScreenings(now).map((screening) => screening.movieId));
  const query = normalize(filters.query ?? '');
  const matches = MOVIES.filter(
    (movie) =>
      movie.status === 'ACTIVE' &&
      withScreenings.has(movie.id) &&
      (query === '' || normalize(movie.title).includes(query)) &&
      (!filters.genre || movie.genre === filters.genre) &&
      (!filters.ageRating || movie.ageRating === filters.ageRating),
  ).sort((a, b) => a.title.localeCompare(b.title, 'es'));

  const page = Math.max(0, filters.page ?? 0);
  return {
    content: matches.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE).map(toSummary),
    page,
    size: PAGE_SIZE,
    totalElements: matches.length,
    totalPages: Math.ceil(matches.length / PAGE_SIZE),
  };
}

export function findMovie(movieId: string): MovieDetail | null {
  const movie = MOVIES.find((candidate) => candidate.id === movieId && candidate.status === 'ACTIVE');
  if (!movie) return null;
  return { ...toSummary(movie), synopsis: movie.synopsis };
}

export function findScreenings(movieId: string, now: Date = new Date()): Screening[] {
  return visibleScreenings(now)
    .filter((screening) => screening.movieId === movieId)
    .sort((a, b) => a.startsAt.localeCompare(b.startsAt))
    .map(({ id, startsAt, roomName, price, currency, availableSeats }) => ({
      id,
      startsAt,
      roomName,
      price,
      currency,
      availableSeats,
    }));
}

/** Opciones de filtro del catálogo de prueba. */
export const GENRES = ['Acción', 'Animación', 'Ciencia ficción', 'Comedia', 'Drama', 'Terror'];
export const AGE_RATINGS = ['TODOS', '7+', '12+', '15+', '18+'];
