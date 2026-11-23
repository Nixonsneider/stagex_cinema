import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { beforeEach, describe, expect, it } from 'vitest';
import { AppRoutes } from '../../app/AppRoutes';
import { DEMO_PASSWORD, resetMockAuth } from '../../services/api/mock/mockAuth';
import { AuthProvider } from './AuthContext';

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </MemoryRouter>,
  );
}

async function signIn(user: ReturnType<typeof userEvent.setup>, email: string, password = DEMO_PASSWORD) {
  await user.type(screen.getByLabelText('Correo electrónico'), email);
  await user.type(screen.getByLabelText('Contraseña'), password);
  await user.click(screen.getByRole('button', { name: 'Ingresar' }));
}

beforeEach(() => {
  resetMockAuth();
});

describe('inicio de sesión', () => {
  it('lleva al cliente sin sesión al login y luego a la ruta que pidió', async () => {
    const user = userEvent.setup();
    renderAt('/my-tickets');

    expect(screen.getByRole('heading', { name: 'Iniciar sesión' })).toBeInTheDocument();
    expect(screen.getByText('Inicia sesión para continuar.')).toBeInTheDocument();

    await signIn(user, 'cliente@stagex.test');

    expect(await screen.findByRole('heading', { name: 'Mis entradas' })).toBeInTheDocument();
    expect(screen.getByText('Cliente Demo')).toBeInTheDocument();
  });

  it('envía a cada rol a su página de inicio', async () => {
    const user = userEvent.setup();
    renderAt('/login');

    await signIn(user, 'TAQUILLA@stagex.test ');

    expect(await screen.findByRole('heading', { name: 'Validar entradas' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Ventas' })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Mis entradas' })).not.toBeInTheDocument();
  });

  it('lleva al administrador al panel de películas', async () => {
    const user = userEvent.setup();
    renderAt('/login');

    await signIn(user, 'admin@stagex.test');

    expect(await screen.findByRole('heading', { name: 'Administrar películas' })).toBeInTheDocument();
  });

  it('niega una ruta de otro rol', async () => {
    const user = userEvent.setup();
    renderAt('/admin/movies');

    await signIn(user, 'cliente@stagex.test');

    expect(await screen.findByRole('heading', { name: 'Acceso no permitido' })).toBeInTheDocument();
    expect(screen.queryByRole('heading', { name: 'Administrar películas' })).not.toBeInTheDocument();
  });

  it('no revela si falló el correo o la contraseña', async () => {
    const user = userEvent.setup();
    renderAt('/login');

    await signIn(user, 'cliente@stagex.test', 'incorrecta1');
    expect(await screen.findByRole('alert')).toHaveTextContent('Correo o contraseña incorrectos.');
    expect(screen.getByLabelText('Contraseña')).toHaveValue('');

    await user.clear(screen.getByLabelText('Correo electrónico'));
    await signIn(user, 'nadie@stagex.test');
    expect(await screen.findByRole('alert')).toHaveTextContent('Correo o contraseña incorrectos.');
  });

  it('avisa cuando la cuenta no está verificada', async () => {
    const user = userEvent.setup();
    renderAt('/login');

    await signIn(user, 'pendiente@stagex.test');

    expect(await screen.findByRole('alert')).toHaveTextContent('Tu cuenta aún no está verificada');
  });

  it('valida el formulario antes de enviar', async () => {
    const user = userEvent.setup();
    renderAt('/login');

    await user.click(screen.getByRole('button', { name: 'Ingresar' }));

    expect(screen.getByText('Escribe tu correo electrónico.')).toBeInTheDocument();
    expect(screen.getByText('Escribe tu contraseña.')).toBeInTheDocument();
    expect(screen.getByLabelText('Correo electrónico')).toHaveAttribute('aria-invalid', 'true');
  });

  it('cierra la sesión y vuelve a proteger las rutas', async () => {
    const user = userEvent.setup();
    renderAt('/login');
    await signIn(user, 'cliente@stagex.test');
    await screen.findByRole('button', { name: 'Cerrar sesión' });

    await user.click(screen.getByRole('button', { name: 'Cerrar sesión' }));

    expect(await screen.findByRole('heading', { name: 'Cartelera' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Iniciar sesión' })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Mis entradas' })).not.toBeInTheDocument();
  });
});

describe('registro', () => {
  async function fillRegister(user: ReturnType<typeof userEvent.setup>, email: string) {
    await user.type(screen.getByLabelText('Nombre completo'), 'Ana Pérez');
    await user.type(screen.getByLabelText('Correo electrónico'), email);
    await user.type(screen.getByLabelText('Contraseña'), 'Segura2026');
    await user.type(screen.getByLabelText('Confirmar contraseña'), 'Segura2026');
    await user.click(screen.getByRole('button', { name: 'Crear cuenta' }));
  }

  it('crea la cuenta pendiente de verificación', async () => {
    const user = userEvent.setup();
    renderAt('/register');

    await fillRegister(user, 'Ana@Correo.com');

    expect(await screen.findByRole('heading', { name: 'Revisa tu correo' })).toBeInTheDocument();
    expect(screen.getByText('ana@correo.com')).toBeInTheDocument();
  });

  it('no permite registrar un correo existente', async () => {
    const user = userEvent.setup();
    renderAt('/register');

    await fillRegister(user, 'cliente@stagex.test');

    expect(await screen.findByRole('alert')).toHaveTextContent('Ya existe una cuenta con este correo');
  });

  it('muestra los errores de cada campo', async () => {
    const user = userEvent.setup();
    renderAt('/register');

    await user.type(screen.getByLabelText('Correo electrónico'), 'correo-invalido');
    await user.type(screen.getByLabelText('Contraseña'), 'corta');
    await user.type(screen.getByLabelText('Confirmar contraseña'), 'otra');
    await user.click(screen.getByRole('button', { name: 'Crear cuenta' }));

    expect(screen.getByText('Escribe tu nombre.')).toBeInTheDocument();
    expect(screen.getByText(/Escribe un correo válido/)).toBeInTheDocument();
    expect(screen.getByText(/al menos 8 caracteres/)).toBeInTheDocument();
    expect(screen.getByText('Las contraseñas no coinciden.')).toBeInTheDocument();
  });

  it('una cuenta recién creada no puede entrar hasta verificarse', async () => {
    const user = userEvent.setup();
    renderAt('/register');
    await fillRegister(user, 'nueva@correo.com');
    await user.click(await screen.findByRole('link', { name: 'Ir a iniciar sesión' }));

    await signIn(user, 'nueva@correo.com', 'Segura2026');

    expect(await screen.findByRole('alert')).toHaveTextContent('Tu cuenta aún no está verificada');
  });
});

describe('recuperar contraseña', () => {
  it('responde igual exista o no el correo', async () => {
    const user = userEvent.setup();
    renderAt('/forgot-password');

    await user.type(screen.getByLabelText('Correo electrónico'), 'nadie@correo.com');
    await user.click(screen.getByRole('button', { name: 'Enviar enlace' }));

    expect(await screen.findByText(/Si el correo está registrado/)).toBeInTheDocument();
  });
});
