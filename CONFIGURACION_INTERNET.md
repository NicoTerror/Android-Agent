# 🌐 Configuración para Dispositivos en Diferentes Ubicaciones

Esta guía explica cómo configurar el sistema para que los dispositivos Android se conecten desde cualquier ubicación (no solo la misma red WiFi).

## 🎯 Opciones Disponibles

### Opción 1: ngrok (Recomendado para Pruebas) ⭐

**Ventajas:**
- ✅ Muy fácil de configurar
- ✅ No requiere configuración de router
- ✅ URL pública automática
- ✅ HTTPS incluido

**Desventajas:**
- ⚠️ URL cambia cada vez (a menos que uses cuenta paga)
- ⚠️ Límite de conexiones en versión gratuita

#### Pasos:

1. **Descargar ngrok**: https://ngrok.com/download
2. **Registrarte** (gratis) y obtener tu authtoken
3. **Configurar ngrok**:
   ```powershell
   ngrok config add-authtoken TU_TOKEN_AQUI
   ```
4. **Iniciar el backend** en tu PC:
   ```powershell
   cd backend-fastapi\app
   python main.py
   ```
5. **En otra terminal, iniciar ngrok**:
   ```powershell
   ngrok http 8000
   ```
6. **Copiar la URL** que ngrok te da (ej: `https://abc123.ngrok.io`)
7. **Actualizar el APK** con la URL de ngrok:
   - Edita `android-agent/app/build.gradle` línea 19:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://abc123.ngrok.io/ws\""
   ```
   **Nota**: Usa `wss://` (WebSocket seguro) en lugar de `ws://`
8. **Regenerar el APK** y distribuir

---

### Opción 2: IP Pública + Port Forwarding (Permanente)

**Ventajas:**
- ✅ URL permanente
- ✅ Sin límites de conexión
- ✅ Gratis

**Desventajas:**
- ⚠️ Requiere acceso al router
- ⚠️ IP pública puede cambiar (usar DDNS)
- ⚠️ Menos seguro (necesitas firewall)

#### Pasos:

1. **Obtener tu IP pública**:
   - Visita: https://whatismyipaddress.com
   - Anota tu IP pública (ej: `203.0.113.45`)

2. **Configurar Port Forwarding en tu router**:
   - Accede a la configuración del router (generalmente `192.168.1.1`)
   - Busca "Port Forwarding" o "Virtual Server"
   - Agrega una regla:
     - **Puerto externo**: `8000`
     - **Puerto interno**: `8000`
     - **IP interna**: Tu IP local (ej: `192.168.1.80`)
     - **Protocolo**: TCP

3. **Configurar firewall de Windows**:
   ```powershell
   # Ejecutar como Administrador
   New-NetFirewallRule -DisplayName "Android Control Backend" -Direction Inbound -LocalPort 8000 -Protocol TCP -Action Allow
   ```

4. **Actualizar el APK**:
   - Edita `android-agent/app/build.gradle` línea 19:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://TU_IP_PUBLICA:8000/ws\""
   ```
   Ejemplo: `"ws://203.0.113.45:8000/ws"`

5. **Opcional: Usar DDNS** (si tu IP cambia):
   - Servicios gratuitos: No-IP, DuckDNS
   - Configura un dominio dinámico (ej: `tuservidor.ddns.net`)
   - Usa ese dominio en lugar de la IP

---

### Opción 3: Servidor en la Nube (Producción)

**Ventajas:**
- ✅ Muy estable
- ✅ URL permanente
- ✅ Escalable
- ✅ Mejor seguridad

**Desventajas:**
- ⚠️ Requiere servidor (VPS, AWS, etc.)
- ⚠️ Costo mensual

#### Opciones de Servicios:

- **VPS**: DigitalOcean, Linode, Vultr (~$5/mes)
- **AWS EC2**: Amazon Web Services
- **Google Cloud**: Compute Engine
- **Heroku**: Platform as a Service

#### Pasos Generales:

1. **Crear servidor** en la nube
2. **Instalar Python 3.12** en el servidor
3. **Subir el código** del backend
4. **Configurar firewall** del servidor (puerto 8000)
5. **Iniciar el backend** en el servidor
6. **Actualizar APK** con la IP/dominio del servidor

---

## 🔧 Configuración del APK

### Cambiar la URL del Backend

1. **Edita** `android-agent/app/build.gradle`
2. **Busca** la línea 19:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://192.168.1.80:8000/ws\""
   ```
3. **Cambia** por tu URL pública:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://tu-dominio.com/ws\""
   ```
   o
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://TU_IP_PUBLICA:8000/ws\""
   ```
4. **Regenera el APK** desde Android Studio

### Notas Importantes:

- **WebSocket Seguro (WSS)**: Si usas HTTPS (ngrok, servidor con SSL), usa `wss://` en lugar de `ws://`
- **WebSocket Normal (WS)**: Si usas HTTP, usa `ws://`
- **Puerto**: Si cambias el puerto del backend, actualízalo también en el APK

---

## 🔒 Seguridad

### Recomendaciones:

1. **Cambiar el API Key**:
   - Edita `android-agent/app/build.gradle` línea 20
   - Cambia `"devkey"` por una clave más segura
   - Actualiza también en el backend si es necesario

2. **Usar HTTPS/WSS** cuando sea posible (ngrok, servidor con SSL)

3. **Firewall**: Configura reglas de firewall apropiadas

4. **Autenticación**: Considera agregar autenticación adicional si es crítico

---

## 📝 Ejemplo Completo con ngrok

### Paso a Paso:

1. **Instalar ngrok** y configurar authtoken
2. **Iniciar backend**:
   ```powershell
   cd backend-fastapi\app
   python main.py
   ```
3. **Iniciar ngrok** (nueva terminal):
   ```powershell
   ngrok http 8000
   ```
4. **Copiar URL** (ej: `Forwarding: https://abc123.ngrok-free.app -> http://localhost:8000`)
5. **Actualizar build.gradle**:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://abc123.ngrok-free.app/ws\""
   ```
6. **Regenerar APK** desde Android Studio
7. **Distribuir APK** a los dispositivos

**Nota**: Si la URL de ngrok cambia, necesitarás regenerar el APK con la nueva URL.

---

## 🐛 Solución de Problemas

### Los dispositivos no se conectan

1. **Verifica que el backend esté accesible**:
   - Prueba desde un navegador: `http://TU_URL/health`
   - Debe mostrar `{"status":"ok"}`

2. **Verifica el protocolo**:
   - HTTPS → usa `wss://`
   - HTTP → usa `ws://`

3. **Verifica el puerto**:
   - ngrok: no incluyas puerto (usa la URL que te da)
   - IP pública: incluye el puerto `:8000`

4. **Firewall**:
   - Asegúrate de que el puerto esté abierto
   - Verifica reglas de firewall

### La conexión se cae frecuentemente

- **ngrok**: Versión gratuita tiene límites, considera cuenta paga
- **IP pública**: Verifica estabilidad de conexión
- **Servidor en nube**: Más estable, considera esta opción

---

## 📋 Checklist

- [ ] Backend configurado y corriendo
- [ ] URL pública configurada (ngrok, IP pública, o servidor)
- [ ] APK actualizado con la URL correcta
- [ ] Protocolo correcto (ws:// o wss://)
- [ ] Firewall configurado
- [ ] API Key actualizado (opcional pero recomendado)
- [ ] APK regenerado y distribuido
- [ ] Dispositivos pueden conectarse desde cualquier ubicación








