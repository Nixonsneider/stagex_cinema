import { Link, useParams } from 'react-router';
import { StatusMessage } from '../components/StatusMessage';
import {
  ageRatingLabel,
  dayKey,
  formatDayHeading,
  formatDuration,
  formatPrice,
  formatTime,
} from '../features/movies/format';
import { useAsync } from '../hooks/useAsync';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { getMovie, getMovieScreenings } from '../services/api/moviesApi';
import type { Screening } from '../types/movie';

function groupByDay(screenings: Screening[]): Array<{ key: string; screenings: Screening[] }> {
  const groups = new Map<string, Screening[]>();
  for (const screening of screenings) {
    const key = dayKey(screening.startsAt);
    groups.set(key, [...(groups.get(key) ?? []), screening]);
  }
  return [...groups.entries()].map(([key, items]) => ({ key, screenings: items }));
}

/** Ficha pública de la película (`/movies/:movieId`) con sus funciones disponibles. */
export function MovieDetailPage() {
  const { movieId = '' } = useParams();
  const result = useAsync(() => Promise.all([getMovie(movieId), getMovieScreenings(movieId)]), [movieId]);
  useDocumentTitle(result.status === 'success' ? result.data[0].title : 'Película');

  if (result.status === 'loading') {
    return <StatusMessage kind="loading">Cargando película…</StatusMessage>;
  }

  if (result.status === 'error') {
    return (
      <section>
        <h1>{result.notFound ? 'Película no disponible' : 'No pudimos cargar la película'}</h1>
        <StatusMessage kind="error" onRetry={result.notFound ? undefined : result.reload}>
          {result.message}
        </StatusMessage>
        <p>
          <Link to="/">Volver a la cartelera</Link>
        </p>
      </section>
    );
  }

  const [movie, screenings] = result.data;
  const days = groupByDay(screenings);

  return (
    <article className="movie-detail" aria-labelledby="titulo-pelicula">
      <p>
        <Link to="/">← Volver a la cartelera</Link>
      </p>
      <h1 id="titulo-pelicula">{movie.title}</h1>
      <p className="movie-meta">
        {movie.genre} · {formatDuration(movie.durationMinutes)} · {ageRatingLabel(movie.ageRating)}
      </p>
      <p className="synopsis">{movie.synopsis}</p>

      <section aria-labelledby="titulo-funciones">
        <h2 id="titulo-funciones">Funciones disponibles</h2>
        {days.length === 0 ? (
          <StatusMessage kind="empty">Esta película no tiene funciones disponibles por ahora.</StatusMessage>
        ) : (
          days.map((day) => (
            <section key={day.key} className="screening-day" aria-label={formatDayHeading(day.screenings[0].startsAt)}>
              <h3>{formatDayHeading(day.screenings[0].startsAt)}</h3>
              <ul className="screening-list">
                {day.screenings.map((screening) => {
                  const time = formatTime(screening.startsAt);
                  const soldOut = screening.availableSeats === 0;
                  return (
                    <li key={screening.id} className="screening">
                      <span className="screening-time">{time}</span>
                      <span>{screening.roomName}</span>
                      <span>{formatPrice(screening.price, screening.currency)}</span>
                      {soldOut ? (
                        <span className="sold-out">Agotada</span>
                      ) : (
                        <>
                          <span className="seats">
                            {screening.availableSeats === 1
                              ? '1 asiento disponible'
                              : `${screening.availableSeats} asientos disponibles`}
                          </span>
                          <Link
                            className="button"
                            to={`/screenings/${encodeURIComponent(screening.id)}/seats`}
                            aria-label={`Elegir asientos para la función de las ${time} en ${screening.roomName}`}
                          >
                            Elegir asientos
                          </Link>
                        </>
                      )}
                    </li>
                  );
                })}
              </ul>
            </section>
          ))
        )}
      </section>
    </article>
  );
}
