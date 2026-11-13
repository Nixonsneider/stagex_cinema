import { useEffect, useId, useState, type FormEvent } from 'react';
import { AGE_RATINGS, GENRES } from '../../services/api/mock/mockCatalog';
import { ageRatingLabel } from './format';

export interface FilterValues {
  query: string;
  genre: string;
  ageRating: string;
}

interface MovieFiltersProps {
  values: FilterValues;
  onApply: (values: FilterValues) => void;
}

/** Búsqueda por título y filtros básicos. Los filtros viven en la URL para poder compartirlos. */
export function MovieFilters({ values, onApply }: MovieFiltersProps) {
  const [query, setQuery] = useState(values.query);
  const id = useId();

  // Si la URL cambia (por ejemplo, al limpiar filtros), el campo de texto se sincroniza.
  useEffect(() => setQuery(values.query), [values.query]);

  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    onApply({ ...values, query: query.trim() });
  };

  const hasFilters = values.query !== '' || values.genre !== '' || values.ageRating !== '';

  return (
    <form className="filters" role="search" onSubmit={submit}>
      <div className="field field-search">
        <label htmlFor={`${id}-query`}>Buscar por título</label>
        <input
          id={`${id}-query`}
          type="search"
          value={query}
          maxLength={100}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Ej.: Órbita"
        />
      </div>
      <div className="field">
        <label htmlFor={`${id}-genre`}>Género</label>
        <select
          id={`${id}-genre`}
          value={values.genre}
          onChange={(event) => onApply({ ...values, query: query.trim(), genre: event.target.value })}
        >
          <option value="">Todos los géneros</option>
          {GENRES.map((genre) => (
            <option key={genre} value={genre}>
              {genre}
            </option>
          ))}
        </select>
      </div>
      <div className="field">
        <label htmlFor={`${id}-rating`}>Clasificación</label>
        <select
          id={`${id}-rating`}
          value={values.ageRating}
          onChange={(event) => onApply({ ...values, query: query.trim(), ageRating: event.target.value })}
        >
          <option value="">Todas</option>
          {AGE_RATINGS.map((rating) => (
            <option key={rating} value={rating}>
              {ageRatingLabel(rating)}
            </option>
          ))}
        </select>
      </div>
      <div className="filter-actions">
        <button type="submit" className="button">
          Buscar
        </button>
        {hasFilters && (
          <button type="button" className="button button-secondary" onClick={() => onApply({ query: '', genre: '', ageRating: '' })}>
            Limpiar filtros
          </button>
        )}
      </div>
    </form>
  );
}
