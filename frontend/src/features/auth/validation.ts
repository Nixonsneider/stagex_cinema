/**
 * Validaciones de formulario para orientar al usuario antes de enviar.
 * No reemplazan las del backend, que es quien decide (Bean Validation).
 */

export const PASSWORD_MIN_LENGTH = 8;

const EMAIL_FORMAT = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export type FieldErrors<T extends string> = Partial<Record<T, string>>;

export function validateEmail(email: string): string | undefined {
  const value = email.trim();
  if (!value) return 'Escribe tu correo electrónico.';
  if (!EMAIL_FORMAT.test(value)) return 'Escribe un correo válido, por ejemplo ana@correo.com.';
  return undefined;
}

export function validateNewPassword(password: string): string | undefined {
  if (password.length < PASSWORD_MIN_LENGTH) {
    return `La contraseña debe tener al menos ${PASSWORD_MIN_LENGTH} caracteres.`;
  }
  if (!/\p{L}/u.test(password) || !/\d/.test(password)) {
    return 'La contraseña debe combinar letras y números.';
  }
  return undefined;
}

export function validateLogin(email: string, password: string): FieldErrors<'email' | 'password'> {
  const errors: FieldErrors<'email' | 'password'> = {};
  const emailError = validateEmail(email);
  if (emailError) errors.email = emailError;
  if (!password) errors.password = 'Escribe tu contraseña.';
  return errors;
}

export function validateRegister(
  name: string,
  email: string,
  password: string,
  confirmation: string,
): FieldErrors<'name' | 'email' | 'password' | 'confirmation'> {
  const errors: FieldErrors<'name' | 'email' | 'password' | 'confirmation'> = {};
  if (!name.trim()) errors.name = 'Escribe tu nombre.';
  const emailError = validateEmail(email);
  if (emailError) errors.email = emailError;
  const passwordError = validateNewPassword(password);
  if (passwordError) errors.password = passwordError;
  if (confirmation !== password) errors.confirmation = 'Las contraseñas no coinciden.';
  return errors;
}

export function hasErrors(errors: object): boolean {
  return Object.keys(errors).length > 0;
}
