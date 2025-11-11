# 📊 Cómo Ver el Dashboard Web para Monitorear Dispositivos

## 🎯 Opción 1: Dashboard Local (Recomendado para Desarrollo)

Si el backend está corriendo en tu PC local:

### Paso 1: Iniciar el Dashboard

Abre una **nueva terminal PowerShell** y ejecuta:

```powershell
cd web-dashboard
.\start.bat
```

### Paso 2: Abrir en el Navegador

El dashboard se abrirá automáticamente en:
```
http://localhost:5173
```

Si no se abre automáticamente, ábrelo manualmente en tu navegador.

---

## 🌐 Opción 2: Dashboard con Cloudflare (Acceso desde Internet)

Si quieres acceder al dashboard desde cualquier lugar usando Cloudflare:

### Paso 1: Iniciar el Dashboard con Cloudflare

```powershell
cd web-dashboard
.\iniciar-con-cloudflare.bat
```

Cuando te pida la URL, ingresa tu URL de Cloudflare:
```
https://dale-assignment-dover-websites.trycloudflare.com
```

### Paso 2: Acceder al Dashboard

El dashboard estará disponible en:
- **Local**: `http://localhost:5173` (solo en tu PC)
- **Internet**: A través de Cloudflare (necesitas configurar Cloudflare para el puerto 5173)

**Nota**: Por defecto, Cloudflare solo expone el puerto 8000 (backend). Para exponer el dashboard también, necesitarías configurar Cloudflare para múltiples puertos o usar un proxy.

---

## 🔧 Configuración Manual

Si prefieres configurar manualmente:

### Para Localhost:

1. Edita `web-dashboard/src/App.tsx` línea 17:
   ```typescript
   const { devices, connected, error } = useWs('ws://localhost:8000/ws?mode=viewer&api_key=devkey')
   ```

2. Inicia el dashboard:
   ```powershell
   cd web-dashboard
   npm install
   npm run dev
   ```

### Para Cloudflare:

1. Edita `web-dashboard/src/App.tsx` línea 17:
   ```typescript
   const { devices, connected, error } = useWs('wss://dale-assignment-dover-websites.trycloudflare.com/ws?mode=viewer&api_key=devkey')
   ```

2. Inicia el dashboard:
   ```powershell
   cd web-dashboard
   npm install
   npm run dev
   ```

---

## 📋 Requisitos

Antes de iniciar el dashboard, asegúrate de que:

- ✅ **Backend esté corriendo** en `http://localhost:8000`
- ✅ **Cloudflare esté activo** (si usas la opción de internet)
- ✅ **Node.js esté instalado** (verifica con `node --version`)

---

## 🖥️ Qué Verás en el Dashboard

El dashboard muestra una tabla con:

- **Device ID**: Identificador del dispositivo
- **Pantalla**: ON/OFF (si la pantalla está encendida)
- **Volumen %**: Porcentaje de volumen actual
- **App en Foreground**: Qué aplicación está en uso
- **Última Actualización**: Cuándo se recibió la última métrica

Los datos se actualizan automáticamente cada 3 segundos.

---

## 🐛 Solución de Problemas

### El dashboard no se conecta

1. Verifica que el backend esté corriendo:
   - Abre `http://localhost:8000/health` en el navegador
   - Debe mostrar `{"status":"ok"}`

2. Verifica la URL del WebSocket:
   - Para localhost: `ws://localhost:8000/ws?mode=viewer&api_key=devkey`
   - Para Cloudflare: `wss://TU_URL.trycloudflare.com/ws?mode=viewer&api_key=devkey`

### No aparecen dispositivos

1. Verifica que la app Android esté instalada y corriendo
2. Verifica que el Device ID esté configurado
3. Verifica que el permiso "Uso de acceso" esté concedido
4. Revisa la consola del navegador (F12) para ver errores

### El puerto 5173 está ocupado

Vite automáticamente usará el siguiente puerto disponible (5174, 5175, etc.)
Revisa la terminal para ver qué puerto está usando.

---

## 📝 Resumen Rápido

```powershell
# Terminal 1: Backend
cd backend-fastapi
.\iniciar-backend.bat

# Terminal 2: Cloudflare (si usas internet)
cd backend-fastapi
.\iniciar-con-cloudflare.bat

# Terminal 3: Dashboard Web
cd web-dashboard
.\start.bat

# Abrir en navegador: http://localhost:5173
```

¡Eso es todo! 🎉



