/** Roles de Stage X V1.0 (docs/V1.0_ALCANCE_Y_BACKLOG.md). */
export type Role = 'CUSTOMER' | 'CASHIER' | 'CINEMA_ADMIN';

/** Usuario de la sesión tal como lo devuelve `POST /auth/login`. */
export interface SessionUser {
  id: string;
  name: string;
  role: Role;
}

export interface LoginRequest {
  email: string;
  password: string;
}

/** Respuesta de `POST /auth/login` y `POST /auth/refresh` (docs/API.md). */
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: 'Bearer';
  /** Segundos de vida del access token. */
  expiresIn: number;
  user: SessionUser;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

/** Respuesta `201` de `POST /auth/register`. */
export interface RegisterResponse {
  userId: string;
  status: 'PENDING_VERIFICATION';
}
