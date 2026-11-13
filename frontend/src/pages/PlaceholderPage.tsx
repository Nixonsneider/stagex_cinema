import { Link } from 'react-router';
import { useDocumentTitle } from '../hooks/useDocumentTitle';

interface PlaceholderPageProps {
  title: string;
  task: string;
}

/** Página base para rutas cuya funcionalidad se implementa en una tarea posterior. */
export function PlaceholderPage({ title, task }: PlaceholderPageProps) {
  useDocumentTitle(title);
  return (
    <section>
      <h1>{title}</h1>
      <p>Esta sección estará disponible próximamente ({task}).</p>
      <p>
        <Link to="/">Ir a la cartelera</Link>
      </p>
    </section>
  );
}
