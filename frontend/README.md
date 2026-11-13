# Frontend de Stage X

React 19 + TypeScript + Vite + React Router. Requiere Node.js 20.19 o superior.

```bash
npm install
npm run dev        # http://localhost:5173
npm test           # pruebas con Vitest y Testing Library
npm run build      # verificación de tipos y build de producción
```

Mientras el backend no exponga el catálogo, el frontend usa datos de prueba
(`src/services/api/mock`). Para conectarlo al backend, copiar `.env.example` a `.env.local`
y definir `VITE_API_URL=http://localhost:8080` con `VITE_USE_MOCKS=false`.

Estructura y reglas de UX: [docs/FRONTEND.md](../docs/FRONTEND.md).
