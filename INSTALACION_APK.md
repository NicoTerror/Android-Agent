# 📱 Guía de Instalación del APK en Dispositivos Android

Esta guía te explica cómo generar e instalar el APK en dispositivos Android reales sin necesidad de Android Studio o depuración USB.

## 🚀 Paso 1: Generar el APK

### Opción A: Usando el script (Recomendado)

1. **Abre PowerShell o CMD** en la carpeta del proyecto
2. **Ejecuta el script**:
   ```powershell
   cd android-agent
   .\generar-apk.bat
   ```

### Opción B: Manualmente desde terminal

```powershell
cd android-agent
.\gradlew clean
.\gradlew assembleDebug
```

### Opción C: Desde Android Studio

1. Abre el proyecto en Android Studio
2. `Build → Build Bundle(s) / APK(s) → Build APK(s)`
3. Espera a que termine la compilación
4. Haz clic en "locate" cuando aparezca la notificación

## 📍 Ubicación del APK

El APK se generará en:
```
android-agent\app\build\outputs\apk\debug\app-debug.apk
```

## 📲 Paso 2: Transferir el APK al Dispositivo

Tienes varias opciones:

### Opción 1: Por USB (Más rápido)
1. Conecta tu dispositivo Android a la PC por USB
2. Copia el archivo `app-debug.apk` a la carpeta de descargas del dispositivo
3. Desconecta el dispositivo

### Opción 2: Por WiFi/Red
1. Comparte el archivo por email, Google Drive, Dropbox, etc.
2. Descárgalo en tu dispositivo Android

### Opción 3: Por Bluetooth
1. Envía el archivo por Bluetooth desde tu PC al dispositivo

## 🔓 Paso 3: Habilitar Instalación de Fuentes Desconocidas

Antes de instalar, necesitas permitir la instalación de apps de fuentes desconocidas:

1. **Abre Ajustes** en tu dispositivo Android
2. **Seguridad** (o **Privacidad** en versiones recientes)
3. Busca **"Instalar apps desconocidas"** o **"Fuentes desconocidas"**
4. Selecciona el navegador o gestor de archivos que usarás (Chrome, Files, etc.)
5. **Activa el permiso**

**Nota**: En Android 8.0+ (Oreo), debes dar permiso por aplicación. Si usas el gestor de archivos del sistema, busca "Archivos" o "Files" en la lista.

## 📥 Paso 4: Instalar el APK

1. **Abre el gestor de archivos** en tu dispositivo (Files, Mi Files, etc.)
2. **Navega** a la carpeta donde copiaste el APK (generalmente Descargas)
3. **Toca el archivo** `app-debug.apk`
4. **Toca "Instalar"**
5. Si aparece una advertencia de seguridad, toca **"Instalar de todas formas"** o **"Instalar"**
6. Espera a que termine la instalación
7. Toca **"Abrir"** o busca "Android Agent" en el menú de apps

## ⚙️ Paso 5: Configurar la App

Una vez instalada:

1. **Abre la app "Android Agent"**
2. **Ingresa un Device ID** cuando te lo pida (solo números, ej: `12345`)
3. **Concede el permiso de "Uso de acceso"**:
   - La app te mostrará un diálogo
   - Toca "Abrir Configuración"
   - En la pantalla que se abre, busca "Android Agent"
   - **Activa el toggle** para permitir el acceso
   - Regresa a la app
4. **Concede permisos de notificaciones** si se solicitan

## 🌐 Paso 6: Verificar la Conexión

1. **Asegúrate de que el backend esté corriendo** en tu PC:
   ```powershell
   cd backend-fastapi\app
   python main.py
   ```

2. **Abre el dashboard web** en tu PC:
   ```
   http://localhost:5173
   ```

3. **Verifica que tu dispositivo aparezca** en la tabla con el Device ID que ingresaste

4. **Las métricas deberían actualizarse** cada 3 segundos

## ⚠️ Notas Importantes

### IP del Backend

- El APK está configurado para conectarse a: `ws://192.168.1.80:8000/ws`
- **Asegúrate de que tu PC y el dispositivo estén en la misma red WiFi**
- Si tu IP cambia, necesitarás regenerar el APK con la nueva IP

### Firewall

- Asegúrate de que el firewall de Windows permita conexiones en el puerto 8000
- Si no funciona, abre el puerto manualmente o desactiva temporalmente el firewall

### Múltiples Dispositivos

- Puedes instalar el mismo APK en múltiples dispositivos
- Cada dispositivo debe tener un Device ID diferente
- Todos aparecerán en el dashboard web

## 🔄 Actualizar el APK

Si necesitas actualizar la app:

1. **Genera un nuevo APK** (mismo proceso del Paso 1)
2. **Instálalo sobre la versión anterior** (no necesitas desinstalar)
3. El Device ID y permisos se mantendrán

## 🐛 Solución de Problemas

### El APK no se instala
- Verifica que tengas "Instalar apps desconocidas" activado
- Asegúrate de que el archivo no esté corrupto (vuelve a generarlo)

### La app no se conecta al backend
- Verifica que el backend esté corriendo
- Verifica que ambos (PC y dispositivo) estén en la misma red WiFi
- Verifica tu IP actual: `ipconfig` en PowerShell
- Si la IP cambió, regenera el APK con la nueva IP

### No aparecen métricas
- Verifica que el permiso de "Uso de acceso" esté concedido
- Reinicia la app
- Verifica que el servicio esté corriendo (deberías ver una notificación)

## 📝 Resumen Rápido

1. ✅ Genera el APK: `.\generar-apk.bat`
2. ✅ Copia `app-debug.apk` a tu dispositivo
3. ✅ Habilita "Instalar apps desconocidas"
4. ✅ Instala el APK desde el gestor de archivos
5. ✅ Configura Device ID y permisos
6. ✅ ¡Listo! Tu dispositivo aparecerá en el dashboard





