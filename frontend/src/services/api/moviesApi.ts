import type { MovieDetail, MovieQuery, MovieSummary, Page, Screening } from '../../types/movie';
import { ApiError, USE_MOCKS, apiGet } from './httpClient';
import { findMovie, findScreenings, searchMovies } from './mock/mockCatalog';

/** Simula la latencia de red en desarrollo para poder ver los estados de carga. */
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

export function getMovies(filters: MovieQuery): Promise<Page<MovieSummary>> {
  if (USE_MOCKS) return fromMock(() => searchMovies(filters));
  return apiGet<Page<MovieSummary>>('/movies', {
    query: filters.query,
    genre: filters.genre,
    ageRating: filters.ageRating,
    page: filters.page,
  });
}

export function getMovie(movieId: string): Promise<MovieDetail> {
  if (USE_MOCKS) {
    return fromMock(() => {
      const movie = findMovie(movieId);
      if (!movie) throw new ApiError(404, 'MOVIE_NOT_FOUND', 'Esta película no existe o ya no está en cartelera.');
      return movie;
    });
  }
  return apiGet<MovieDetail>(`/movies/${encodeURIComponent(movieId)}`);
}

export function getMovieScreenings(movieId: string): Promise<Screening[]> {
  if (USE_MOCKS) return fromMock(() => findScreenings(movieId));
  return apiGet<Screening[]>(`/movies/${encodeURIComponent(movieId)}/screenings`);
}
