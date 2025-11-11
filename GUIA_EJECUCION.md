# Guía de Ejecución - Android Control

Guía paso a paso para ejecutar todo el sistema localmente.

## 📋 Prerequisitos

1. **Python 3.12** instalado
2. **Node.js 18+** y npm instalados
3. **Android Studio** instalado (para compilar la app Android)
4. **Emulador Android** o dispositivo físico conectado

## 🚀 Paso 1: Iniciar el Backend (FastAPI)

### En Windows (PowerShell):

```powershell
cd backend-fastapi
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
cd app
python main.py
```

### En Linux/Mac:

```bash
cd backend-fastapi
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
cd app
python main.py
```

**✅ Verificación**: Abre `http://localhost:8000/health` en el navegador. Deberías ver `{"status":"ok"}`

**✅ Verificación WebSocket**: Abre `http://localhost:8000/docs` para ver la documentación de la API.

---

## 🌐 Paso 2: Iniciar el Web Dashboard

Abre una **nueva terminal** (deja el backend corriendo):

### En Windows (PowerShell):

```powershell
cd web-dashboard
npm install
npm run dev
```

### En Linux/Mac:

```bash
cd web-dashboard
npm install
npm run dev
```

**✅ Verificación**: El dashboard estará en `http://localhost:5173` (o el puerto que Vite muestre)

Deberías ver la tabla vacía con el mensaje "No hay dispositivos conectados" y el indicador de conexión en rojo (desconectado) hasta que conectes un dispositivo Android.

---

## 📱 Paso 3: Configurar y Ejecutar Android Agent

### Opción A: Emulador Android

1. **Abrir Android Studio**
2. **Abrir el proyecto**: `File → Open → Seleccionar carpeta android-agent`
3. **Crear/Iniciar Emulador**:
   - `Tools → Device Manager`
   - Crear un dispositivo virtual (API 24 o superior)
   - Iniciar el emulador

4. **Configurar URL del WebSocket**:
   
   Edita `android-agent/app/build.gradle` y busca la sección `defaultConfig`:
   
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://10.0.2.2:8000/ws\""
   buildConfigField "String", "API_KEY", "\"devkey\""
   ```
   
   **Nota**: `10.0.2.2` es la IP especial del emulador que apunta a `localhost` de tu máquina.

5. **Compilar e instalar**:
   
   En Android Studio: `Build → Make Project` (o `Ctrl+F9`)
   
   Luego: `Run → Run 'app'` (o `Shift+F10`)
   
   O desde terminal:
   ```bash
   cd android-agent
   .\gradlew assembleDebug
   adb install app\build\outputs\apk\debug\app-debug.apk
   ```

### Opción B: Dispositivo Físico

1. **Habilitar opciones de desarrollador** en tu Android:
   - `Ajustes → Acerca del teléfono → Toca 7 veces "Número de compilación"`

2. **Habilitar depuración USB**:
   - `Ajustes → Opciones de desarrollador → Depuración USB`

3. **Conectar dispositivo** por USB

4. **Obtener tu IP local** (donde corre el backend):
   
   En Windows:
   ```powershell
   ipconfig
   ```
   Busca "IPv4" (ej: `192.168.1.100`)
   
   En Linux/Mac:
   ```bash
   ifconfig
   # o
   ip addr
   ```

5. **Configurar URL del WebSocket**:
   
   Edita `android-agent/app/build.gradle`:
   
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://TU_IP:8000/ws\""
   buildConfigField "String", "API_KEY", "\"devkey\""
   ```
   
   Ejemplo: `"ws://192.168.1.100:8000/ws"`

6. **Compilar e instalar** (igual que Opción A)

---

## 🔐 Paso 4: Conceder Permisos en Android

Una vez instalada la app:

1. **Permisos básicos**: Se solicitarán automáticamente al abrir la app

2. **Permiso de Uso de Aplicaciones** (crítico para `foregroundApp`):
   - `Ajustes → Apps → Android Agent → Uso de acceso`
   - Activar el toggle
   - Seleccionar "Android Agent" y activar

   **Nota**: Sin este permiso, `foregroundApp` mostrará "permission_required"

3. **Notificaciones**: Se solicitarán automáticamente (Android 13+)

---

## ✅ Verificación Final

1. **Backend corriendo**: `http://localhost:8000/health` → `{"status":"ok"}`

2. **Dashboard abierto**: `http://localhost:5173` → Tabla visible

3. **App Android instalada y abierta**:
   - Deberías ver una notificación persistente "Android Agent - Monitoreo activo"
   - El servicio está corriendo en background

4. **Ver dispositivos conectados**:
   - Abre `http://localhost:8000/devices` en el navegador
   - Deberías ver un JSON con el dispositivo conectado

5. **Dashboard actualizándose**:
   - En el dashboard web, deberías ver:
     - Indicador verde (conectado)
     - Tabla con tu dispositivo
     - Métricas actualizándose cada 3 segundos

---

## 🐛 Solución de Problemas

### Backend no inicia
- Verifica que Python 3.12 esté instalado: `python --version`
- Verifica que el puerto 8000 esté libre
- Revisa los logs en la terminal

### Dashboard no se conecta
- Verifica que el backend esté corriendo
- Abre la consola del navegador (F12) para ver errores
- Verifica que la URL en `useWs.ts` sea `ws://localhost:8000/ws`

### Android no se conecta al backend

**Emulador:**
- Verifica que uses `ws://10.0.2.2:8000/ws`
- Verifica que el backend esté corriendo en tu máquina
- Revisa Logcat en Android Studio: `View → Tool Windows → Logcat`

**Dispositivo físico:**
- Verifica que el dispositivo y tu PC estén en la misma red WiFi
- Verifica que uses la IP correcta de tu PC
- Verifica que el firewall de Windows no bloquee el puerto 8000
- Prueba desde el navegador del dispositivo: `http://TU_IP:8000/health`

### No aparecen métricas
- Verifica que el permiso "Uso de acceso" esté concedido
- Revisa Logcat para ver errores
- Verifica que el servicio esté corriendo (notificación visible)

### WebSocket se desconecta
- Es normal que se reconecte automáticamente
- Revisa los logs del backend para ver errores
- Verifica la conexión de red

---

## 📊 Flujo de Datos

```
Android Agent (cada 3s)
    ↓ WebSocket (ws://...)
Backend FastAPI
    ↓ Broadcast
Web Dashboard (actualización en vivo)
```

---

## 🎯 Orden de Ejecución Recomendado

1. ✅ Backend (dejar corriendo)
2. ✅ Dashboard web (dejar corriendo)
3. ✅ Compilar e instalar Android Agent
4. ✅ Conceder permisos
5. ✅ Verificar que todo funcione

---

## 💡 Tips

- **Múltiples dispositivos**: Puedes instalar la app en varios dispositivos/emuladores. Todos aparecerán en el dashboard.
- **Logs útiles**:
  - Backend: Terminal donde corre `python main.py`
  - Android: Android Studio → Logcat
  - Dashboard: Consola del navegador (F12)
- **Reiniciar todo**: Si algo falla, reinicia en orden: Backend → Dashboard → App Android

