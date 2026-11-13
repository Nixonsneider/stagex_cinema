import { Route, Routes } from 'react-router';
import { Layout } from '../components/Layout';
import { BillboardPage } from '../pages/BillboardPage';
import { MovieDetailPage } from '../pages/MovieDetailPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { PlaceholderPage } from '../pages/PlaceholderPage';

/**
 * Rutas de docs/FRONTEND.md. Las que aún no están implementadas muestran una página base.
 * La protección por rol de las rutas privadas se agrega en T-020.
 */
const PENDING_ROUTES: Array<{ path: string; title: string; task: string }> = [
  { path: 'screenings/:screeningId/seats', title: 'Selección de asientos', task: 'T-044' },
  { path: 'checkout/:orderId', title: 'Resumen y pago', task: 'T-045' },
  { path: 'confirmation/:orderId', title: 'Confirmación de compra', task: 'T-045' },
  { path: 'login', title: 'Iniciar sesión', task: 'T-020' },
  { path: 'register', title: 'Crear cuenta', task: 'T-020' },
  { path: 'forgot-password', title: 'Recuperar contraseña', task: 'T-020' },
  { path: 'my-tickets', title: 'Mis entradas', task: 'T-052' },
  { path: 'my-tickets/:ticketId', title: 'Detalle de entrada', task: 'T-052' },
  { path: 'admin/movies', title: 'Administrar películas', task: 'T-034' },
  { path: 'admin/rooms', title: 'Administrar salas', task: 'T-035' },
  { path: 'admin/screenings', title: 'Administrar funciones', task: 'T-036' },
  { path: 'admin/sales', title: 'Ventas', task: 'T-055' },
  { path: 'access/validate', title: 'Validar entradas', task: 'T-055' },
];

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<BillboardPage />} />
        <Route path="movies/:movieId" element={<MovieDetailPage />} />
        {PENDING_ROUTES.map((route) => (
          <Route key={route.path} path={route.path} element={<PlaceholderPage title={route.title} task={route.task} />} />
        ))}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
