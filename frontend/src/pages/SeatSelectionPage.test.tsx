import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router';
import { beforeEach, describe, expect, it } from 'vitest';
import { AuthProvider } from '../features/auth/AuthContext';
import { resetMockHolds } from '../services/api/mock/mockBooking';
import { SeatSelectionPage } from './SeatSelectionPage';

function renderSeats(screeningId = 'scr-001') {
  return render(
    <MemoryRouter initialEntries={[`/screenings/${screeningId}/seats`]}>
      <AuthProvider>
        <Routes>
          <Route path="screenings/:screeningId/seats" element={<SeatSelectionPage />} />
          <Route path="checkout/:orderId" element={<h1>Resumen y pago</h1>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  );
}

describe('selección de asientos (T-044)', () => {
  beforeEach(() => {
    resetMockHolds();
  });

  it('muestra el mapa con el estado de cada asiento en texto', async () => {
    renderSeats();

    expect(await screen.findByRole('heading', { level: 1, name: 'Elige tus asientos' })).toBeInTheDocument();
    const map = screen.getByRole('group', { name: 'Mapa de asientos' });
    expect(within(map).getByRole('button', { name: 'Fila A, asiento 1, disponible' })).toBeEnabled();
    // Los vendidos, reservados y no disponibles no se pueden elegir.
    expect(within(map).getAllByRole('button', { name: /vendido/ })[0]).toBeDisabled();
    expect(within(map).getAllByRole('button', { name: /reservado por otra persona/ })[0]).toBeDisabled();
    expect(within(map).getByRole('button', { name: 'Fila F, asiento 10, no disponible' })).toBeDisabled();
  });

  it('reserva como invitado y muestra el total y el contador', async () => {
    const user = userEvent.setup();
    renderSeats();

    await user.click(await screen.findByRole('button', { name: 'Fila A, asiento 1, disponible' }));
    await user.click(screen.getByRole('button', { name: 'Fila A, asiento 2, disponible' }));
    expect(screen.getByRole('button', { name: 'Fila A, asiento 1, seleccionado' })).toHaveAttribute(
      'aria-pressed',
      'true',
    );
    expect(screen.getByText('A1, A2')).toBeInTheDocument();

    // Sin sesión pide el correo antes de reservar.
    await user.click(screen.getByRole('button', { name: 'Reservar asientos' }));
    expect(screen.getByText('Escribe tu correo electrónico.')).toBeInTheDocument();

    await user.type(screen.getByLabelText('Correo para recibir tus entradas'), 'ana@correo.com');
    await user.click(screen.getByRole('button', { name: 'Reservar asientos' }));

    expect(await screen.findByRole('heading', { name: 'Asientos reservados' })).toBeInTheDocument();
    expect(screen.getByRole('timer', { name: 'Tiempo restante de la reserva' })).toHaveTextContent(/^(10:0\d|9:\d\d)$/);
    expect(screen.getByRole('link', { name: 'Continuar al pago' })).toHaveAttribute('href', '/checkout/ord-0001');
    // Con la reserva activa el mapa queda bloqueado.
    expect(screen.getByRole('button', { name: 'Fila A, asiento 3, disponible' })).toBeDisabled();
  });

  it('libera la reserva para elegir otros asientos', async () => {
    const user = userEvent.setup();
    renderSeats();

    await user.click(await screen.findByRole('button', { name: 'Fila A, asiento 1, disponible' }));
    await user.type(screen.getByLabelText('Correo para recibir tus entradas'), 'ana@correo.com');
    await user.click(screen.getByRole('button', { name: 'Reservar asientos' }));
    await user.click(await screen.findByRole('button', { name: 'Cambiar asientos' }));

    expect(await screen.findByText('Liberamos tus asientos. Puedes elegir otros.')).toBeInTheDocument();
    expect(await screen.findByRole('button', { name: 'Fila A, asiento 1, disponible' })).toBeEnabled();
  });

  it('informa cuando la función no existe', async () => {
    renderSeats('no-existe');

    expect(await screen.findByRole('heading', { name: 'Función no disponible' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Volver a la cartelera' })).toBeInTheDocument();
  });
});
