# Descubrimiento Automático de URL

## ¿Qué es esto?

Esta funcionalidad permite que la aplicación Android y el web dashboard **actualicen automáticamente** la URL del servidor cuando ngrok cambia su URL, **sin necesidad de regenerar el APK** o modificar código.

## ¿Cómo funciona?

1. **Backend**: Consulta periódicamente la API local de ngrok (`http://localhost:4040/api/tunnels`) para obtener la URL pública actual.
2. **Endpoint `/server-url`**: Expone la URL actual del servidor WebSocket.
3. **App Android**: Consulta el endpoint cada 30 segundos y actualiza la conexión si la URL cambia.
4. **Web Dashboard**: Consulta el endpoint cada 30 segundos y actualiza la conexión automáticamente.

## Configuración

### 1. Backend

El backend ya está configurado para detectar automáticamente la URL de ngrok. Solo necesitas:

1. Iniciar el backend: `iniciar-backend.bat`
2. Iniciar ngrok: `ngrok http 8000`

El backend detectará automáticamente la URL de ngrok y la expondrá en el endpoint `/server-url`.

### 2. App Android

En `android-agent/app/build.gradle`, configura:

```gradle
buildConfigField "String", "BASE_URL", "\"https://TU_URL_NGROK_ACTUAL\""
buildConfigField "String", "WS_URL", "\"wss://TU_URL_NGROK_ACTUAL/ws\""
```

**Importante**: 
- `BASE_URL`: URL base HTTP para consultar el endpoint de descubrimiento (sin `/ws`)
- `WS_URL`: URL WebSocket de fallback si el descubrimiento falla
- Usa la **URL actual de ngrok** (puede ser la última que obtuviste)

**Ejemplo**:
```gradle
buildConfigField "String", "BASE_URL", "\"https://abc123.ngrok-free.app\""
buildConfigField "String", "WS_URL", "\"wss://abc123.ngrok-free.app/ws\""
```

### 3. Web Dashboard

En `web-dashboard/src/useServerUrl.ts`, la URL base se obtiene de:

1. Variable de entorno `VITE_BASE_URL` (si está configurada)
2. Fallback: `http://localhost:8000`

Para configurar la URL base en producción, crea un archivo `.env` en `web-dashboard/`:

```env
VITE_BASE_URL=https://TU_URL_NGROK_ACTUAL
```

O modifica directamente en `useServerUrl.ts`:

```typescript
const BASE_URL = 'https://TU_URL_NGROK_ACTUAL'
```

## Flujo de Funcionamiento

### Primera Conexión

1. **App Android**:
   - Al iniciar, consulta `BASE_URL/server-url`
   - Obtiene la URL WebSocket actual
   - Se conecta usando esa URL

2. **Web Dashboard**:
   - Al cargar, consulta `BASE_URL/server-url`
   - Obtiene la URL WebSocket actual
   - Se conecta usando esa URL

### Actualización Automática

1. **Backend**:
   - Cada 10 segundos, consulta la API de ngrok
   - Si detecta un cambio, actualiza `current_ngrok_url`
   - El endpoint `/server-url` retorna la nueva URL

2. **App Android**:
   - Cada 30 segundos, consulta `BASE_URL/server-url`
   - Si la URL cambió, cierra la conexión actual y reconecta con la nueva URL

3. **Web Dashboard**:
   - Cada 30 segundos, consulta `BASE_URL/server-url`
   - Si la URL cambió, `useWs` detecta el cambio y reconecta automáticamente

## Ventajas

✅ **No necesitas regenerar el APK** cuando ngrok cambia la URL  
✅ **Actualización automática** sin intervención del usuario  
✅ **Funciona con ngrok gratuito** (que cambia la URL frecuentemente)  
✅ **Fallback seguro**: Si el descubrimiento falla, usa la URL de `build.gradle`

## Limitaciones

⚠️ **Necesitas una URL base conocida**: La app necesita saber dónde consultar el endpoint `/server-url`. Esta URL debe estar en `build.gradle` y puede ser la última URL de ngrok que obtuviste.

⚠️ **Si ngrok cambia completamente**: Si la URL en `BASE_URL` ya no funciona, la app no podrá descubrir la nueva URL. En este caso, necesitarías actualizar `BASE_URL` en `build.gradle` y regenerar el APK.

## Solución al Problema del Huevo y la Gallina

**Problema**: ¿Cómo consultar el endpoint si no sabemos la URL?

**Solución**: 
- Usa la **última URL conocida de ngrok** en `BASE_URL`
- Si ngrok cambia, el backend detecta el cambio y actualiza el endpoint
- La app consulta usando la URL anterior, obtiene la nueva, y se actualiza
- Si la URL anterior ya no funciona, necesitarás actualizar `BASE_URL` manualmente

**Recomendación**: Para evitar este problema, considera usar:
- **ngrok con cuenta paga** ($8/mes): URL permanente que no cambia
- **Cloudflare Tunnel con dominio propio**: URL permanente
- **Servidor propio con dominio fijo**: La mejor opción para producción

## Troubleshooting

### La app no se conecta

1. Verifica que el backend esté corriendo
2. Verifica que ngrok esté corriendo
3. Verifica que `BASE_URL` en `build.gradle` sea correcta
4. Revisa los logs de la app: `adb logcat | grep MetricsWsClient`

### El web dashboard no se conecta

1. Verifica que `VITE_BASE_URL` esté configurada correctamente
2. Abre la consola del navegador (F12) y revisa los mensajes de `[URL Discovery]`
3. Verifica que el backend esté accesible desde el navegador

### El backend no detecta la URL de ngrok

1. Verifica que ngrok esté corriendo en el puerto 4040 (API local)
2. Abre `http://localhost:4040/api/tunnels` en el navegador para verificar
3. Revisa los logs del backend para ver si hay errores

## Próximos Pasos

Para una solución más robusta, considera:

1. **Servidor de descubrimiento dedicado**: Un servidor pequeño con URL fija que siempre retorne la URL actual
2. **ngrok con cuenta paga**: URL permanente que no cambia
3. **Dominio propio con DDNS**: Configurar un dominio que apunte a tu IP dinámica


