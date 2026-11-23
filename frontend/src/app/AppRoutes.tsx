import { Route, Routes } from 'react-router';
import { Layout } from '../components/Layout';
import { RequireAuth } from '../features/auth/RequireAuth';
import { BillboardPage } from '../pages/BillboardPage';
import { ForgotPasswordPage } from '../pages/ForgotPasswordPage';
import { LoginPage } from '../pages/LoginPage';
import { MovieDetailPage } from '../pages/MovieDetailPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { PlaceholderPage } from '../pages/PlaceholderPage';
import { RegisterPage } from '../pages/RegisterPage';
import { ResetPasswordPage } from '../pages/ResetPasswordPage';
import { VerifyEmailPage } from '../pages/VerifyEmailPage';
import type { Role } from '../types/auth';

interface PendingRoute {
  path: string;
  title: string;
  task: string;
  /** Roles con acceso según docs/FRONTEND.md. Sin `roles`, la ruta es pública. */
  roles?: ReadonlyArray<Role>;
}

/**
 * Rutas de docs/FRONTEND.md. Las que aún no están implementadas muestran una página base.
 * Las rutas privadas exigen sesión y rol (T-020); el backend vuelve a validar el permiso.
 */
export const PENDING_ROUTES: ReadonlyArray<PendingRoute> = [
  { path: 'screenings/:screeningId/seats', title: 'Selección de asientos', task: 'T-044' },
  { path: 'checkout/:orderId', title: 'Resumen y pago', task: 'T-045' },
  { path: 'confirmation/:orderId', title: 'Confirmación de compra', task: 'T-045' },
  { path: 'my-tickets', title: 'Mis entradas', task: 'T-052', roles: ['CUSTOMER'] },
  { path: 'my-tickets/:ticketId', title: 'Detalle de entrada', task: 'T-052', roles: ['CUSTOMER'] },
  { path: 'admin/movies', title: 'Administrar películas', task: 'T-034', roles: ['CINEMA_ADMIN'] },
  { path: 'admin/rooms', title: 'Administrar salas', task: 'T-035', roles: ['CINEMA_ADMIN'] },
  { path: 'admin/screenings', title: 'Administrar funciones', task: 'T-036', roles: ['CINEMA_ADMIN'] },
  { path: 'admin/sales', title: 'Ventas', task: 'T-055', roles: ['CASHIER', 'CINEMA_ADMIN'] },
  { path: 'access/validate', title: 'Validar entradas', task: 'T-055', roles: ['CASHIER'] },
];

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<BillboardPage />} />
        <Route path="movies/:movieId" element={<MovieDetailPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="forgot-password" element={<ForgotPasswordPage />} />
        <Route path="verify-email" element={<VerifyEmailPage />} />
        <Route path="reset-password" element={<ResetPasswordPage />} />
        {PENDING_ROUTES.map((route) => {
          const page = <PlaceholderPage title={route.title} task={route.task} />;
          return (
            <Route
              key={route.path}
              path={route.path}
              element={route.roles ? <RequireAuth roles={route.roles}>{page}</RequireAuth> : page}
            />
          );
        })}
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
