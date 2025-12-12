# ⚡ Configuración Rápida para Internet

## 🎯 Opción Más Rápida: ngrok (5 minutos)

### Paso 1: Instalar ngrok
1. Descarga: https://ngrok.com/download
2. Extrae el archivo `ngrok.exe`
3. Regístrate gratis: https://dashboard.ngrok.com/signup
4. Copia tu authtoken del dashboard

### Paso 2: Configurar ngrok
```powershell
# En PowerShell, navega a donde está ngrok.exe
.\ngrok config add-authtoken TU_TOKEN_AQUI
```

### Paso 3: Iniciar el Backend
```powershell
cd backend-fastapi\app
python main.py
```

### Paso 4: Iniciar ngrok (Nueva Terminal)
```powershell
ngrok http 8000
```

Verás algo como:
```
Forwarding: https://abc123.ngrok-free.app -> http://localhost:8000
```

### Paso 5: Actualizar el APK

1. **Copia la URL** de ngrok (ej: `https://abc123.ngrok-free.app`)
2. **Edita** `android-agent/app/build.gradle` línea 19:
   ```gradle
   buildConfigField "String", "WS_URL", "\"wss://abc123.ngrok-free.app/ws\""
   ```
   **IMPORTANTE**: Usa `wss://` (no `ws://`) porque ngrok usa HTTPS

3. **Regenera el APK** desde Android Studio:
   - `Build → Build Bundle(s) / APK(s) → Build APK(s)`

4. **Distribuye el APK** a tus dispositivos

### ✅ Listo!

Ahora los dispositivos pueden conectarse desde cualquier ubicación.

---

## ⚠️ Notas Importantes

### URL de ngrok cambia
- En versión gratuita, la URL cambia cada vez que reinicias ngrok
- **Solución**: Cada vez que cambie, actualiza el APK con la nueva URL
- **Alternativa**: Cuenta paga de ngrok ($8/mes) te da URL fija

### Mantener ngrok corriendo
- ngrok debe estar corriendo mientras uses los dispositivos
- Si cierras ngrok, los dispositivos perderán conexión

### Múltiples dispositivos
- Puedes usar el mismo APK en todos los dispositivos
- Todos se conectarán a través de ngrok

---

## 🔄 Flujo de Trabajo

1. ✅ Iniciar backend: `python main.py`
2. ✅ Iniciar ngrok: `ngrok http 8000`
3. ✅ Copiar URL de ngrok
4. ✅ Actualizar `build.gradle` con la URL
5. ✅ Regenerar APK
6. ✅ Distribuir APK a dispositivos
7. ✅ ¡Funcionando desde cualquier lugar!

---

## 📋 Para Producción (URL Permanente)

Si necesitas una URL permanente, considera:

1. **Servidor VPS** (DigitalOcean, Linode, etc.) - ~$5/mes
2. **IP Pública + Port Forwarding** - Gratis pero requiere router
3. **ngrok con cuenta paga** - $8/mes, URL fija

Ver guía completa en: [CONFIGURACION_INTERNET.md](CONFIGURACION_INTERNET.md)








