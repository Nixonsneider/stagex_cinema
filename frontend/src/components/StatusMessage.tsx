import type { ReactNode } from 'react';

interface StatusMessageProps {
  kind: 'loading' | 'error' | 'empty';
  children: ReactNode;
  onRetry?: () => void;
}

/** Mensaje de estado accesible: carga, error o lista vacía. */
export function StatusMessage({ kind, children, onRetry }: StatusMessageProps) {
  return (
    <div className={`status status-${kind}`} role={kind === 'error' ? 'alert' : 'status'}>
      <p>{children}</p>
      {onRetry && (
        <button type="button" className="button" onClick={onRetry}>
          Intentar de nuevo
        </button>
      )}
    </div>
  );
}
