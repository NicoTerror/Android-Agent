# 🚀 Android Control - Inicio Rápido

## ⚡ Iniciar Todo (3 Terminales)

### Terminal 1: Backend
```powershell
cd backend-fastapi
.\iniciar-backend.bat
```

### Terminal 2: ngrok
```powershell
ngrok http 8000
```
Copia la URL que aparece (ej: `https://abc123.ngrok-free.app`)

### Terminal 3: Dashboard Web
```powershell
cd web-dashboard
.\start.bat
```
Abre `http://localhost:5173` en tu navegador

---

## 📱 Configurar APK

1. Edita `android-agent\app\build.gradle` línea 21:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://TU_URL_NGROK/ws\""
   ```

2. Genera el APK desde Android Studio

---

¡Listo! 🎉


