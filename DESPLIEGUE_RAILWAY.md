# 🚀 Guía Completa: Desplegar en Railway

Esta guía te llevará paso a paso para desplegar el backend en Railway y tener una **URL fija permanente** para tu aplicación.

## 📋 Requisitos Previos

- ✅ Cuenta en GitHub (ya tienes el código subido)
- ✅ Cuenta en Railway (gratis, se crea con GitHub)
- ✅ Código del proyecto en GitHub

## 🎯 Objetivo

Desplegar el backend FastAPI en Railway para obtener:
- ✅ URL fija permanente (ej: `https://tu-app.railway.app`)
- ✅ HTTPS automático
- ✅ Backend siempre disponible
- ✅ Web dashboard funcionando
- ✅ App Android lista para generar APK

---

## Paso 1: Crear Cuenta en Railway

1. Ve a: **https://railway.app**
2. Click en **"Start a New Project"** o **"Login"**
3. Selecciona **"Login with GitHub"**
4. Autoriza Railway para acceder a tus repositorios

---

## Paso 2: Crear Nuevo Proyecto

1. En Railway, click en **"New Project"**
2. Selecciona **"Deploy from GitHub repo"**
3. Busca y selecciona tu repositorio: **`NicoTerror/Android-Agent`**
4. Railway detectará automáticamente que es un proyecto Python

---

## Paso 3: Configurar el Servicio

Railway debería detectar automáticamente tu proyecto. Si no:

1. Click en el servicio creado
2. Ve a la pestaña **"Settings"**
3. Configura:
   - **Root Directory**: `backend-fastapi`
   - **Start Command**: (dejar vacío, usa Procfile)
   - **Python Version**: 3.11 (o la que prefieras)

---

## Paso 4: Obtener tu URL de Railway

1. En Railway, ve a tu proyecto
2. Click en el servicio desplegado
3. Ve a la pestaña **"Settings"**
4. Scroll hasta **"Domains"**
5. Verás tu URL: `https://tu-app.railway.app` (o similar)
6. **⚠️ COPIA ESTA URL** - la necesitarás después

**Ejemplo de URL**: `https://androidcontrol-production.up.railway.app`

---

## Paso 5: Verificar que el Backend Funciona

1. Abre tu navegador
2. Ve a: `https://TU-URL-RAILWAY.railway.app/health`
3. Deberías ver: `{"status":"ok"}`

Si funciona, ¡el backend está desplegado correctamente! 🎉

---

## Paso 6: Configurar la App Android

### 6.1 Actualizar build.gradle

1. Abre: `android-agent/app/build.gradle`
2. Busca las líneas:
   ```gradle
   buildConfigField "String", "BASE_URL", "\"https://TU-URL-RAILWAY.railway.app\""
   buildConfigField "String", "WS_URL", "\"wss://TU-URL-RAILWAY.railway.app/ws\""
   ```
3. Reemplaza `TU-URL-RAILWAY` con tu URL real de Railway
   ```gradle
   buildConfigField "String", "BASE_URL", "\"https://androidcontrol-production.up.railway.app\""
   buildConfigField "String", "WS_URL", "\"wss://androidcontrol-production.up.railway.app/ws\""
   ```

### 6.2 Regenerar el APK

1. Abre Android Studio
2. Abre el proyecto: `android-agent`
3. Build → Generate Signed Bundle / APK
4. Selecciona APK
5. Sigue el proceso de firma
6. El APK se generará en: `android-agent/app/release/app-release.apk`

---

## Paso 7: Configurar el Web Dashboard

### 7.1 Crear archivo .env

1. En `web-dashboard/`, crea un archivo `.env`
2. Agrega:
   ```
   VITE_BASE_URL=https://TU-URL-RAILWAY.railway.app
   ```
   (Reemplaza `TU-URL-RAILWAY` con tu URL real)

### 7.2 Iniciar el Dashboard

```powershell
cd web-dashboard
npm install
npm run dev
```

El dashboard se abrirá en `http://localhost:5173` y se conectará automáticamente a Railway.

