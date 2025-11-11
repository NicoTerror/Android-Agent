# 🔧 Solución para URL Fija - Configuración en build.gradle

Este documento explica cómo configurar la URL del servidor en el APK antes de compilarlo.

## ⚙️ Configuración de la URL

La URL del servidor se configura en `android-agent/app/build.gradle` antes de compilar el APK. Esto asegura que los usuarios no puedan cambiar la configuración.

### Cómo Configurar:

1. **Edita `android-agent/app/build.gradle`** (línea 21):
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://TU_URL_AQUI/ws\""
   ```

2. **Regenera el APK** desde Android Studio:
   - `Build → Build Bundle(s) / APK(s) → Build APK(s)`

3. **Distribuye el APK** a los dispositivos

### Ventajas:

- ✅ **Seguro** - Los usuarios no pueden cambiar la configuración
- ✅ **Simple** - Una sola configuración antes de compilar
- ✅ **Funciona con cualquier túnel** - ngrok, Cloudflare, etc.

---

## 🌐 Opción 1: Cloudflare Tunnel (URL Fija y Gratis)

Cloudflare Tunnel ofrece una URL que **NO cambia** mientras el túnel esté activo. Es completamente gratis.

### Instalación:

1. **Descargar cloudflared:**
   - Opción A: Desde GitHub: https://github.com/cloudflare/cloudflared/releases
   - Opción B: Con winget: `winget install --id Cloudflare.cloudflared`

2. **Iniciar el túnel:**
   ```powershell
   cd backend-fastapi
   .\iniciar-con-cloudflare.bat
   ```

3. **Copiar la URL** que aparece (ej: `https://abc123.trycloudflare.com`)

4. **Configurar en build.gradle:**
   - Edita `android-agent/app/build.gradle` línea 21:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://abc123.trycloudflare.com/ws\""
   ```
   - Regenera el APK

### Ventajas:

- ✅ **URL fija** - No cambia mientras el túnel esté activo
- ✅ **Completamente gratis**
- ✅ **HTTPS incluido** (usa `wss://`)
- ✅ **Sin límites** de conexión

### Desventajas:

- ⚠️ La URL cambia si reinicias el túnel (necesitas regenerar el APK)
- ⚠️ Debes mantener el túnel corriendo

---

## 🔄 Opción 2: ngrok con Cuenta Paga (URL Permanente)

Si usas ngrok con una cuenta paga ($8/mes), puedes tener una URL **permanente** que nunca cambia.

### Configuración:

1. **Crear cuenta paga en ngrok:** https://dashboard.ngrok.com/billing

2. **Configurar dominio fijo:**
   ```powershell
   ngrok config add-authtoken TU_TOKEN
   ngrok http 8000 --domain=tu-dominio-fijo.ngrok.io
   ```

3. **Configurar en build.gradle:**
   - Edita `android-agent/app/build.gradle` línea 21:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://tu-dominio-fijo.ngrok.io/ws\""
   ```
   - Regenera el APK

### Ventajas:

- ✅ **URL permanente** - Nunca cambia
- ✅ **Muy confiable**

### Desventajas:

- ⚠️ **Cuesta $8/mes**
- ⚠️ Requiere cuenta paga

---

## 🏠 Opción 3: Port Forwarding + DDNS (Solución Permanente)

Si tienes acceso a tu router, puedes configurar port forwarding y usar un servicio DDNS para tener una URL permanente.

### Configuración:

1. **Configurar Port Forwarding en tu router:**
   - Puerto externo: 8000
   - IP interna: Tu IP local
   - Puerto interno: 8000

2. **Configurar DDNS** (ej: No-IP, DuckDNS):
   - Crea una cuenta gratuita
   - Configura un dominio (ej: `tu-servidor.ddns.net`)
   - Configura el cliente DDNS en tu router o PC

3. **Configurar en build.gradle:**
   - Edita `android-agent/app/build.gradle` línea 21:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://tu-servidor.ddns.net:8000/ws\""
   ```
   - Regenera el APK

### Ventajas:

- ✅ **URL permanente**
- ✅ **Completamente gratis**
- ✅ **Sin límites**

### Desventajas:

- ⚠️ Requiere acceso al router
- ⚠️ Más complejo de configurar
- ⚠️ Necesitas abrir puertos (consideraciones de seguridad)

---

## 📱 Cómo Configurar la URL en build.gradle

### Paso a Paso:

1. **Abre `android-agent/app/build.gradle`**

2. **Busca la línea 21** que dice:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://stately-pennied-guy.ngrok-free.dev/ws\""
   ```

3. **Reemplaza la URL** con tu URL del servidor:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://TU_URL_AQUI/ws\""
   ```

4. **Regenera el APK** desde Android Studio:
   - `Build → Build Bundle(s) / APK(s) → Build APK(s)`

### Formato de URL:

- **WebSocket seguro (WSS):** `wss://dominio.com/ws`
- **WebSocket normal (WS):** `ws://ip-o-dominio:8000/ws`

Ejemplos:
- `wss://abc123.ngrok-free.app/ws`
- `wss://abc123.trycloudflare.com/ws`
- `ws://192.168.1.100:8000/ws` (red local)
- `ws://tu-servidor.ddns.net:8000/ws`

---

## 🔄 Flujo de Trabajo Recomendado

### Con ngrok-free (URL cambia):

1. Iniciar backend: `python main.py`
2. Iniciar ngrok: `ngrok http 8000`
3. Copiar la nueva URL de ngrok
4. **Editar `build.gradle` línea 21** con la nueva URL
5. **Regenerar el APK** y distribuir

### Con Cloudflare Tunnel (URL fija):

1. Iniciar backend y túnel: `.\iniciar-con-cloudflare.bat`
2. Copiar la URL de Cloudflare
3. **Editar `build.gradle` línea 21** con la URL de Cloudflare
4. **Regenerar el APK** y distribuir
5. La URL se mantendrá mientras el túnel esté activo

---

## 🐛 Solución de Problemas

### La app no se conecta

1. Verifica que la URL en `build.gradle` tenga el formato correcto:
   - Debe comenzar con `ws://` o `wss://`
   - Debe terminar con `/ws`
   - Ejemplo correcto: `wss://abc123.ngrok-free.app/ws`

2. Verifica que el backend esté accesible:
   - Abre en el navegador: `https://tu-url-sin-ws` (ej: `https://abc123.ngrok-free.app`)
   - Debe mostrar algo o al menos no dar error 404

3. Verifica que regeneraste el APK después de cambiar la URL:
   - Los cambios en `build.gradle` solo se aplican al recompilar

---

## 📝 Notas Importantes

- La URL se configura en `build.gradle` antes de compilar el APK
- Los usuarios **NO pueden cambiar** la configuración desde la app
- Si cambia la URL del túnel, debes editar `build.gradle` y regenerar el APK
- Con Cloudflare Tunnel, la URL se mantiene fija mientras el túnel esté activo

