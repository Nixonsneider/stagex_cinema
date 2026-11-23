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

### Cuentas demo (solo con datos de prueba)

Mientras el backend no exponga `/auth/*` (T-016), el inicio de sesión usa
`src/services/api/mock/mockAuth.ts`. Todas las cuentas usan la contraseña `StageX2026`:

| Correo | Rol | Entra a |
|---|---|---|
| `cliente@stagex.test` | `CUSTOMER` | Cartelera y Mis entradas |
| `taquilla@stagex.test` | `CASHIER` | Validar entradas y Ventas |
| `admin@stagex.test` | `CINEMA_ADMIN` | Películas, salas, funciones y ventas |
| `pendiente@stagex.test` | `CUSTOMER` sin verificar | No puede entrar: muestra el aviso de cuenta pendiente |

Estas cuentas no existen en el backend real.

Estructura y reglas de UX: [docs/FRONTEND.md](../docs/FRONTEND.md).
