import { describe, expect, it } from 'vitest';
import { findMovie, findScreenings, searchMovies } from './mockCatalog';

const NOW = new Date('2026-11-12T15:00:00Z');

describe('catálogo de prueba', () => {
  it('solo publica películas activas con funciones vigentes', () => {
    const titles = searchMovies({}, NOW).content.map((movie) => movie.title);

    expect(titles).toHaveLength(6);
    expect(titles).not.toContain('Ecos del Pasado'); // funciones pasadas, canceladas o en sala inactiva
    expect(titles).not.toContain('Proyecto Retirado'); // película inactiva
  });

  it('busca por título sin importar mayúsculas ni tildes', () => {
    expect(searchMovies({ query: 'ORBITA' }, NOW).content.map((movie) => movie.title)).toEqual(['Órbita Cero']);
    expect(searchMovies({ query: '  faro ' }, NOW).content.map((movie) => movie.title)).toEqual(['El Último Faro']);
  });

  it('filtra por género y clasificación', () => {
    expect(searchMovies({ genre: 'Comedia' }, NOW).content.map((movie) => movie.id)).toEqual(['mov-003']);
    expect(searchMovies({ ageRating: 'TODOS' }, NOW).totalElements).toBe(2);
    expect(searchMovies({ genre: 'Terror', ageRating: 'TODOS' }, NOW).totalElements).toBe(0);
  });

  it('no expone la ficha de una película inactiva o inexistente', () => {
    expect(findMovie('mov-001')?.title).toBe('El Último Faro');
    expect(findMovie('mov-008')).toBeNull();
    expect(findMovie('no-existe')).toBeNull();
  });

  it('devuelve solo funciones vigentes ordenadas por hora', () => {
    const screenings = findScreenings('mov-002', NOW);
    const times = screenings.map((screening) => screening.startsAt);

    expect(screenings.length).toBeGreaterThan(0);
    expect(times).toEqual([...times].sort());
    expect(screenings.every((screening) => new Date(screening.startsAt) > NOW)).toBe(true);
    expect(findScreenings('mov-007', NOW)).toEqual([]);
  });
});
