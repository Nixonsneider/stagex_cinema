import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { hasErrors, PASSWORD_MIN_LENGTH, validateRegister, type FieldErrors } from '../features/auth/validation';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { register } from '../services/api/authApi';
import { ApiError } from '../services/api/httpClient';

type Field = 'name' | 'email' | 'password' | 'confirmation';

export function RegisterPage() {
  useDocumentTitle('Crear cuenta');
  const [values, setValues] = useState<Record<Field, string>>({ name: '', email: '', password: '', confirmation: '' });
  const [errors, setErrors] = useState<FieldErrors<Field>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState<string | null>(null);

  function update(field: Field, value: string) {
    setValues((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting) return;
    const validation = validateRegister(values.name, values.email, values.password, values.confirmation);
    setErrors(validation);
    setFormError(null);
    if (hasErrors(validation)) return;

    setSubmitting(true);
    const email = values.email.trim().toLowerCase();
    try {
      await register({ name: values.name.trim(), email, password: values.password });
      setRegisteredEmail(email);
    } catch (error) {
      setFormError(error instanceof ApiError ? error.message : 'No pudimos crear tu cuenta. Intenta de nuevo.');
    } finally {
      setSubmitting(false);
    }
  }

  if (registeredEmail) {
    return (
      <section className="auth-page">
        <h1>Revisa tu correo</h1>
        <p role="status">
          Creamos tu cuenta. Enviamos un enlace de confirmación a <strong>{registeredEmail}</strong>; ábrelo para
          activarla antes de iniciar sesión.
        </p>
        <p>
          <Link to="/login">Ir a iniciar sesión</Link>
        </p>
      </section>
    );
  }

  return (
    <section className="auth-page">
      <h1>Crear cuenta</h1>
      {formError && (
        <p className="form-error" role="alert">
          {formError}
        </p>
      )}
      <form className="auth-form" onSubmit={handleSubmit} noValidate>
        {renderField({ id: 'register-name', label: 'Nombre completo', autoComplete: 'name', field: 'name' })}
        {renderField({
          id: 'register-email',
          label: 'Correo electrónico',
          type: 'email',
          autoComplete: 'email',
          field: 'email',
        })}
        {renderField({
          id: 'register-password',
          label: 'Contraseña',
          type: 'password',
          autoComplete: 'new-password',
          field: 'password',
          hint: `Mínimo ${PASSWORD_MIN_LENGTH} caracteres, con letras y números.`,
        })}
        {renderField({
          id: 'register-confirmation',
          label: 'Confirmar contraseña',
          type: 'password',
          autoComplete: 'new-password',
          field: 'confirmation',
        })}
        <button type="submit" className="button" disabled={submitting}>
          {submitting ? 'Creando cuenta…' : 'Crear cuenta'}
        </button>
      </form>
      <p>
        ¿Ya tienes cuenta? <Link to="/login">Iniciar sesión</Link>
      </p>
    </section>
  );

  // Se llama como función (no como <Componente />) para no remontar el input en cada tecla.
  function renderField(props: { id: string; label: string; field: Field; type?: string; autoComplete: string; hint?: string }) {
    const error = errors[props.field];
    const describedBy = [props.hint ? `${props.id}-hint` : '', error ? `${props.id}-error` : ''].filter(Boolean).join(' ');
    return (
      <div className="field">
        <label htmlFor={props.id}>{props.label}</label>
        <input
          id={props.id}
          type={props.type ?? 'text'}
          autoComplete={props.autoComplete}
          value={values[props.field]}
          onChange={(event) => update(props.field, event.target.value)}
          aria-invalid={error ? true : undefined}
          aria-describedby={describedBy || undefined}
        />
        {props.hint && (
          <p id={`${props.id}-hint`} className="field-hint">
            {props.hint}
          </p>
        )}
        {error && (
          <p id={`${props.id}-error`} className="field-error">
            {error}
          </p>
        )}
      </div>
    );
  }
}
