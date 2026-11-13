import { useCallback, useEffect, useState, type DependencyList } from 'react';
import { ApiError } from '../services/api/httpClient';

export type AsyncState<T> =
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; message: string; notFound: boolean };

/**
 * Ejecuta una carga asíncrona y expone su estado. Ignora respuestas que llegan
 * después de cambiar los parámetros, para no mostrar resultados viejos.
 */
export function useAsync<T>(load: () => Promise<T>, deps: DependencyList): AsyncState<T> & { reload: () => void } {
  const [state, setState] = useState<AsyncState<T>>({ status: 'loading' });
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let active = true;
    setState({ status: 'loading' });
    load().then(
      (data) => {
        if (active) setState({ status: 'success', data });
      },
      (error: unknown) => {
        if (!active) return;
        const apiError = error instanceof ApiError ? error : null;
        setState({
          status: 'error',
          message: apiError?.message ?? 'Ocurrió un problema. Intenta de nuevo en unos minutos.',
          notFound: apiError?.status === 404,
        });
      },
    );
    return () => {
      active = false;
    };
    // `load` cambia en cada render; las dependencias reales las indica quien llama.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, attempt]);

  const reload = useCallback(() => setAttempt((value) => value + 1), []);
  return { ...state, reload };
}
