# 🚀 Iniciar Todo el Sistema - Guía Simple y Limpia

## ⚡ Inicio Rápido (3 Comandos)

Abre **3 terminales PowerShell** y ejecuta en este orden:

---

### 1️⃣ Backend (Terminal 1) - PRIMERO

```powershell
cd C:\Users\davso\Documents\Androidcontrol\backend-fastapi
.\iniciar-backend.bat
```

**Espera** hasta ver: `Backend iniciando en http://localhost:8000`

**Verifica:** Abre `http://localhost:8000/health` en el navegador → Debe mostrar `{"status":"ok"}`

**⚠️ IMPORTANTE:** Deja esta terminal abierta. El backend debe seguir corriendo.

---

### 2️⃣ ngrok (Terminal 2) - DESPUÉS DEL BACKEND

```powershell
ngrok http 8000
```

**Espera** hasta ver la URL (ej: `https://abc123.ngrok-free.app`)

**Copia esa URL** - La necesitarás para el APK

**⚠️ IMPORTANTE:** Deja esta terminal abierta. ngrok debe seguir corriendo.

---

### 3️⃣ Dashboard Web (Terminal 3) - OPCIONAL

```powershell
cd C:\Users\davso\Documents\Androidcontrol\web-dashboard
.\start.bat
```

**Abre** `http://localhost:5173` en tu navegador para ver los dispositivos

---

## ✅ Verificación Rápida

- [ ] **Backend**: `http://localhost:8000/health` → `{"status":"ok"}`
- [ ] **ngrok**: URL visible en terminal (ej: `https://abc123.ngrok-free.app`)
- [ ] **Dashboard**: `http://localhost:5173` se abre

---

## 🔧 Si Algo Falla

### Backend no inicia

**Solución manual:**
```powershell
cd C:\Users\davso\Documents\Androidcontrol\backend-fastapi
.\venv\Scripts\activate.bat
pip install -r requirements.txt
cd app
python main.py
```

### ngrok no funciona

Verifica que ngrok esté instalado y configurado:
```powershell
ngrok --version
ngrok config add-authtoken TU_TOKEN
```

### Dashboard no inicia

```powershell
cd C:\Users\davso\Documents\Androidcontrol\web-dashboard
npm install
npm run dev
```

---

## 📱 Configurar el APK

1. **Copia la URL de ngrok** (de la Terminal 2)

2. **Edita** `android-agent\app\build.gradle` líneas 22-23:
   ```gradle
   buildConfigField "String", "BASE_URL", "\"https://TU_URL_NGROK\""
   buildConfigField "String", "WS_URL", "\"wss://TU_URL_NGROK/ws\""
   ```
   
   **Ejemplo:**
   ```gradle
   buildConfigField "String", "BASE_URL", "\"https://abc123.ngrok-free.app\""
   buildConfigField "String", "WS_URL", "\"wss://abc123.ngrok-free.app/ws\""
   ```
   
   **IMPORTANTE:**
   - `BASE_URL`: URL base HTTP (sin `/ws`)
   - `WS_URL`: URL WebSocket de fallback (con `wss://` y `/ws`)
   - Mantén las comillas y barras invertidas

3. **Guarda** el archivo (Ctrl+S)

4. **Genera el APK** desde Android Studio:
   - `Build → Build Bundle(s) / APK(s) → Build APK(s)`
   
   O desde terminal:
   ```powershell
   cd C:\Users\davso\Documents\Androidcontrol\android-agent
   .\gradlew.bat assembleDebug
   ```

### ✨ Descubrimiento Automático de URL

**¡Buenas noticias!** La app ahora actualiza automáticamente la URL cuando ngrok cambia. 

- La app consulta el endpoint `/server-url` cada 30 segundos
- Si detecta un cambio, se reconecta automáticamente
- **No necesitas regenerar el APK** si ngrok cambia la URL

**Nota**: Si la URL en `BASE_URL` deja de funcionar completamente, necesitarás actualizarla manualmente en `build.gradle` y regenerar el APK.

📖 Ver `DESCUBRIMIENTO_URL_AUTOMATICO.md` para más detalles.

---

## 🛑 Detener Todo

Presiona `Ctrl+C` en cada terminal o cierra las ventanas.

---

## 📝 Resumen de Comandos

```powershell
# Terminal 1: Backend
cd C:\Users\davso\Documents\Androidcontrol\backend-fastapi
.\iniciar-backend.bat

# Terminal 2: ngrok (después de que el backend esté corriendo)
ngrok http 8000

# Terminal 3: Dashboard (opcional)
cd C:\Users\davso\Documents\Androidcontrol\web-dashboard
.\start.bat
```

---

## 🎯 Orden Correcto

1. ✅ **Primero**: Inicia el Backend (Terminal 1)
2. ✅ **Segundo**: Inicia ngrok (Terminal 2) - Solo después de que el backend esté corriendo
3. ✅ **Tercero**: Inicia Dashboard (Terminal 3)

---

¡Eso es todo! Con estos 3 comandos deberías tener todo funcionando. 🎉
