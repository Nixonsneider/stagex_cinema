# Frontend de Stage X

React 19 + TypeScript + Vite + React Router. Requiere Node.js 20.19 o superior.

```bash
npm install
npm run dev        # http://localhost:5173
npm test           # pruebas con Vitest y Testing Library
npm run build      # verificación de tipos y build de producción
```

Sin configuración, el frontend usa datos de prueba (`src/services/api/mock`) para la cartelera,
el inicio de sesión y el mapa de asientos. Para conectarlo al backend, copiar `.env.example` a
`.env.local` y definir `VITE_API_URL=http://localhost:8080` con `VITE_USE_MOCKS=false`.

Las pruebas usan siempre los datos de prueba. Si existe `.env.local` apuntando al backend,
ejecutarlas forzando el mock:

```powershell
$env:VITE_USE_MOCKS="true"; npm.cmd test; Remove-Item Env:VITE_USE_MOCKS
```

### Cuentas demo

Las mismas cuentas existen en el mock y, con el perfil `local`, en el backend real (T-019).
Todas usan la contraseña `StageX2026`:

| Correo | Rol | Entra a |
|---|---|---|
| `cliente@stagex.test` | `CUSTOMER` | Cartelera y Mis entradas |
| `taquilla@stagex.test` | `CASHIER` | Validar entradas y Ventas |
| `admin@stagex.test` | `CINEMA_ADMIN` | Películas, salas, funciones y ventas |
| `pendiente@stagex.test` | `CUSTOMER` sin verificar | No puede entrar: muestra el aviso de cuenta pendiente |

Sin sesión también se puede reservar asientos: se pide el correo del invitado.

Estructura y reglas de UX: [docs/FRONTEND.md](../docs/FRONTEND.md).
