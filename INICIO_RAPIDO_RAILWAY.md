# ⚡ Inicio Rápido - Railway

## 🎯 Objetivo
Tener el backend en Railway con URL fija para monitorear tablets.

## 📝 Pasos Rápidos

### 1. Desplegar en Railway (5 minutos)

1. Ve a: **https://railway.app**
2. Login con GitHub
3. **New Project** → **Deploy from GitHub repo**
4. Selecciona: `NicoTerror/Android-Agent`
5. Espera 2-3 minutos
6. Ve a **Settings** → **Domains**
7. **Copia tu URL**: `https://tu-app.railway.app`

### 2. Actualizar Android (2 minutos)

1. Abre: `android-agent/app/build.gradle`
2. Reemplaza `TU-URL-RAILWAY` con tu URL real:
   ```gradle
   buildConfigField "String", "BASE_URL", "\"https://tu-app.railway.app\""
   buildConfigField "String", "WS_URL", "\"wss://tu-app.railway.app/ws\""
   ```
3. Regenera el APK en Android Studio

### 3. Configurar Dashboard (1 minuto)

1. En `web-dashboard/`, crea `.env`:
   ```
   VITE_BASE_URL=https://tu-app.railway.app
   ```
2. Inicia: `npm run dev`

### 4. Probar

- ✅ Backend: `https://tu-app.railway.app/health`
- ✅ Dashboard: `http://localhost:5173`
- ✅ App: Instala el APK en una tablet

## ✅ ¡Listo!

Tu sistema está funcionando con URL fija permanente.

**Ver guía completa**: `DESPLIEGUE_RAILWAY.md`

