const TIME_ZONE = 'America/Bogota';

export function formatDuration(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const rest = minutes % 60;
  if (hours === 0) return `${rest} min`;
  if (rest === 0) return `${hours} h`;
  return `${hours} h ${rest} min`;
}

export function formatPrice(amount: number, currency: string): string {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency, maximumFractionDigits: 0 }).format(amount);
}

/** Clave AAAA-MM-DD en hora de Colombia, para agrupar funciones por día. */
export function dayKey(iso: string): string {
  return new Intl.DateTimeFormat('en-CA', {
    timeZone: TIME_ZONE,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date(iso));
}

export function formatDayHeading(iso: string): string {
  const text = new Intl.DateTimeFormat('es-CO', {
    timeZone: TIME_ZONE,
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  }).format(new Date(iso));
  return text.charAt(0).toUpperCase() + text.slice(1);
}

export function formatTime(iso: string): string {
  return new Intl.DateTimeFormat('es-CO', {
    timeZone: TIME_ZONE,
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(iso));
}

export function ageRatingLabel(rating: string): string {
  return rating === 'TODOS' ? 'Todo público' : `Mayores de ${rating.replace('+', '')}`;
}
