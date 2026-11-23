import { useState, type FormEvent } from 'react';
import { Link, useSearchParams } from 'react-router';
import { PASSWORD_MIN_LENGTH, validateNewPassword } from '../features/auth/validation';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { resetPassword } from '../services/api/authApi';
import { ApiError } from '../services/api/httpClient';

/** Destino del enlace de recuperación: /reset-password?token=... (T-017). */
export function ResetPasswordPage() {
  useDocumentTitle('Crear contraseña nueva');
  const [params] = useSearchParams();
  const token = params.get('token') ?? '';
  const [password, setPassword] = useState('');
  const [confirmation, setConfirmation] = useState('');
  const [errors, setErrors] = useState<{ password?: string; confirmation?: string }>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting) return;
    const validation: { password?: string; confirmation?: string } = {};
    const passwordError = validateNewPassword(password);
    if (passwordError) validation.password = passwordError;
    if (confirmation !== password) validation.confirmation = 'Las contraseñas no coinciden.';
    setErrors(validation);
    setFormError(null);
    if (Object.keys(validation).length > 0) return;

    setSubmitting(true);
    try {
      await resetPassword(token, password);
      setDone(true);
    } catch (error) {
      setFormError(error instanceof ApiError ? error.message : 'No pudimos cambiar tu contraseña. Intenta de nuevo.');
    } finally {
      setSubmitting(false);
    }
  }

  if (!token) {
    return (
      <section className="auth-page">
        <h1>Crear contraseña nueva</h1>
        <p className="form-error" role="alert">
          El enlace no es válido o ya venció. Solicita uno nuevo.
        </p>
        <p>
          <Link to="/forgot-password">Solicitar otro enlace</Link>
        </p>
      </section>
    );
  }

  if (done) {
    return (
      <section className="auth-page">
        <h1>Contraseña actualizada</h1>
        <p role="status">Cambiamos tu contraseña y cerramos las sesiones abiertas. Inicia sesión con la nueva.</p>
        <p>
          <Link to="/login">Iniciar sesión</Link>
        </p>
      </section>
    );
  }

  return (
    <section className="auth-page">
      <h1>Crear contraseña nueva</h1>
      {formError && (
        <p className="form-error" role="alert">
          {formError} <Link to="/forgot-password">Solicitar otro enlace</Link>
        </p>
      )}
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <div className="field">
          <label htmlFor="reset-password">Contraseña nueva</label>
          <input
            id="reset-password"
            type="password"
            autoComplete="new-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            aria-invalid={errors.password ? true : undefined}
            aria-describedby={errors.password ? 'reset-password-hint reset-password-error' : 'reset-password-hint'}
          />
          <p id="reset-password-hint" className="field-hint">
            Mínimo {PASSWORD_MIN_LENGTH} caracteres, con letras y números.
          </p>
          {errors.password && (
            <p id="reset-password-error" className="field-error">
              {errors.password}
            </p>
          )}
        </div>
        <div className="field">
          <label htmlFor="reset-confirmation">Confirmar contraseña</label>
          <input
            id="reset-confirmation"
            type="password"
            autoComplete="new-password"
            value={confirmation}
            onChange={(event) => setConfirmation(event.target.value)}
            aria-invalid={errors.confirmation ? true : undefined}
            aria-describedby={errors.confirmation ? 'reset-confirmation-error' : undefined}
          />
          {errors.confirmation && (
            <p id="reset-confirmation-error" className="field-error">
              {errors.confirmation}
            </p>
          )}
        </div>
        <button type="submit" className="button" disabled={submitting}>
          {submitting ? 'Guardando…' : 'Guardar contraseña'}
        </button>
      </form>
    </section>
  );
}