---

## Paso 8: Probar Todo el Sistema

### 8.1 Probar Backend
- ✅ Abre: `https://TU-URL-RAILWAY.railway.app/health`
- ✅ Debe responder: `{"status":"ok"}`

### 8.2 Probar Endpoint de Descubrimiento
- ✅ Abre: `https://TU-URL-RAILWAY.railway.app/server-url`
- ✅ Debe responder con `ws_url` y `http_url`

### 8.3 Probar Web Dashboard
- ✅ Inicia el dashboard: `npm run dev` en `web-dashboard`
- ✅ Debe conectarse automáticamente a Railway
- ✅ Debe mostrar "Conectado" en verde

### 8.4 Probar App Android
- ✅ Instala el APK en una tablet/emulador
- ✅ La app debe conectarse automáticamente a Railway
- ✅ Debe aparecer en el web dashboard

---

## 🔧 Solución de Problemas

### Error: "Module not found" en Railway

**Solución**: Verifica que `requirements.txt` esté en `backend-fastapi/` y tenga todas las dependencias.

### Error: "Port already in use"

**Solución**: Railway maneja el puerto automáticamente. Asegúrate de usar `$PORT` en el Procfile.

### Error: "502 Bad Gateway"

**Solución**: 
1. Verifica los logs en Railway (pestaña "Deployments")
2. Asegúrate de que el Procfile esté correcto
3. Verifica que `main.py` esté en `backend-fastapi/app/`

### El WebSocket no conecta

**Solución**:
1. Verifica que la URL en `build.gradle` sea correcta
2. Asegúrate de usar `wss://` (no `ws://`) para Railway
3. Verifica que Railway esté desplegado y funcionando

### El dashboard no encuentra el servidor

**Solución**:
1. Verifica que `.env` en `web-dashboard/` tenga `VITE_BASE_URL` correcto
2. Reinicia el servidor de desarrollo: `npm run dev`
3. Verifica que Railway esté funcionando: `https://TU-URL/server-url`

---

## 📊 Monitoreo en Railway

### Ver Logs
1. En Railway, ve a tu servicio
2. Click en la pestaña **"Deployments"**
3. Click en el deployment más reciente
4. Verás los logs en tiempo real

### Ver Métricas
1. En Railway, ve a tu servicio
2. Click en la pestaña **"Metrics"**
3. Verás CPU, memoria, y tráfico

---

## 💰 Costos

### Plan Gratuito
- ✅ $5 de crédito gratis al mes
- ✅ Suficiente para desarrollo y pruebas
- ✅ Se renueva cada mes

### Plan Hobby ($20/mes)
- ✅ Para producción pequeña
- ✅ Más recursos
- ✅ Sin límites de tiempo

**Para monitorear tablets, el plan gratuito suele ser suficiente.**

---

## 🔄 Actualizar el Código

Cada vez que hagas cambios:

1. **Haz commit y push a GitHub**:
   ```powershell
   git add .
   git commit -m "Descripción de cambios"
   git push origin main
   ```

2. **Railway detectará automáticamente** y desplegará la nueva versión

3. **Espera 2-3 minutos** para que el despliegue termine

4. **Verifica** que todo funcione

---

## 🎉 ¡Listo!

Ahora tienes:
- ✅ Backend desplegado en Railway con URL fija
- ✅ Web dashboard funcionando
- ✅ App Android lista para generar APK
- ✅ Todo conectado y funcionando

**Tu URL de Railway es permanente** - no cambiará a menos que elimines el servicio.

---

## 📝 Resumen Rápido

1. ✅ Crear cuenta en Railway
2. ✅ Conectar repositorio de GitHub
3. ✅ Desplegar automáticamente
4. ✅ Obtener URL de Railway
5. ✅ Actualizar `build.gradle` con la URL
6. ✅ Regenerar APK
7. ✅ Configurar `.env` en web-dashboard
8. ✅ Probar todo el sistema

**¡Tu sistema está listo para monitorear tablets en producción!** 🚀

