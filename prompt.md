Objetivo: Monorepo con 3 piezas para monitoreo Android sin control remoto:
- android-agent/ (Kotlin, minSdk 24, targetSdk 34)
- backend-fastapi/ (Python 3.12, FastAPI + WebSocket)
- web-dashboard/ (React + Vite)

Requisitos funcionales
- Android:
  - Métricas cada 3 s: screenOn (PowerManager.isInteractive), volume (AudioManager STREAM_MUSIC), foregroundApp (UsageStatsManager, requiere Usage Access en ajustes).
  - ForegroundService + notificación persistente.
  - WebSocket (OkHttp) → envía JSON al backend: {type, deviceId, ts, screenOn, volume{level,max,percent}, foregroundApp}
  - Reconexión con backoff exponencial.
  - BOOT_COMPLETED → relanzar servicio.
  - Permisos: INTERNET, RECEIVE_BOOT_COMPLETED, WAKE_LOCK, FOREGROUND_SERVICE, POST_NOTIFICATIONS (api33+), PACKAGE_USAGE_STATS (declarado; se concede manualmente).
  - buildConfigFields: WS_URL (ej: ws://10.0.2.2:8000/ws), API_KEY (devkey).

- Backend (FastAPI):
  - GET /health
  - GET /devices → estado en memoria
  - WS /ws?mode={agent|viewer}&api_key=&device_id=
    - agent → recibe métricas y actualiza estado; hace broadcast a viewers.
    - viewer → recibe snapshot inicial y updates.

- Web (React + Vite):
  - Conectar por WebSocket en modo viewer y renderizar tabla en vivo (deviceId, screenOn, volume%, foregroundApp, lastSeen).
  - Reconexión automática.

Estructura final
android-agent/
  app/src/main/AndroidManifest.xml
  app/build.gradle
  build.gradle, settings.gradle
  src principales: MainActivity.kt, service/MetricsService.kt, core/MetricsCollector.kt, core/MetricsWsClient.kt, core/Notifications.kt, boot/BootReceiver.kt
backend-fastapi/
  app/main.py
  requirements.txt
web-dashboard/
  index.html
  src/main.tsx
  src/App.tsx
  src/useWs.ts

Calidad
- Código compilable, simple, sin dependencias innecesarias.
- Comentarios mínimos pero claros.
- README raíz con pasos para correr local.

Entrega de archivos (crea el contenido exacto):
