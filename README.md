# Android Control - Monorepo

Monorepo para monitoreo de dispositivos Android sin control remoto. Consta de tres componentes:

- **android-agent/**: Aplicación Android (Kotlin) que recolecta métricas
- **backend-fastapi/**: Backend Python con FastAPI y WebSocket
- **web-dashboard/**: Dashboard web en React + Vite

## Requisitos

- **Android**: Android Studio, JDK 8+, minSdk 24, targetSdk 34
- **Backend**: Python 3.12
- **Web**: Node.js 18+ y npm/yarn

## 🚀 Inicio Rápido

**📖 Para una guía detallada paso a paso, consulta [GUIA_EJECUCION.md](GUIA_EJECUCION.md)**

### Ejecución Rápida (Windows)

1. **Backend**: Doble clic en `backend-fastapi/start.bat` o ejecuta:
   ```powershell
   cd backend-fastapi
   .\start.bat
   ```

2. **Dashboard**: Doble clic en `web-dashboard/start.bat` o ejecuta:
   ```powershell
   cd web-dashboard
   .\start.bat
   ```

3. **Android**: Abre el proyecto en Android Studio y ejecuta

## Configuración y Ejecución Detallada

### 1. Backend FastAPI

```bash
cd backend-fastapi
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate
pip install -r requirements.txt
cd app
python main.py
```

El backend estará disponible en `http://localhost:8000`

Endpoints:
- `GET /health` - Health check
- `GET /devices` - Estado de dispositivos
- `WS /ws?mode={agent|viewer}&api_key=devkey&device_id=<id>` - WebSocket

### 2. Web Dashboard

```bash
cd web-dashboard
npm install
npm run dev
```

El dashboard estará disponible en `http://localhost:5173` (o el puerto que Vite asigne)

### 3. Android Agent

1. Abrir el proyecto en Android Studio
2. Configurar las variables en `app/build.gradle` o en `gradle.properties`:
   ```gradle
   WS_URL=ws://TU_IP_LOCAL:8000/ws
   API_KEY=devkey
   ```
   Para emulador: `ws://10.0.2.2:8000/ws`
   Para dispositivo físico: `ws://TU_IP_LOCAL:8000/ws` (ej: `ws://192.168.1.100:8000/ws`)

3. Compilar e instalar en el dispositivo/emulador:
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Permisos importantes**:
   - La app solicitará permisos básicos automáticamente
   - **Usage Access**: Debe concederse manualmente en Ajustes → Apps → Android Agent → Uso de acceso

5. Al abrir la app, iniciará automáticamente el servicio de monitoreo

## Funcionalidades

### Android Agent
- Recolecta métricas cada 3 segundos:
  - `screenOn`: Estado de la pantalla (PowerManager.isInteractive)
  - `volume`: Nivel de volumen (AudioManager STREAM_MUSIC)
  - `foregroundApp`: App en primer plano (UsageStatsManager)
- Servicio en foreground con notificación persistente
- Conexión WebSocket con reconexión automática (backoff exponencial)
- Reinicio automático tras BOOT_COMPLETED

### Backend
- Mantiene estado de dispositivos en memoria
- WebSocket con dos modos:
  - `agent`: Recibe métricas de dispositivos Android
  - `viewer`: Envía actualizaciones a clientes web
- Broadcast automático de actualizaciones a todos los viewers

### Web Dashboard
- Tabla en tiempo real con métricas de dispositivos
- Indicador de estado de conexión
- Reconexión automática si se pierde la conexión

## Estructura del Proyecto

```
.
├── android-agent/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   └── java/com/androidagent/
│   │   │       ├── MainActivity.kt
│   │   │       ├── service/MetricsService.kt
│   │   │       ├── core/
│   │   │       │   ├── MetricsCollector.kt
│   │   │       │   ├── MetricsWsClient.kt
│   │   │       │   └── Notifications.kt
│   │   │       └── boot/BootReceiver.kt
│   │   └── build.gradle
│   ├── build.gradle
│   └── settings.gradle
├── backend-fastapi/
│   ├── app/
│   │   └── main.py
│   └── requirements.txt
├── web-dashboard/
│   ├── src/
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── useWs.ts
│   ├── index.html
│   └── package.json
└── README.md
```

## Notas

- El backend mantiene estado en memoria (se pierde al reiniciar)
- Para producción, considerar persistencia en base de datos
- El `api_key` actual es `devkey` (cambiar en producción)
- El permiso `PACKAGE_USAGE_STATS` requiere configuración manual en Android

