import type { SeatStatus, SeatView } from '../../types/booking';

type SeatState = Lowercase<SeatStatus> | 'selected';

const STATE_LABELS: Record<SeatState, string> = {
  available: 'disponible',
  selected: 'seleccionado',
  held: 'reservado por otra persona',
  sold: 'vendido',
  unavailable: 'no disponible',
};

const LEGEND: ReadonlyArray<{ state: SeatState; label: string }> = [
  { state: 'available', label: 'Disponible' },
  { state: 'selected', label: 'Seleccionado' },
  { state: 'held', label: 'Reservado' },
  { state: 'sold', label: 'Vendido' },
  { state: 'unavailable', label: 'No disponible' },
];

/** Agrupa los asientos por fila conservando el orden que envía el backend. */
function groupByRow(seats: SeatView[]): Array<[string, SeatView[]]> {
  const rows = new Map<string, SeatView[]>();
  for (const seat of seats) {
    rows.set(seat.row, [...(rows.get(seat.row) ?? []), seat]);
  }
  return [...rows.entries()];
}

export function seatName(seat: Pick<SeatView, 'row' | 'number'>): string {
  return `${seat.row}${seat.number}`;
}

interface SeatMapProps {
  seats: SeatView[];
  selected: ReadonlySet<string>;
  /** Con una reserva activa el mapa queda de solo lectura. */
  locked: boolean;
  onToggle: (seat: SeatView) => void;
}

/**
 * Mapa de asientos (T-044). Cada asiento es un botón con su estado en texto, no solo en
 * color, como pide docs/FRONTEND.md. El frontend no decide la disponibilidad: solo
 * muestra la que envía el backend.
 */
export function SeatMap({ seats, selected, locked, onToggle }: SeatMapProps) {
  return (
    <div className="seat-map-wrapper">
      <ul className="seat-legend" aria-label="Convenciones">
        {LEGEND.map((item) => (
          <li key={item.state}>
            <span className={`seat-swatch seat-${item.state}`} aria-hidden="true" />
            {item.label}
          </li>
        ))}
      </ul>
      <div className="seat-map" role="group" aria-label="Mapa de asientos">
        <div className="seat-screen" aria-hidden="true">
          Pantalla
        </div>
        {groupByRow(seats).map(([row, rowSeats]) => (
          <div key={row} className="seat-row">
            <span className="seat-row-label" aria-hidden="true">
              {row}
            </span>
            {rowSeats.map((seat) => {
              const isSelected = selected.has(seat.id);
              const state: SeatState = isSelected ? 'selected' : (seat.status.toLowerCase() as SeatState);
              return (
                <button
                  key={seat.id}
                  type="button"
                  className={`seat seat-${state}`}
                  aria-pressed={isSelected}
                  aria-label={`Fila ${seat.row}, asiento ${seat.number}, ${STATE_LABELS[state]}`}
                  disabled={locked || (!isSelected && seat.status !== 'AVAILABLE')}
                  onClick={() => onToggle(seat)}
                >
                  {seat.number}
                </button>
              );
            })}
          </div>
        ))}
      </div>
    </div>
  );
}
