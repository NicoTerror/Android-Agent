from fastapi import FastAPI, WebSocket, WebSocketDisconnect, Query, HTTPException, Request
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from typing import Dict, Set, Optional
import json
import os
from datetime import datetime, timezone

app = FastAPI()

# Configurar CORS para permitir peticiones desde el web dashboard
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especifica los orígenes permitidos
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Estado en memoria
devices: Dict[str, Dict] = {}
viewers: Set[WebSocket] = set()

@app.get("/")
async def root():
    """Endpoint raíz - información del servicio"""
    return {
        "service": "Android Control Backend",
        "status": "running",
        "endpoints": {
            "health": "/health",
            "server_url": "/server-url",
            "devices": "/devices",
            "websocket": "/ws"
        }
    }

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.get("/server-url")
async def get_server_url(request: Request):
    """
    Retorna la URL actual del servidor WebSocket.
    En Railway, usa la URL del servicio directamente.
    """
    # Obtener URL base de Railway desde variables de entorno o headers
    base_url = os.getenv("RAILWAY_PUBLIC_DOMAIN") or os.getenv("RAILWAY_STATIC_URL")
    
    # Si no está en variables de entorno, construir desde el request
    if not base_url:
        # Railway proporciona el host en los headers
        host = request.headers.get("host", "")
        if host:
            # Railway siempre usa HTTPS
            base_url = f"https://{host}"
        else:
            # Fallback: usar variable de entorno personalizada o localhost
            base_url = os.getenv("BASE_URL", "http://localhost:8000")
    
    # Asegurar que la URL base tenga protocolo
    if not base_url.startswith(("http://", "https://")):
        base_url = f"https://{base_url}"
    
    # Convertir a WebSocket URL
    ws_url = base_url.replace("https://", "wss://").replace("http://", "ws://")
    if not ws_url.endswith("/ws"):
        ws_url = f"{ws_url}/ws"
    
    return {
        "ws_url": ws_url,
        "http_url": base_url,
        "source": "railway" if "railway" in base_url.lower() or os.getenv("RAILWAY_ENVIRONMENT") else "local"
    }

@app.get("/devices")
async def get_devices():
    """Retorna el estado actual de todos los dispositivos"""
    return devices

@app.websocket("/ws")
async def websocket_endpoint(
    websocket: WebSocket,
    mode: str = Query(...),
    api_key: str = Query(...),
    device_id: str = Query(None)
):
    await websocket.accept()
    
    if mode == "agent":
        if not device_id:
            await websocket.close(code=1008, reason="device_id required for agent mode")
            return
        
        try:
            while True:
                data = await websocket.receive_text()
                message = json.loads(data)
                
                if message.get("type") == "metrics":
                    # Actualizar estado del dispositivo
                    devices[device_id] = {
                        "deviceId": device_id,
                        "screenOn": message.get("screenOn", False),
                        "volume": message.get("volume", {}),
                        "foregroundApp": message.get("foregroundApp", "unknown"),
                        "lastSeen": datetime.now(timezone.utc).isoformat()
                    }
                    
                    # Broadcast a todos los viewers
                    await broadcast_to_viewers(devices[device_id])
        except WebSocketDisconnect:
            # Remover dispositivo si se desconecta
            if device_id in devices:
                del devices[device_id]
            await broadcast_to_viewers({"type": "device_disconnected", "deviceId": device_id})
        except Exception as e:
            print(f"Error in agent connection: {e}")
            await websocket.close(code=1011, reason=str(e))
    
    elif mode == "viewer":
        # Agregar viewer
        viewers.add(websocket)
        
        # Enviar snapshot inicial
        await websocket.send_json({
            "type": "snapshot",
            "devices": devices
        })
        
        try:
            # Mantener conexión abierta
            while True:
                await websocket.receive_text()
        except WebSocketDisconnect:
            viewers.remove(websocket)
        except Exception as e:
            print(f"Error in viewer connection: {e}")
            viewers.discard(websocket)
            await websocket.close(code=1011, reason=str(e))
    else:
        await websocket.close(code=1008, reason="Invalid mode. Use 'agent' or 'viewer'")

async def broadcast_to_viewers(message: Dict):
    """Envía un mensaje a todos los viewers conectados"""
    if not viewers:
        return
    
    disconnected = set()
    for viewer in viewers:
        try:
            await viewer.send_json({
                "type": "update",
                "data": message
            })
        except Exception as e:
            print(f"Error broadcasting to viewer: {e}")
            disconnected.add(viewer)
    
    # Remover viewers desconectados
    for viewer in disconnected:
        viewers.discard(viewer)

if __name__ == "__main__":
    import uvicorn
    # Railway usa la variable de entorno PORT, localhost usa 8000
    port = int(os.getenv("PORT", 8000))
    uvicorn.run(app, host="0.0.0.0", port=port)

