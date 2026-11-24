/** Error de API con un mensaje apto para mostrar al usuario (sin detalles técnicos). */
export class ApiError extends Error {
  readonly status: number;
  readonly code: string;

  constructor(status: number, code: string, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.code = code;
  }
}

const API_URL = (import.meta.env.VITE_API_URL ?? '').replace(/\/+$/, '');

/** Usa datos de prueba si no hay backend configurado o si se fuerza con VITE_USE_MOCKS=true. */
export const USE_MOCKS = import.meta.env.VITE_USE_MOCKS === 'true' || API_URL === '';

const FALLBACK_MESSAGES: Record<number, string> = {
  400: 'Revisa los datos e intenta de nuevo.',
  401: 'Tu sesión expiró. Inicia sesión de nuevo.',
  403: 'No tienes permiso para ver esta información.',
  404: 'No encontramos lo que buscas.',
  409: 'La información cambió. Actualiza la página e intenta de nuevo.',
};

type QueryParams = Record<string, string | number | undefined>;

/**
 * Access token de la sesión actual. Vive solo en memoria (docs/FRONTEND.md): no se
 * guarda en localStorage para que un script inyectado no pueda leerlo después.
 */
let accessToken: string | null = null;

export function setAccessToken(token: string | null): void {
  accessToken = token;
}

async function request<T>(method: 'GET' | 'POST' | 'DELETE', url: URL, body?: unknown): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' };
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (accessToken) headers.Authorization = `Bearer ${accessToken}`;

  let response: Response;
  try {
    response = await fetch(url, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
      credentials: 'include',
    });
  } catch {
    throw new ApiError(0, 'NETWORK_ERROR', 'No pudimos conectar con el servidor. Revisa tu conexión e intenta de nuevo.');
  }

  if (!response.ok) {
    let code = 'HTTP_ERROR';
    let message = FALLBACK_MESSAGES[response.status] ?? 'Ocurrió un problema. Intenta de nuevo en unos minutos.';
    try {
      const errorBody: unknown = await response.json();
      if (errorBody && typeof errorBody === 'object') {
        const error = errorBody as { code?: unknown; message?: unknown };
        if (typeof error.code === 'string') code = error.code;
        if (typeof error.message === 'string' && response.status < 500) message = error.message;
      }
    } catch {
      // El cuerpo no es JSON: se conserva el mensaje genérico.
    }
    throw new ApiError(response.status, code, message);
  }

  // 202 y 204 no tienen cuerpo (por ejemplo, logout o recuperar contraseña).
  if (response.status === 202 || response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

export function apiGet<T>(path: string, params: QueryParams = {}): Promise<T> {
  const url = new URL(`${API_URL}/api/v1${path}`);
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== '') {
      url.searchParams.set(key, String(value));
    }
  }
  return request<T>('GET', url);
}

export function apiPost<T>(path: string, body: unknown): Promise<T> {
  return request<T>('POST', new URL(`${API_URL}/api/v1${path}`), body);
}

export function apiDelete<T>(path: string): Promise<T> {
  return request<T>('DELETE', new URL(`${API_URL}/api/v1${path}`));
}
