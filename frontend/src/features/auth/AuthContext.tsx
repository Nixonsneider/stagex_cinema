import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react';
import * as authApi from '../../services/api/authApi';
import { setAccessToken } from '../../services/api/httpClient';
import type { LoginRequest, Role, SessionUser } from '../../types/auth';

interface Session {
  user: SessionUser;
  refreshToken: string;
}

interface AuthContextValue {
  user: SessionUser | null;
  login: (credentials: LoginRequest) => Promise<SessionUser>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

/**
 * Sesión del usuario (T-020). Los tokens viven solo en memoria, como pide
 * docs/FRONTEND.md: al recargar la página hay que iniciar sesión otra vez hasta
 * que el backend entregue el refresh token en una cookie HttpOnly (T-016).
 * El frontend solo usa el rol para mostrar u ocultar pantallas; el backend
 * vuelve a validar el permiso en cada petición.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(null);

  const login = useCallback(async (credentials: LoginRequest) => {
    const response = await authApi.login({ email: credentials.email.trim().toLowerCase(), password: credentials.password });
    setAccessToken(response.accessToken);
    setSession({ user: response.user, refreshToken: response.refreshToken });
    return response.user;
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = session?.refreshToken;
    // La sesión local se cierra aunque falle la revocación en el servidor.
    setAccessToken(null);
    setSession(null);
    if (refreshToken) {
      try {
        await authApi.logout(refreshToken);
      } catch {
        // El refresh token vence solo; no se bloquea al usuario por este error.
      }
    }
  }, [session]);

  const value = useMemo<AuthContextValue>(
    () => ({ user: session?.user ?? null, login, logout }),
    [session, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de <AuthProvider>');
  }
  return context;
}

/** Pantalla inicial de cada rol después de iniciar sesión (CU-02, paso 6). */
export function homePathFor(role: Role): string {
  switch (role) {
    case 'CASHIER':
      return '/access/validate';
    case 'CINEMA_ADMIN':
      return '/admin/movies';
    default:
      return '/';
  }
}
