import { Link, NavLink, Outlet } from 'react-router';

export function Layout() {
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
            <li>
              <NavLink to="/my-tickets">Mis entradas</NavLink>
            </li>
            <li>
              <NavLink to="/login">Iniciar sesión</NavLink>
            </li>
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
