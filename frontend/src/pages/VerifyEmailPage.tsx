import { useEffect, useRef, useState } from 'react';
import { Link, useSearchParams } from 'react-router';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { verifyEmail } from '../services/api/authApi';
import { ApiError } from '../services/api/httpClient';

type State = { status: 'verifying' } | { status: 'done' } | { status: 'error'; message: string };

/** Destino del enlace del correo de verificación: /verify-email?token=... (T-015). */
export function VerifyEmailPage() {
  useDocumentTitle('Confirmar correo');
  const [params] = useSearchParams();
  const token = params.get('token') ?? '';
  const [state, setState] = useState<State>({ status: 'verifying' });
  // StrictMode ejecuta los efectos dos veces en desarrollo; el token es de un solo uso.
  const sent = useRef(false);

  useEffect(() => {
    if (sent.current) return;
    sent.current = true;
    if (!token) {
      setState({ status: 'error', message: 'El enlace no es válido o ya venció. Solicita uno nuevo.' });
      return;
    }
    verifyEmail(token).then(
      () => setState({ status: 'done' }),
      (error: unknown) =>
        setState({
          status: 'error',
          message: error instanceof ApiError ? error.message : 'No pudimos confirmar tu correo. Intenta de nuevo.',
        }),
    );
  }, [token]);

  return (
    <section className="auth-page">
      <h1>Confirmar correo</h1>
      {state.status === 'verifying' && <p role="status">Confirmando tu correo…</p>}
      {state.status === 'done' && (
        <>
          <p role="status">Tu cuenta quedó activa. Ya puedes iniciar sesión.</p>
          <p>
            <Link to="/login">Iniciar sesión</Link>
          </p>
        </>
      )}
      {state.status === 'error' && (
        <>
          <p className="form-error" role="alert">
            {state.message}
          </p>
          <p>
            <Link to="/register">Volver a registrarme</Link>
          </p>
        </>
      )}
    </section>
  );
}
