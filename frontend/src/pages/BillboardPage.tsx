import { useSearchParams } from 'react-router';
import { StatusMessage } from '../components/StatusMessage';
import { MovieCard } from '../features/movies/MovieCard';
import { MovieFilters, type FilterValues } from '../features/movies/MovieFilters';
import { useAsync } from '../hooks/useAsync';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { getMovies } from '../services/api/moviesApi';

/** Cartelera pública (`/`): solo muestra lo que el backend publica como disponible. */
export function BillboardPage() {
  useDocumentTitle('Cartelera');
  const [params, setParams] = useSearchParams();
  const filters: FilterValues = {
    query: params.get('q') ?? '',
    genre: params.get('genre') ?? '',
    ageRating: params.get('rating') ?? '',
  };

  const result = useAsync(
    () => getMovies({ query: filters.query, genre: filters.genre, ageRating: filters.ageRating }),
    [filters.query, filters.genre, filters.ageRating],
  );

  const applyFilters = (values: FilterValues) => {
    const next = new URLSearchParams();
    if (values.query) next.set('q', values.query);
    if (values.genre) next.set('genre', values.genre);
    if (values.ageRating) next.set('rating', values.ageRating);
    setParams(next);
  };

  return (
    <section aria-labelledby="titulo-cartelera">
      <h1 id="titulo-cartelera">Cartelera</h1>
      <MovieFilters values={filters} onApply={applyFilters} />

      {result.status === 'loading' && <StatusMessage kind="loading">Cargando películas…</StatusMessage>}

      {result.status === 'error' && (
        <StatusMessage kind="error" onRetry={result.reload}>
          {result.message}
        </StatusMessage>
      )}

      {result.status === 'success' &&
        (result.data.content.length === 0 ? (
          <StatusMessage kind="empty">No encontramos películas con funciones disponibles para esa búsqueda.</StatusMessage>
        ) : (
          <>
            <p className="results-count" role="status">
              {result.data.totalElements === 1
                ? '1 película en cartelera'
                : `${result.data.totalElements} películas en cartelera`}
            </p>
            <ul className="movie-grid">
              {result.data.content.map((movie) => (
                <li key={movie.id}>
                  <MovieCard movie={movie} />
                </li>
              ))}
            </ul>
          </>
        ))}
    </section>
  );
}
