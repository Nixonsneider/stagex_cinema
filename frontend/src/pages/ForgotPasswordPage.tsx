import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { validateEmail } from '../features/auth/validation';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { requestPasswordReset } from '../services/api/authApi';
import { ApiError } from '../services/api/httpClient';

export function ForgotPasswordPage() {
  useDocumentTitle('Recuperar contraseña');
  const [email, setEmail] = useState('');
  const [error, setError] = useState<string | undefined>();
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [sent, setSent] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting) return;
    const validation = validateEmail(email);
    setError(validation);
    setFormError(null);
    if (validation) return;

    setSubmitting(true);
    try {
      await requestPasswordReset(email.trim().toLowerCase());
      setSent(true);
    } catch (requestError) {
      setFormError(
        requestError instanceof ApiError ? requestError.message : 'No pudimos enviar la solicitud. Intenta de nuevo.',
      );
    } finally {
      setSubmitting(false);
    }
  }

  if (sent) {
    // Mensaje neutro: no revela si el correo está registrado (REQ-005).
    return (
      <section className="auth-page">
        <h1>Revisa tu correo</h1>
        <p role="status">
          Si el correo está registrado, te enviamos un enlace temporal para crear una contraseña nueva.
        </p>
        <p>
          <Link to="/login">Volver a iniciar sesión</Link>
        </p>
      </section>
    );
  }

  return (
    <section className="auth-page">
      <h1>Recuperar contraseña</h1>
      <p>Escribe el correo de tu cuenta y te enviaremos un enlace para crear una contraseña nueva.</p>
      {formError && (
        <p className="form-error" role="alert">
          {formError}
        </p>
      )}
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        <div className="field">
          <label htmlFor="forgot-email">Correo electrónico</label>
          <input
            id="forgot-email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            aria-invalid={error ? true : undefined}
            aria-describedby={error ? 'forgot-email-error' : undefined}
          />
          {error && (
            <p id="forgot-email-error" className="field-error">
              {error}
            </p>
          )}
        </div>
        <button type="submit" className="button" disabled={submitting}>
          {submitting ? 'Enviando…' : 'Enviar enlace'}
        </button>
      </form>
      <p>
        <Link to="/login">Volver a iniciar sesión</Link>
      </p>
    </section>
  );
}
