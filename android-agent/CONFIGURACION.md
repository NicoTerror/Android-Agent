# Configuración Android Agent

## 🔧 Configuración para Emulación

El proyecto está **preconfigurado para emulador Android** con la IP `10.0.2.2:8000`.

### Verificar Configuración Actual

Abre `app/build.gradle` y verifica estas líneas en `defaultConfig`:

```gradle
buildConfigField "String", "WS_URL", "\"ws://10.0.2.2:8000/ws\""
buildConfigField "String", "API_KEY", "\"devkey\""
```

✅ **Para emulador**: Ya está configurado correctamente (`10.0.2.2`)

---

## 📱 Cambiar a Dispositivo Físico

Si quieres usar un dispositivo físico en lugar del emulador:

1. **Obtén tu IP local**:
   - Windows: `ipconfig` → Busca "IPv4"
   - Linux/Mac: `ifconfig` o `ip addr`

2. **Edita `app/build.gradle`**:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://TU_IP:8000/ws\""
   ```
   Ejemplo: `"ws://192.168.1.100:8000/ws"`

3. **Recompila**:
   ```bash
   ./gradlew clean assembleDebug
   ```

---

## ✅ Checklist Pre-Compilación

- [ ] Backend corriendo en `localhost:8000`
- [ ] URL WebSocket correcta en `build.gradle`
- [ ] Emulador iniciado (si usas emulador)
- [ ] Dispositivo conectado por USB (si usas dispositivo físico)

---

## 🚀 Compilar e Instalar

### Desde Android Studio:
1. `Build → Make Project` (Ctrl+F9)
2. `Run → Run 'app'` (Shift+F10)

### Desde Terminal:
```bash
cd android-agent
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔐 Permisos Requeridos

Después de instalar:

1. **Permisos básicos**: Se solicitan automáticamente
2. **Uso de acceso** (crítico):
   - `Ajustes → Apps → Android Agent → Uso de acceso`
   - Activar el toggle
   - Sin esto, `foregroundApp` mostrará "permission_required"

---

## 🐛 Solución de Problemas

### No se conecta al backend
- ✅ Verifica que el backend esté corriendo: `http://localhost:8000/health`
- ✅ Verifica la URL en `build.gradle`
- ✅ Para emulador: usa `10.0.2.2`
- ✅ Para dispositivo físico: usa tu IP local, misma red WiFi

### No aparecen métricas
- ✅ Concede permiso "Uso de acceso"
- ✅ Verifica Logcat en Android Studio para errores

### BuildConfig no se genera
- ✅ Verifica que `buildFeatures { buildConfig = true }` esté en `build.gradle`
- ✅ Limpia y reconstruye: `./gradlew clean build`

