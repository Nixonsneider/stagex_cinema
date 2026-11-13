import { Link } from 'react-router';
import type { MovieSummary } from '../../types/movie';
import { ageRatingLabel, formatDuration } from './format';

function initials(title: string): string {
  return title
    .split(/\s+/)
    .filter((word) => word.length > 3 || /^[A-ZÁÉÍÓÚÑ]/.test(word))
    .slice(0, 2)
    .map((word) => word.charAt(0).toUpperCase())
    .join('');
}

export function MovieCard({ movie }: { movie: MovieSummary }) {
  return (
    <article className="movie-card">
      {movie.posterUrl ? (
        <img className="poster" src={movie.posterUrl} alt="" loading="lazy" />
      ) : (
        <div className="poster poster-placeholder" aria-hidden="true">
          {initials(movie.title)}
        </div>
      )}
      <div className="movie-card-body">
        <h2 className="movie-card-title">
          <Link to={`/movies/${encodeURIComponent(movie.id)}`}>{movie.title}</Link>
        </h2>
        <p className="movie-meta">
          {movie.genre} · {formatDuration(movie.durationMinutes)}
        </p>
        <p className="rating" title={ageRatingLabel(movie.ageRating)}>
          <span className="visually-hidden">Clasificación: </span>
          {movie.ageRating === 'TODOS' ? 'Todo público' : movie.ageRating}
        </p>
      </div>
    </article>
  );
}
