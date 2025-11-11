# 🚀 Guía Completa: Cómo Iniciar Todo el Sistema

## 📚 Aclaración Importante

### ¿Qué es cada cosa?

1. **Backend (Python/FastAPI)**
   - Es tu servidor Python que corre en `localhost:8000`
   - Recibe las métricas de los dispositivos Android
   - NO es ngrok, es tu código Python

2. **ngrok**
   - Es un servicio que expone tu `localhost:8000` a internet
   - Crea una URL pública (ej: `https://abc123.ngrok-free.app`)
   - Hace que tu backend local sea accesible desde internet
   - **NO es el backend**, solo es un "puente" a internet

**Resumen**: 
- Backend = Tu servidor Python (localhost:8000)
- ngrok = Túnel que expone tu backend a internet

---

## 🎯 Proceso Completo: 3 Terminales

Necesitas abrir **3 terminales PowerShell** diferentes:

---

### Terminal 1: Backend (Python)

**¿Qué hace?** Ejecuta tu servidor Python que recibe datos de los dispositivos Android.

**Comandos:**
```powershell
cd backend-fastapi
.\iniciar-backend.bat
```

**Nota:** El script detecta automáticamente si usar `py` o `python`, y usa el entorno virtual si existe.

**Qué verás:**
```
========================================
Iniciando Backend FastAPI
========================================

Activando entorno virtual...
Instalando dependencias...
Backend iniciando en http://localhost:8000
```

**✅ Verificación:** Abre `http://localhost:8000/health` en el navegador → Debe mostrar `{"status":"ok"}`

**⚠️ IMPORTANTE:** Deja esta terminal abierta. El backend debe seguir corriendo.

**Si el script no funciona:**
```powershell
cd backend-fastapi
.\venv\Scripts\activate.bat
cd app
python main.py
```

**Nota:** En PowerShell, usa `activate.bat` (no solo `activate`). Una vez activado el venv, `python` funcionará.

---

### Terminal 2: ngrok

**¿Qué hace?** Expone tu backend local a internet creando una URL pública.

**Comandos:**
```powershell
ngrok http 8000
```

**Qué verás:**
```
Forwarding: https://abc123.ngrok-free.app -> http://localhost:8000
```

**Copia esa URL** (ejemplo: `https://abc123.ngrok-free.app`)

**⚠️ IMPORTANTE:** Deja esta terminal abierta. ngrok debe seguir corriendo.

---

### Terminal 3: Dashboard Web

**¿Qué hace?** Muestra una interfaz web para ver los dispositivos conectados en tiempo real.

**Comandos:**
```powershell
cd web-dashboard
.\start.bat
```

**Qué verás:**
```
Iniciando Web Dashboard...
Instalando dependencias...
El dashboard estará disponible en http://localhost:5173
```

**Abre en el navegador:** `http://localhost:5173`

**⚠️ IMPORTANTE:** Deja esta terminal abierta. El dashboard debe seguir corriendo.

---

## 📋 Orden de Inicio (Paso a Paso)

### Paso 1: Iniciar Backend
```powershell
# Terminal 1
cd C:\Users\davso\Documents\Androidcontrol\backend-fastapi
.\iniciar-backend.bat
```
**Espera** a que veas "Backend iniciando en http://localhost:8000"

**Nota:** El script usa automáticamente `py` si `python` no está disponible, y usa el entorno virtual si existe.

**Si no funciona, usa manualmente:**
```powershell
cd C:\Users\davso\Documents\Androidcontrol\backend-fastapi
.\venv\Scripts\activate.bat
cd app
python main.py
```

**Nota:** En PowerShell, usa `activate.bat`. Una vez activado el entorno virtual, `python` funcionará porque el venv tiene sus propias dependencias instaladas.

---

### Paso 2: Iniciar ngrok
```powershell
# Terminal 2 (NUEVA)
ngrok http 8000
```
**Espera** a que aparezca la URL de ngrok (ej: `https://abc123.ngrok-free.app`)

**Copia esa URL** - La necesitarás para el APK

---

### Paso 3: Iniciar Dashboard Web
```powershell
# Terminal 3 (NUEVA)
cd C:\Users\davso\Documents\Androidcontrol\web-dashboard
.\start.bat
```
**Abre** `http://localhost:5173` en tu navegador

---

## 🔧 Configurar el APK con la URL de ngrok

Una vez que tengas la URL de ngrok del Paso 2:

### Paso 4: Actualizar build.gradle

1. Abre el archivo: `android-agent\app\build.gradle`

