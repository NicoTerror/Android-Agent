# 📦 Generar APK para Instalación Manual

## 🎯 Método Recomendado: Desde Android Studio

### Paso 1: Abrir el Proyecto
1. Abre **Android Studio**
2. `File → Open → Selecciona la carpeta android-agent`

### Paso 2: Generar el APK
1. En la barra superior: `Build → Build Bundle(s) / APK(s) → Build APK(s)`
2. Espera a que termine la compilación (verás el progreso en la barra inferior)
3. Cuando termine, aparecerá una notificación: **"APK(s) generated successfully"**
4. Haz clic en **"locate"** en la notificación

### Paso 3: Ubicación del APK
El APK estará en:
```
android-agent\app\build\outputs\apk\debug\app-debug.apk
```

---

## 🔧 Método Alternativo: Desde Terminal (si Android Studio no funciona)

### Requisitos previos:
- Verifica tu conexión a internet
- Asegúrate de estar en la carpeta correcta

### Comandos:
```powershell
cd android-agent
.\gradlew.bat clean
.\gradlew.bat assembleDebug
```

Si hay problemas de conexión, intenta:
```powershell
.\gradlew.bat clean assembleDebug --offline
```

---

## 📱 Después de Generar el APK

Una vez que tengas el archivo `app-debug.apk`:

1. **Cópialo a tu dispositivo Android** (por USB, email, Drive, etc.)
2. **Habilita "Instalar apps desconocidas"** en tu dispositivo
3. **Instala el APK** desde el gestor de archivos
4. **Configura la app** (Device ID y permisos)

**Ver la guía completa en: [INSTALACION_APK.md](../INSTALACION_APK.md)**

---

## ⚙️ Configuración Actual del APK

- **IP del Backend**: `192.168.1.80:8000`
- **URL WebSocket**: `ws://192.168.1.80:8000/ws`
- **API Key**: `devkey`

**Nota**: Si tu IP cambia, edita `app/build.gradle` línea 19 y regenera el APK.

---

## 🐛 Solución de Problemas

### Error de conexión al generar APK
- Verifica tu conexión a internet
- Intenta desde Android Studio (más confiable)
- Verifica que los repositorios Maven estén accesibles

### El APK no se genera
- Asegúrate de que el proyecto compile sin errores
- Verifica que todas las dependencias estén descargadas
- Intenta `Build → Clean Project` y luego `Build → Rebuild Project`







