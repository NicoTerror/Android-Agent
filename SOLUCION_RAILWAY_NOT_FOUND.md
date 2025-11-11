# 🔧 Solución: Railway muestra "Not Found"

## Problema
Al visitar `https://tu-app.railway.app/health` obtienes "Not Found" en lugar de `{"status":"ok"}`.

## Causa
Railway busca el `Procfile` en la **raíz del repositorio**, pero estaba en `backend-fastapi/`.

## Solución Aplicada

### 1. Procfile en la Raíz
Se creó un `Procfile` en la raíz del proyecto que apunta correctamente al backend:

```
web: cd backend-fastapi && pip install -r requirements.txt && cd app && python -m uvicorn main:app --host 0.0.0.0 --port $PORT
```

### 2. runtime.txt en la Raíz
Se creó `runtime.txt` en la raíz para especificar la versión de Python.

## Pasos para Aplicar la Solución

### 1. Hacer Commit y Push

```powershell
git add Procfile runtime.txt
git commit -m "Fix: Procfile en raíz para Railway"
git push origin main
```

### 2. En Railway

1. Ve a tu proyecto en Railway
2. Railway detectará automáticamente el nuevo commit
3. Espera 2-3 minutos para que se despliegue
4. Verifica los logs en Railway (pestaña "Deployments")

### 3. Verificar que Funciona

1. Espera a que el despliegue termine (verás "Deployed" en Railway)
2. Visita: `https://tu-app.railway.app/health`
3. Deberías ver: `{"status":"ok"}`

## Verificar Logs en Railway

Si sigue sin funcionar:

1. En Railway, ve a tu servicio
2. Click en la pestaña **"Deployments"**
3. Click en el deployment más reciente
4. Revisa los logs para ver errores

### Errores Comunes

#### Error: "Module not found: fastapi"
**Solución**: Railway no está instalando las dependencias. Verifica que `requirements.txt` esté en `backend-fastapi/`.

#### Error: "No such file or directory: main.py"
**Solución**: El Procfile no está encontrando el archivo. Verifica que la ruta en el Procfile sea correcta.

#### Error: "Port already in use"
**Solución**: Railway maneja el puerto automáticamente. Asegúrate de usar `$PORT` en el Procfile.

## Configuración Alternativa en Railway

Si el Procfile en la raíz no funciona, puedes configurar Railway manualmente:

1. En Railway, ve a tu servicio
2. Click en **Settings**
3. En **Root Directory**, escribe: `backend-fastapi`
4. Guarda los cambios
5. Railway usará el `Procfile` de `backend-fastapi/`

## Verificación Final

Una vez desplegado correctamente:

- ✅ `https://tu-app.railway.app/health` → `{"status":"ok"}`
- ✅ `https://tu-app.railway.app/server-url` → JSON con `ws_url` y `http_url`
- ✅ `https://tu-app.railway.app/devices` → `{}` (vacío si no hay dispositivos)

## Si Aún No Funciona

1. **Verifica los logs** en Railway (pestaña "Deployments")
2. **Verifica que el código esté en GitHub** (haz push si falta)
3. **Verifica que Railway esté conectado** al repositorio correcto
4. **Revisa el Procfile** - debe estar en la raíz del repo
5. **Verifica requirements.txt** - debe estar en `backend-fastapi/`

## Contacto

Si después de estos pasos sigue sin funcionar, comparte:
- Los logs de Railway (pestaña "Deployments")
- El contenido de tu Procfile
- La estructura de tu repositorio

