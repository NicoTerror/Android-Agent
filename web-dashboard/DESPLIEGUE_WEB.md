# 🚀 Desplegar Web Dashboard en Línea

Tienes dos opciones para desplegar el web dashboard:

## Opción 1: Vercel (Recomendado - Más Fácil) ⭐

Vercel es perfecto para proyectos Vite/React y es **gratis**.

### Pasos:

1. **Crear cuenta en Vercel**
   - Ve a: https://vercel.com
   - Click en **"Sign Up"**
   - Inicia sesión con GitHub

2. **Desplegar el proyecto**
   - En Vercel, click en **"Add New Project"**
   - Selecciona tu repositorio: `NicoTerror/Android-Agent`
   - Configura:
     - **Framework Preset**: Vite
     - **Root Directory**: `web-dashboard`
     - **Build Command**: `npm run build`
     - **Output Directory**: `dist`
     - **Install Command**: `npm install`

3. **Configurar Variables de Entorno**
   - En la configuración del proyecto, ve a **"Environment Variables"**
   - Agrega:
     - **Name**: `VITE_BASE_URL`
     - **Value**: `https://android-agent-production.up.railway.app`
   - Click en **"Save"**

4. **Desplegar**
   - Click en **"Deploy"**
   - Espera 1-2 minutos
   - ¡Listo! Tendrás una URL como: `https://tu-dashboard.vercel.app`

### Ventajas de Vercel:
- ✅ Gratis
- ✅ Muy fácil de configurar
- ✅ Despliegue automático desde GitHub
- ✅ HTTPS automático
- ✅ URL personalizada
- ✅ Muy rápido

---

## Opción 2: Railway (Todo Junto)

Si prefieres tener todo en Railway:

### Pasos:

1. **En Railway, agregar nuevo servicio**
   - Ve a tu proyecto en Railway
   - Click en **"New"** → **"GitHub Repo"**
   - Selecciona el mismo repositorio

2. **Configurar el servicio**
   - **Root Directory**: `web-dashboard`
   - **Build Command**: `npm install && npm run build`
   - **Start Command**: `npx serve -s dist -l $PORT`

3. **Agregar variable de entorno**
   - En Settings → Variables
   - Agrega: `VITE_BASE_URL=https://android-agent-production.up.railway.app`

4. **Desplegar**
   - Railway detectará automáticamente y desplegará
   - Obtendrás una URL como: `https://tu-dashboard.railway.app`

### Nota para Railway:
Necesitas instalar `serve` como dependencia:

```json
// package.json
{
  "scripts": {
    "start": "serve -s dist -l $PORT"
  },
  "dependencies": {
    "serve": "^14.2.0"
  }
}
```

---

## Recomendación: Vercel

**Vercel es más fácil y está optimizado para Vite/React**. Te recomiendo usar Vercel para el dashboard y Railway para el backend.

---

## Configuración Post-Despliegue

Una vez desplegado, el dashboard:
- ✅ Se conectará automáticamente a Railway
- ✅ Mostrará los dispositivos conectados
- ✅ Estará disponible 24/7 sin necesidad de tu computadora

## Actualizar la URL

Si cambias la URL de Railway, solo necesitas:
1. Actualizar la variable de entorno `VITE_BASE_URL` en Vercel/Railway
2. Hacer un nuevo despliegue (automático si está conectado a GitHub)

---

## Prueba Rápida

Después del despliegue:
1. Abre la URL de tu dashboard (Vercel o Railway)
2. Debe mostrar "Conectado" en verde
3. Debe conectarse automáticamente a Railway
4. Los dispositivos Android aparecerán automáticamente

¡Listo! 🎉

