import type { LoginResponse, RegisterRequest, RegisterResponse, Role, SessionUser } from '../../../types/auth';
import { ApiError } from '../httpClient';

/**
 * Simulación del backend de autenticación para desarrollar sin API (T-020).
 * Replica las respuestas de docs/API.md mientras T-016 no exponga `/auth/*`:
 * credenciales inválidas siempre dan el mismo error, una cuenta pendiente no
 * puede entrar y un correo repetido no se puede registrar.
 *
 * Solo existe en desarrollo y pruebas: las contraseñas de abajo no son reales.
 */

interface MockAccount extends SessionUser {
  email: string;
  password: string;
  status: 'PENDING_VERIFICATION' | 'ACTIVE';
}

/** Contraseña de las cuentas demo (solo datos de prueba). */
export const DEMO_PASSWORD = 'StageX2026';

const DEMO_ACCOUNTS: ReadonlyArray<MockAccount> = [
  demo('usr-cliente', 'Cliente Demo', 'cliente@stagex.test', 'CUSTOMER', 'ACTIVE'),
  demo('usr-taquilla', 'Taquilla Demo', 'taquilla@stagex.test', 'CASHIER', 'ACTIVE'),
  demo('usr-admin', 'Administrador Demo', 'admin@stagex.test', 'CINEMA_ADMIN', 'ACTIVE'),
  demo('usr-pendiente', 'Cliente Pendiente', 'pendiente@stagex.test', 'CUSTOMER', 'PENDING_VERIFICATION'),
];

let accounts: MockAccount[] = [...DEMO_ACCOUNTS];
let sequence = 0;

function demo(id: string, name: string, email: string, role: Role, status: MockAccount['status']): MockAccount {
  return { id, name, email, role, status, password: DEMO_PASSWORD };
}

/** Vuelve a las cuentas demo; lo usan las pruebas para no depender del orden. */
export function resetMockAuth(): void {
  accounts = [...DEMO_ACCOUNTS];
  sequence = 0;
}

function normalizeEmail(email: string): string {
  return email.trim().toLowerCase();
}

function randomToken(): string {
  sequence += 1;
  return `mock-${Date.now().toString(36)}-${sequence}`;
}

export function mockLogin(email: string, password: string): LoginResponse {
  const account = accounts.find((candidate) => candidate.email === normalizeEmail(email));
  if (!account || account.password !== password) {
    throw new ApiError(401, 'INVALID_CREDENTIALS', 'Correo o contraseña incorrectos.');
  }
  if (account.status === 'PENDING_VERIFICATION') {
    throw new ApiError(
      403,
      'ACCOUNT_PENDING_VERIFICATION',
      'Tu cuenta aún no está verificada. Revisa tu correo y abre el enlace de confirmación.',
    );
  }
  return {
    accessToken: randomToken(),
    refreshToken: randomToken(),
    tokenType: 'Bearer',
    expiresIn: 900,
    user: { id: account.id, name: account.name, role: account.role },
  };
}

export function mockRegister({ name, email, password }: RegisterRequest): RegisterResponse {
  const normalized = normalizeEmail(email);
  if (accounts.some((account) => account.email === normalized)) {
    throw new ApiError(409, 'EMAIL_ALREADY_REGISTERED', 'Ya existe una cuenta con este correo. Inicia sesión o recupera tu contraseña.');
  }
  sequence += 1;
  const id = `usr-nuevo-${sequence}`;
  accounts = [
    ...accounts,
    { id, name: name.trim(), email: normalized, password, role: 'CUSTOMER', status: 'PENDING_VERIFICATION' },
  ];
  return { userId: id, status: 'PENDING_VERIFICATION' };
}
