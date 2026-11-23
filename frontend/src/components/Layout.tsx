import { Link, NavLink, Outlet, useNavigate } from 'react-router';
import { useAuth } from '../features/auth/AuthContext';
import type { Role } from '../types/auth';

/** Enlaces del menú visibles para cada rol. Ocultarlos no da ni quita permisos. */
const ROLE_LINKS: Record<Role, Array<{ to: string; label: string }>> = {
  CUSTOMER: [{ to: '/my-tickets', label: 'Mis entradas' }],
  CASHIER: [
    { to: '/access/validate', label: 'Validar entradas' },
    { to: '/admin/sales', label: 'Ventas' },
  ],
  CINEMA_ADMIN: [
    { to: '/admin/movies', label: 'Películas' },
    { to: '/admin/rooms', label: 'Salas' },
    { to: '/admin/screenings', label: 'Funciones' },
    { to: '/admin/sales', label: 'Ventas' },
  ],
};

export function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate('/', { replace: true });
  }

  return (
    <div className="app">
      <a className="skip-link" href="#contenido">
        Saltar al contenido
      </a>
      <header className="app-header">
        <Link to="/" className="brand">
          Stage X
        </Link>
        <nav aria-label="Principal">
          <ul className="nav-list">
            <li>
              <NavLink to="/" end>
                Cartelera
              </NavLink>
            </li>
            {user ? (
              <>
                {ROLE_LINKS[user.role].map((link) => (
                  <li key={link.to}>
                    <NavLink to={link.to}>{link.label}</NavLink>
                  </li>
                ))}
                <li>
                  <span className="nav-user">{user.name}</span>
                </li>
                <li>
                  <button type="button" className="nav-button" onClick={handleLogout}>
                    Cerrar sesión
                  </button>
                </li>
              </>
            ) : (
              <>
                <li>
                  <NavLink to="/login">Iniciar sesión</NavLink>
                </li>
                <li>
                  <NavLink to="/register">Crear cuenta</NavLink>
                </li>
              </>
            )}
          </ul>
        </nav>
      </header>
      <main id="contenido" className="app-main" tabIndex={-1}>
        <Outlet />
      </main>
      <footer className="app-footer">
        <p>Stage X · Venta de entradas de cine</p>
      </footer>
    </div>
  );
}