2. Busca la línea 21:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://stately-pennied-guy.ngrok-free.dev/ws\""
   ```

3. Reemplázala con tu URL de ngrok:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://TU_URL_NGROK/ws\""
   ```
   
   **Ejemplo:**
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://abc123.ngrok-free.app/ws\""
   ```
   
   **IMPORTANTE:**
   - Cambia `https://` por `wss://`
   - Agrega `/ws` al final
   - Mantén las comillas y barras invertidas

4. **Guarda** el archivo (Ctrl+S)

---

### Paso 5: Generar el APK

**Opción A: Desde Android Studio (Recomendado)**
1. Abre Android Studio
2. Abre el proyecto: `android-agent`
3. Ve a: `Build → Build Bundle(s) / APK(s) → Build APK(s)`
4. Espera a que termine
5. El APK estará en: `android-agent\app\build\outputs\apk\debug\app-debug.apk`

**Opción B: Desde Terminal**
```powershell
cd android-agent
.\gradlew.bat clean assembleDebug
```

---

### Paso 6: Instalar el APK

1. Copia el APK a tus dispositivos Android
2. Instálalo (habilitando "Instalar apps desconocidas" si es necesario)
3. Configura el Device ID cuando la app lo solicite
4. Concede el permiso "Uso de acceso" cuando lo pida

---

## ✅ Checklist de Verificación

Antes de considerar que todo está funcionando:

- [ ] **Terminal 1**: Backend corriendo → `http://localhost:8000/health` funciona
- [ ] **Terminal 2**: ngrok corriendo → URL visible en la terminal
- [ ] **Terminal 3**: Dashboard corriendo → `http://localhost:5173` se abre
- [ ] **build.gradle**: URL actualizada con la URL de ngrok
- [ ] **APK**: Generado e instalado en dispositivos
- [ ] **Dashboard**: Muestra dispositivos conectados

---

## 🔄 Flujo de Datos

```
Dispositivo Android
    ↓ (WebSocket)
ngrok (URL pública)
    ↓
Backend Python (localhost:8000)
    ↓ (WebSocket)
Dashboard Web (localhost:5173)
```

**Explicación:**
1. El dispositivo Android se conecta a la URL de ngrok
2. ngrok redirige la conexión a tu backend local
3. El backend recibe los datos y los almacena
4. El dashboard web se conecta al backend y muestra los datos

---

## 🛑 Cómo Detener Todo

Para detener el sistema:

1. **Terminal 1 (Backend)**: Presiona `Ctrl+C`
2. **Terminal 2 (ngrok)**: Presiona `Ctrl+C`
3. **Terminal 3 (Dashboard)**: Presiona `Ctrl+C`

O simplemente cierra las ventanas de terminal.

---

## 🐛 Problemas Comunes

### El backend no inicia
- Verifica que Python esté instalado: `py --version`
- Usa `.\iniciar-backend.bat` (detecta automáticamente `py` o `python`)

### ngrok no funciona
- Verifica que ngrok esté instalado: `ngrok --version`
- Configura tu authtoken: `ngrok config add-authtoken TU_TOKEN`

### El dashboard no muestra dispositivos
- Verifica que el backend esté corriendo
- Verifica que los dispositivos Android tengan el APK instalado
- Verifica que el permiso "Uso de acceso" esté concedido

### Los dispositivos no se conectan
- Verifica que ngrok esté corriendo
- Verifica que la URL en `build.gradle` sea correcta (debe ser `wss://` y terminar con `/ws`)
- Verifica que regeneraste el APK después de cambiar la URL

---

## 📝 Resumen de Comandos Rápidos

```powershell
# Terminal 1: Backend
cd backend-fastapi
.\iniciar-backend.bat

# Terminal 2: ngrok
ngrok http 8000

# Terminal 3: Dashboard
cd web-dashboard
.\start.bat
```

---

## 🎯 Conceptos Clave

| Componente | Qué es | Dónde corre | Puerto |
|------------|--------|-------------|--------|
| **Backend** | Tu servidor Python | Tu PC (localhost) | 8000 |
| **ngrok** | Túnel a internet | Tu PC (redirige) | - |
| **Dashboard** | Interfaz web | Tu PC (localhost) | 5173 |
| **APK Android** | App en dispositivos | Dispositivos Android | - |

**Recuerda:**
- Backend = Tu código Python (NO es ngrok)
- ngrok = Servicio que expone tu backend a internet
- Ambos deben estar corriendo para que funcione

---

¡Listo! Con esta guía deberías poder iniciar todo el sistema sin problemas. 🎉

