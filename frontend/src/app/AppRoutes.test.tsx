import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router';
import { describe, expect, it } from 'vitest';
import { AppRoutes } from './AppRoutes';

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <AppRoutes />
    </MemoryRouter>,
  );
}

describe('rutas de la aplicación', () => {
  it('muestra la cartelera con las películas disponibles', async () => {
    renderAt('/');

    expect(screen.getByRole('heading', { level: 1, name: 'Cartelera' })).toBeInTheDocument();
    expect(await screen.findByRole('link', { name: 'Órbita Cero' })).toBeInTheDocument();
    expect(screen.queryByText('Proyecto Retirado')).not.toBeInTheDocument();
  });

  it('busca por título', async () => {
    const user = userEvent.setup();
    renderAt('/');
    await screen.findByRole('link', { name: 'Órbita Cero' });

    await user.type(screen.getByLabelText('Buscar por título'), 'faro');
    await user.click(screen.getByRole('button', { name: 'Buscar' }));

    expect(await screen.findByText('1 película en cartelera')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'El Último Faro' })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Órbita Cero' })).not.toBeInTheDocument();
  });

  it('informa cuando la búsqueda no tiene resultados', async () => {
    renderAt('/?q=inexistente');

    expect(await screen.findByText(/No encontramos películas/)).toBeInTheDocument();
  });

  it('navega de la cartelera a la ficha con sus funciones', async () => {
    const user = userEvent.setup();
    renderAt('/');

    await user.click(await screen.findByRole('link', { name: 'Risas en el Barrio' }));

    expect(await screen.findByRole('heading', { level: 1, name: 'Risas en el Barrio' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Funciones disponibles' })).toBeInTheDocument();
    expect(screen.getAllByRole('link', { name: /Elegir asientos/ }).length).toBeGreaterThan(0);
  });

  it('muestra un mensaje claro si la película no existe', async () => {
    renderAt('/movies/no-existe');

    expect(await screen.findByRole('heading', { name: 'Película no disponible' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Volver a la cartelera' })).toBeInTheDocument();
  });

  it('muestra la sinopsis como texto y no como HTML', async () => {
    renderAt('/movies/mov-001');

    expect(await screen.findByText(/guardiana de faro/)).toBeInTheDocument();
  });

  it('tiene páginas base para las rutas pendientes y una página 404', () => {
    renderAt('/login');
    expect(screen.getByRole('heading', { name: 'Iniciar sesión' })).toBeInTheDocument();
  });

  it('muestra 404 en rutas desconocidas', () => {
    renderAt('/ruta-que-no-existe');
    expect(screen.getByRole('heading', { name: 'Página no encontrada' })).toBeInTheDocument();
  });
});
