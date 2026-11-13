import { describe, expect, it } from 'vitest';
import { ageRatingLabel, dayKey, formatDuration, formatPrice } from './format';

describe('formatos de catálogo', () => {
  it('formatea la duración', () => {
    expect(formatDuration(45)).toBe('45 min');
    expect(formatDuration(120)).toBe('2 h');
    expect(formatDuration(132)).toBe('2 h 12 min');
  });

  it('formatea pesos colombianos sin decimales', () => {
    expect(formatPrice(24000, 'COP')).toContain('24.000');
  });

  it('agrupa por día en hora de Colombia', () => {
    // 01:00 UTC del 13 equivale a las 8:00 p. m. del 12 en Bogotá.
    expect(dayKey('2026-11-13T01:00:00Z')).toBe('2026-11-12');
  });

  it('describe la clasificación en texto', () => {
    expect(ageRatingLabel('TODOS')).toBe('Todo público');
    expect(ageRatingLabel('15+')).toBe('Mayores de 15');
  });
});
