# 🚀 Guía Completa: Desplegar en PythonAnywhere

Esta guía te llevará paso a paso para desplegar el backend FastAPI en PythonAnywhere.

## 📋 Requisitos Previos

- ✅ Cuenta en PythonAnywhere creada (gratis)
- ✅ Código del proyecto en tu computadora
- ✅ Acceso a la consola de PythonAnywhere

## ⚠️ Nota Importante sobre WebSockets

**PythonAnywhere en el plan gratuito tiene limitaciones con WebSockets.** El plan gratuito solo permite conexiones WebSocket desde dominios whitelisted. Sin embargo, podemos intentar configurarlo y ver si funciona. Si no funciona, tendrás que considerar:

- Actualizar a un plan de pago de PythonAnywhere ($5/mes)
- O usar otra plataforma que soporte WebSockets gratis

---

## Paso 1: Subir tu Código a PythonAnywhere

Tienes **3 opciones** para subir tu código:

### Opción A: Usar Git (Recomendado) ⭐

Si tu código está en GitHub:

1. **En PythonAnywhere, abre una consola Bash**:
   - Ve a la pestaña "Consoles"
   - Click en "Bash"
   
2. **Clona tu repositorio**:
   ```bash
   cd ~
   git clone https://github.com/TU_USUARIO/TU_REPOSITORIO.git Androidcontrol
   ```
   (Reemplaza `TU_USUARIO` y `TU_REPOSITORIO` con tus datos)

### Opción B: Subir Archivos Manualmente

1. **En PythonAnywhere, ve a la pestaña "Files"**
2. **Navega a `/home/TU_USUARIO/`** (reemplaza `TU_USUARIO` con tu nombre de usuario)
3. **Crea una carpeta llamada `Androidcontrol`**
4. **Sube todos los archivos de `backend-fastapi/`**:
   - `app/main.py`
   - `requirements.txt`
   - `wsgi.py`
   - `runtime.txt` (si existe)

### Opción C: Usar el Editor Web

1. **En PythonAnywhere, ve a "Files"**
2. **Crea los archivos necesarios** copiando y pegando el contenido

---

## Paso 2: Instalar Dependencias

1. **Abre una consola Bash** en PythonAnywhere
2. **Navega a tu proyecto**:
   ```bash
   cd ~/Androidcontrol/backend-fastapi
   ```
3. **Crea un entorno virtual** (recomendado):
   ```bash
   python3.10 -m venv venv
   source venv/bin/activate
   ```
   (PythonAnywhere usa Python 3.10 por defecto)

4. **Instala las dependencias**:
   ```bash
   pip install --user -r requirements.txt
   ```
   
   **Nota**: El flag `--user` es importante en PythonAnywhere

---

## Paso 3: Configurar el Archivo WSGI

1. **Edita el archivo `wsgi.py`** que creamos:
   - Ve a "Files" en PythonAnywhere
   - Abre `/home/TU_USUARIO/Androidcontrol/backend-fastapi/wsgi.py`
   - **Reemplaza `TU_USUARIO`** con tu nombre de usuario real

   Ejemplo si tu usuario es `nicolasrtr20`:
   ```python
   path = '/home/nicolasrtr20/Androidcontrol/backend-fastapi'
   ```

2. **Guarda el archivo**

---

## Paso 4: Configurar la Aplicación Web en PythonAnywhere

1. **Ve a la pestaña "Web"** en PythonAnywhere
2. **Click en "Add a new web app"**
3. **Selecciona "Manual configuration"**
4. **Selecciona Python 3.10**
5. **Click en "Next"**

### Configuración del WSGI:

1. **En la sección "Code"**, busca el archivo WSGI
2. **Edita el archivo WSGI** (click en el enlace)
3. **Reemplaza TODO el contenido** con:

```python
import sys
import os

# Agregar el directorio del proyecto al path
path = '/home/TU_USUARIO/Androidcontrol/backend-fastapi'
if path not in sys.path:
    sys.path.insert(0, path)

# Cambiar al directorio de la app
os.chdir(path)

# Importar la aplicación FastAPI
from app.main import app

# PythonAnywhere necesita que la variable se llame 'application'
application = app
```

**⚠️ IMPORTANTE**: Reemplaza `TU_USUARIO` con tu nombre de usuario real.

4. **Guarda el archivo**

### Configurar la Ruta:

1. **En la sección "Reload"**, verifica que la ruta sea:
   ```
   /home/TU_USUARIO/Androidcontrol/backend-fastapi/wsgi.py
   ```

---

## Paso 5: Configurar Variables de Entorno (Opcional)

Si necesitas variables de entorno:

1. **En la pestaña "Web"**, busca "Environment variables"
2. **Agrega variables** si las necesitas:
   - `PYTHONANYWHERE_SITE`: Tu dominio (ej: `nicolasrtr20`)

---

## Paso 6: Obtener tu URL

