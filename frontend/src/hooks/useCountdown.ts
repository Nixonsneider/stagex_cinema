import { useEffect, useState } from 'react';

/**
 * Segundos que faltan para `deadline` (ISO 8601), actualizados cada segundo.
 * Devuelve `null` si no hay fecha límite y nunca baja de 0.
 */
export function useCountdown(deadline: string | null): number | null {
  const [now, setNow] = useState(() => Date.now());

  useEffect(() => {
    if (!deadline) return undefined;
    setNow(Date.now());
    const timer = window.setInterval(() => setNow(Date.now()), 1000);
    return () => window.clearInterval(timer);
  }, [deadline]);

  if (!deadline) return null;
  return Math.max(0, Math.ceil((new Date(deadline).getTime() - now) / 1000));
}

/** Formato mm:ss para el contador de la reserva. */
export function formatCountdown(seconds: number): string {
  const minutes = Math.floor(seconds / 60);
  const rest = seconds % 60;
  return `${minutes}:${String(rest).padStart(2, '0')}`;
}
