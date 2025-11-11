# ⚡ Inicio Rápido

## 🎯 Ejecutar Todo en 3 Pasos

### 1️⃣ Backend (Terminal 1)

**Windows:**
```powershell
cd backend-fastapi
.\start.bat
```

**Linux/Mac:**
```bash
cd backend-fastapi
chmod +x start.sh
./start.sh
```

✅ **Verifica**: Abre `http://localhost:8000/health` → Debe mostrar `{"status":"ok"}`

---

### 2️⃣ Dashboard Web (Terminal 2 - Nueva)

**Windows:**
```powershell
cd web-dashboard
.\start.bat
```

**Linux/Mac:**
```bash
cd web-dashboard
chmod +x start.sh
./start.sh
```

✅ **Verifica**: Abre `http://localhost:5173` → Debe mostrar el dashboard

---

### 3️⃣ Android Agent

#### Opción A: Emulador
1. Abre `android-agent` en Android Studio
2. Configura en `app/build.gradle`:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://10.0.2.2:8000/ws\""
   ```
3. Ejecuta: `Run → Run 'app'`

#### Opción B: Dispositivo Físico
1. Obtén tu IP local: `ipconfig` (Windows) o `ifconfig` (Linux/Mac)
2. Configura en `app/build.gradle`:
   ```gradle
   buildConfigField "String", "WS_URL", "\"ws://TU_IP:8000/ws\""
   ```
   Ejemplo: `"ws://192.168.1.100:8000/ws"`
3. Compila e instala desde Android Studio

---

## ✅ Checklist de Verificación

- [ ] Backend corriendo → `http://localhost:8000/health` funciona
- [ ] Dashboard abierto → `http://localhost:5173` muestra la tabla
- [ ] App Android instalada
- [ ] Permiso "Uso de acceso" concedido en Android
- [ ] Notificación persistente visible en Android
- [ ] Dashboard muestra el dispositivo conectado
- [ ] Métricas se actualizan cada 3 segundos

---

## 🐛 Problemas Comunes

| Problema | Solución |
|----------|----------|
| Backend no inicia | Verifica Python 3.12, puerto 8000 libre |
| Dashboard no conecta | Verifica que backend esté corriendo |
| Android no conecta | Verifica IP/URL en build.gradle, misma red WiFi |
| No aparecen métricas | Concede permiso "Uso de acceso" en Android |

---

📖 **Guía completa**: Ver [GUIA_EJECUCION.md](GUIA_EJECUCION.md)

