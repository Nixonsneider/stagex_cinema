import { beforeEach, describe, expect, it } from 'vitest';
import { ApiError } from '../httpClient';
import { DEMO_PASSWORD, EXPIRED_MOCK_TOKEN, mockConsumeToken, mockLogin, mockRegister, resetMockAuth } from './mockAuth';

function errorOf(action: () => unknown): ApiError {
  try {
    action();
  } catch (error) {
    if (error instanceof ApiError) return error;
    throw error;
  }
  throw new Error('Se esperaba un ApiError');
}

beforeEach(() => {
  resetMockAuth();
});

describe('autenticación de prueba', () => {
  it('devuelve tokens y el rol del usuario', () => {
    const response = mockLogin(' Admin@StageX.test ', DEMO_PASSWORD);

    expect(response.tokenType).toBe('Bearer');
    expect(response.user).toEqual({ id: 'usr-admin', name: 'Administrador Demo', role: 'CINEMA_ADMIN' });
    expect(response.accessToken).not.toBe(response.refreshToken);
  });

  it('usa el mismo error para correo inexistente y contraseña incorrecta', () => {
    const wrongPassword = errorOf(() => mockLogin('cliente@stagex.test', 'otra'));
    const unknownEmail = errorOf(() => mockLogin('nadie@stagex.test', DEMO_PASSWORD));

    expect(wrongPassword.status).toBe(401);
    expect(wrongPassword.code).toBe('INVALID_CREDENTIALS');
    expect(unknownEmail.code).toBe(wrongPassword.code);
    expect(unknownEmail.message).toBe(wrongPassword.message);
  });

  it('solo revela que la cuenta está pendiente si la contraseña es correcta', () => {
    expect(errorOf(() => mockLogin('pendiente@stagex.test', 'otra')).code).toBe('INVALID_CREDENTIALS');
    const pending = errorOf(() => mockLogin('pendiente@stagex.test', DEMO_PASSWORD));
    expect(pending.status).toBe(403);
    expect(pending.code).toBe('ACCOUNT_PENDING_VERIFICATION');
  });

  it('registra clientes pendientes y rechaza correos repetidos', () => {
    expect(mockRegister({ name: 'Ana', email: 'Ana@Correo.com', password: 'Segura2026' }).status).toBe(
      'PENDING_VERIFICATION',
    );
    const duplicate = errorOf(() => mockRegister({ name: 'Otra', email: 'ana@correo.com', password: 'Segura2026' }));
    expect(duplicate.status).toBe(409);
    expect(duplicate.code).toBe('EMAIL_ALREADY_REGISTERED');
    expect(errorOf(() => mockLogin('ana@correo.com', 'Segura2026')).code).toBe('ACCOUNT_PENDING_VERIFICATION');
  });

  it('acepta cada enlace de correo una sola vez', () => {
    expect(() => mockConsumeToken('abc')).not.toThrow();
    expect(errorOf(() => mockConsumeToken('abc')).code).toBe('INVALID_OR_EXPIRED_TOKEN');
    expect(errorOf(() => mockConsumeToken(EXPIRED_MOCK_TOKEN)).status).toBe(400);
  });
});