1. **En la pestaña "Web"**, verás tu URL:
   - Formato: `https://TU_USUARIO.pythonanywhere.com`
   - Ejemplo: `https://nicolasrtr20.pythonanywhere.com`

2. **Copia esta URL** - la necesitarás después

---

## Paso 7: Probar el Backend

1. **Click en el botón verde "Reload"** en la pestaña "Web"
2. **Espera unos segundos** para que se recargue
3. **Abre en tu navegador**: `https://TU_USUARIO.pythonanywhere.com/health`
4. **Deberías ver**: `{"status":"ok"}`

Si funciona, ¡el backend está desplegado! 🎉

---

## Paso 8: Probar WebSockets

**⚠️ IMPORTANTE**: El plan gratuito de PythonAnywhere puede tener limitaciones con WebSockets.

1. **Abre**: `https://TU_USUARIO.pythonanywhere.com/server-url`
2. **Deberías ver** la URL del WebSocket
3. **Prueba desde tu app Android** o dashboard web

Si los WebSockets no funcionan:
- Puede ser una limitación del plan gratuito
- Considera actualizar a un plan de pago ($5/mes)
- O prueba con otra plataforma

---

## Paso 9: Configurar la App Android

1. **Abre**: `android-agent/app/build.gradle`
2. **Busca las líneas**:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://...\""
   ```
3. **Actualiza con tu URL de PythonAnywhere**:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://TU_USUARIO.pythonanywhere.com/ws\""
   ```
   **Nota**: Usa `wss://` (WebSocket seguro) porque PythonAnywhere usa HTTPS

4. **Regenera el APK**

---

## Paso 10: Configurar el Web Dashboard

1. **En `web-dashboard/`, crea un archivo `.env`**:
   ```
   VITE_BASE_URL=https://TU_USUARIO.pythonanywhere.com
   ```

2. **Reinicia el servidor de desarrollo**:
   ```powershell
   cd web-dashboard
   npm run dev
   ```

---

## 🔧 Solución de Problemas

### Error: "Module not found"

**Solución**: 
1. Verifica que instalaste las dependencias con `pip install --user -r requirements.txt`
2. Verifica que el path en `wsgi.py` sea correcto

### Error: "500 Internal Server Error"

**Solución**:
1. Ve a la pestaña "Web" → "Error log"
2. Revisa los errores
3. Verifica que el archivo `wsgi.py` tenga el path correcto

### WebSockets no funcionan

**Solución**:
- El plan gratuito puede tener limitaciones
- Verifica los logs en "Error log"
- Considera actualizar a un plan de pago

### La aplicación no se recarga

**Solución**:
1. Click en el botón verde "Reload" en la pestaña "Web"
2. Espera 10-20 segundos
3. Verifica los logs si hay errores

---

## 📊 Monitoreo

### Ver Logs:
1. **Pestaña "Web"** → **"Error log"** (errores)
2. **Pestaña "Web"** → **"Server log"** (logs del servidor)

### Verificar Estado:
- `https://TU_USUARIO.pythonanywhere.com/health` → Debe responder `{"status":"ok"}`
- `https://TU_USUARIO.pythonanywhere.com/server-url` → Debe mostrar la URL WebSocket

---

## 🔄 Actualizar el Código

Cada vez que hagas cambios:

1. **Si usas Git**:
   ```bash
   cd ~/Androidcontrol
   git pull
   ```

2. **Si subes manualmente**: Sube los archivos modificados

3. **Recarga la aplicación**:
   - Ve a la pestaña "Web"
   - Click en "Reload"

---

## 💰 Costos

### Plan Gratuito:
- ✅ Suficiente para desarrollo
- ⚠️ Limitaciones con WebSockets
- ⚠️ Aplicación se suspende después de inactividad

### Plan Hacker ($5/mes):
- ✅ WebSockets funcionan completamente
- ✅ Sin suspensiones
- ✅ Más recursos

---

## 🎉 ¡Listo!

Ahora tienes:
- ✅ Backend desplegado en PythonAnywhere
- ✅ URL fija permanente
- ✅ HTTPS automático
- ⚠️ WebSockets pueden tener limitaciones en plan gratuito

**Tu URL de PythonAnywhere es permanente** - no cambiará a menos que elimines la aplicación.

---

## 📝 Resumen Rápido

1. ✅ Subir código a PythonAnywhere (Git o manual)
2. ✅ Instalar dependencias
3. ✅ Configurar `wsgi.py` con tu usuario
4. ✅ Crear aplicación web en PythonAnywhere
5. ✅ Configurar archivo WSGI
6. ✅ Obtener URL de PythonAnywhere
7. ✅ Probar `/health`
8. ✅ Actualizar `build.gradle` con la nueva URL
9. ✅ Regenerar APK
10. ✅ Configurar `.env` en web-dashboard

**¡Tu sistema está listo para monitorear tablets en producción!** 🚀

