# ⚡ Desplegar Web Dashboard - Guía Rápida

## 🎯 Opción Recomendada: Vercel (5 minutos)

### Paso 1: Crear cuenta
1. Ve a: **https://vercel.com**
2. Login con GitHub

### Paso 2: Desplegar
1. Click **"Add New Project"**
2. Selecciona: `NicoTerror/Android-Agent`
3. Configura:
   - **Framework**: Vite
   - **Root Directory**: `web-dashboard`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`

### Paso 3: Variables de Entorno
En **Environment Variables**, agrega:
- **Name**: `VITE_BASE_URL`
- **Value**: `https://android-agent-production.up.railway.app`

### Paso 4: Deploy
Click **"Deploy"** y espera 1-2 minutos.

✅ **¡Listo!** Tendrás una URL como: `https://tu-dashboard.vercel.app`

---

## 🔄 Actualizar después de cambios

Cada vez que hagas push a GitHub, Vercel desplegará automáticamente.

---

## 📝 Notas

- El dashboard se conectará automáticamente a Railway
- No necesitas mantener tu computadora encendida
- Estará disponible 24/7

**Ver guía completa**: `web-dashboard/DESPLIEGUE_WEB.md`

