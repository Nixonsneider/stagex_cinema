import { useState, type FormEvent } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router';
import { homePathFor, useAuth } from '../features/auth/AuthContext';
import { hasErrors, validateLogin, type FieldErrors } from '../features/auth/validation';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { ApiError } from '../services/api/httpClient';

/** Ruta a la que el usuario intentaba entrar antes de pedirle iniciar sesión. */
function redirectTarget(state: unknown): string | null {
  if (state && typeof state === 'object' && 'from' in state) {
    const from = (state as { from?: unknown }).from;
    // Solo rutas internas: evita redirecciones abiertas a otros sitios.
    if (typeof from === 'string' && from.startsWith('/') && !from.startsWith('//')) return from;
  }
  return null;
}

export function LoginPage() {
  useDocumentTitle('Iniciar sesión');
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = redirectTarget(location.state);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<FieldErrors<'email' | 'password'>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (user && !submitting) {
    return <Navigate to={homePathFor(user.role)} replace />;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting) return;
    const validation = validateLogin(email, password);
    setErrors(validation);
    setFormError(null);
    if (hasErrors(validation)) return;

    setSubmitting(true);
    try {
      const loggedUser = await login({ email, password });
      navigate(from ?? homePathFor(loggedUser.role), { replace: true });
    } catch (error) {
      setFormError(error instanceof ApiError ? error.message : 'No pudimos iniciar sesión. Intenta de nuevo.');
      setPassword('');
      setSubmitting(false);
    }
  }

  return (
    <section className="auth-page">
      <h1>Iniciar sesión</h1>
      {from && <p className="auth-notice">Inicia sesión para continuar.</p>}
      {formError && (
        <p className="form-error" role="alert">
          {formError}
        </p>
      )}
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <div className="field">
          <label htmlFor="login-email">Correo electrónico</label>
          <input
            id="login-email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            aria-invalid={errors.email ? true : undefined}
            aria-describedby={errors.email ? 'login-email-error' : undefined}
          />
          {errors.email && (
            <p id="login-email-error" className="field-error">
              {errors.email}
            </p>
          )}
        </div>
        <div className="field">
          <label htmlFor="login-password">Contraseña</label>
          <input
            id="login-password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            aria-invalid={errors.password ? true : undefined}
            aria-describedby={errors.password ? 'login-password-error' : undefined}
          />
          {errors.password && (
            <p id="login-password-error" className="field-error">
              {errors.password}
            </p>
          )}
        </div>
        <button type="submit" className="button" disabled={submitting}>
          {submitting ? 'Ingresando…' : 'Ingresar'}
        </button>
      </form>
      <p>
        <Link to="/forgot-password">¿Olvidaste tu contraseña?</Link>
      </p>
      <p>
        ¿No tienes cuenta? <Link to="/register">Crear cuenta</Link>
      </p>
    </section>
  );
}
