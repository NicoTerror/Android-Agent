# 🚀 Inicio Simple - ngrok + Web Dashboard

## ⚡ Iniciar Todo (3 Terminales)

### Terminal 1: Backend

```powershell
cd C:\Users\davso\Documents\Androidcontrol\backend-fastapi
.\iniciar-backend.bat
```

Espera a ver: `Backend iniciando en http://localhost:8000`

---

### Terminal 2: ngrok

```powershell
ngrok http 8000
```

Copia la URL que aparece (ej: `https://abc123.ngrok-free.app`)

---

### Terminal 3: Dashboard Web

```powershell
cd C:\Users\davso\Documents\Androidcontrol\web-dashboard
.\start.bat
```

Abre `http://localhost:5173` en tu navegador

---

## 📱 Configurar el APK

1. **Copia la URL de ngrok** (de Terminal 2)

2. **Edita** `android-agent\app\build.gradle` línea 21:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://TU_URL_NGROK/ws\""
   ```
   
   Ejemplo: `"wss://abc123.ngrok-free.app/ws"`

3. **Genera el APK** desde Android Studio

---

## ✅ Verificación

- Backend: `http://localhost:8000/health` → `{"status":"ok"}`
- ngrok: URL visible en terminal
- Dashboard: `http://localhost:5173` se abre

---

¡Eso es todo! 🎉


