import { Link } from 'react-router';
import { useDocumentTitle } from '../hooks/useDocumentTitle';

export function NotFoundPage() {
  useDocumentTitle('Página no encontrada');
  return (
    <section>
      <h1>Página no encontrada</h1>
      <p>La dirección que abriste no existe.</p>
      <p>
        <Link to="/">Ir a la cartelera</Link>
      </p>
    </section>
  );
}
