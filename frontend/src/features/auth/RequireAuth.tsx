import type { ReactNode } from 'react';
import { Link, Navigate, useLocation } from 'react-router';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import type { Role } from '../../types/auth';
import { homePathFor, useAuth } from './AuthContext';

interface RequireAuthProps {
  roles: ReadonlyArray<Role>;
  children: ReactNode;
}

/**
 * Protege una ruta privada (docs/FRONTEND.md). Sin sesión lleva al inicio de sesión
 * y recuerda a dónde quería ir el usuario; con otro rol muestra un aviso de acceso
 * denegado. Esto solo organiza la interfaz: el backend responde 403 de todos modos.
 */
export function RequireAuth({ roles, children }: RequireAuthProps) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" replace state={{ from: `${location.pathname}${location.search}` }} />;
  }
  if (!roles.includes(user.role)) {
    return <ForbiddenPage homePath={homePathFor(user.role)} />;
  }
  return <>{children}</>;
}

function ForbiddenPage({ homePath }: { homePath: string }) {
  useDocumentTitle('Acceso no permitido');
  return (
    <section>
      <h1>Acceso no permitido</h1>
      <p>Tu cuenta no tiene permiso para ver esta sección.</p>
      <p>
        <Link to={homePath}>Ir a tu página de inicio</Link>
      </p>
    </section>
  );
}
