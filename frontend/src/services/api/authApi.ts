import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from '../../types/auth';
import { USE_MOCKS, apiPost } from './httpClient';
import { mockLogin, mockRegister } from './mock/mockAuth';

/** Simula la latencia de red en desarrollo para poder ver los estados de envío. */
const MOCK_DELAY_MS = import.meta.env.MODE === 'test' ? 0 : 300;

function fromMock<T>(produce: () => T): Promise<T> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        resolve(produce());
      } catch (error) {
        reject(error);
      }
    }, MOCK_DELAY_MS);
  });
}

export function login(credentials: LoginRequest): Promise<LoginResponse> {
  if (USE_MOCKS) return fromMock(() => mockLogin(credentials.email, credentials.password));
  return apiPost<LoginResponse>('/auth/login', credentials);
}

export function register(data: RegisterRequest): Promise<RegisterResponse> {
  if (USE_MOCKS) return fromMock(() => mockRegister(data));
  return apiPost<RegisterResponse>('/auth/register', data);
}

/** Siempre responde igual, exista o no el correo, para no revelar cuentas registradas. */
export function requestPasswordReset(email: string): Promise<void> {
  if (USE_MOCKS) return fromMock(() => undefined);
  return apiPost<void>('/auth/forgot-password', { email });
}

export function logout(refreshToken: string): Promise<void> {
  if (USE_MOCKS) return fromMock(() => undefined);
  return apiPost<void>('/auth/logout', { refreshToken });
}
