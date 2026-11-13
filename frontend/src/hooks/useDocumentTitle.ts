import { useEffect } from 'react';

/** Actualiza el título de la pestaña; ayuda a lectores de pantalla al cambiar de página. */
export function useDocumentTitle(title: string): void {
  useEffect(() => {
    document.title = title ? `${title} | Stage X` : 'Stage X';
  }, [title]);
}
