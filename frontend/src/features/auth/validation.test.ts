import { describe, expect, it } from 'vitest';
import { hasErrors, validateEmail, validateLogin, validateNewPassword, validateRegister } from './validation';

describe('validaciones de acceso', () => {
  it('exige un correo con formato válido', () => {
    expect(validateEmail('ana@correo.com')).toBeUndefined();
    expect(validateEmail('  ana@correo.com  ')).toBeUndefined();
    expect(validateEmail('')).toBe('Escribe tu correo electrónico.');
    expect(validateEmail('ana@correo')).toBe('Escribe un correo válido, por ejemplo ana@correo.com.');
    expect(validateEmail('ana correo@x.com')).toBe('Escribe un correo válido, por ejemplo ana@correo.com.');
  });

  it('exige contraseñas de al menos 8 caracteres con letras y números', () => {
    expect(validateNewPassword('Segura2026')).toBeUndefined();
    expect(validateNewPassword('Ñandú1234')).toBeUndefined();
    expect(validateNewPassword('abc123')).toBe('La contraseña debe tener al menos 8 caracteres.');
    expect(validateNewPassword('solamenteletras')).toBe('La contraseña debe combinar letras y números.');
    expect(validateNewPassword('1234567890')).toBe('La contraseña debe combinar letras y números.');
  });

  it('valida el inicio de sesión sin imponer reglas de contraseña nueva', () => {
    expect(hasErrors(validateLogin('ana@correo.com', 'x'))).toBe(false);
    expect(validateLogin('', '')).toEqual({
      email: 'Escribe tu correo electrónico.',
      password: 'Escribe tu contraseña.',
    });
  });

  it('valida todos los campos del registro', () => {
    expect(hasErrors(validateRegister('Ana', 'ana@correo.com', 'Segura2026', 'Segura2026'))).toBe(false);
    expect(validateRegister('  ', 'ana@correo.com', 'Segura2026', 'Otra2026')).toEqual({
      name: 'Escribe tu nombre.',
      confirmation: 'Las contraseñas no coinciden.',
    });
  });
});
