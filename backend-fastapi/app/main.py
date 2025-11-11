from fastapi import FastAPI, WebSocket, WebSocketDisconnect, Query, HTTPException
from fastapi.responses import JSONResponse
from typing import Dict, Set, Optional
import json
import asyncio
from datetime import datetime, timedelta
import requests
import threading
import time

app = FastAPI()

# Estado en memoria
devices: Dict[str, Dict] = {}
viewers: Set[WebSocket] = set()

# URL actual de ngrok (se actualiza automáticamente)
current_ngrok_url: Optional[str] = None
url_lock = threading.Lock()

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.get("/server-url")
async def get_server_url():
    """
    Retorna la URL actual del servidor WebSocket.
    Consulta la API local de ngrok para obtener la URL pública actual.
    """
    global current_ngrok_url
    
    with url_lock:
        if current_ngrok_url:
            # Convertir http/https a ws/wss
            ws_url = current_ngrok_url.replace("https://", "wss://").replace("http://", "ws://")
            if not ws_url.endswith("/ws"):
                ws_url = f"{ws_url}/ws"
            return {
                "ws_url": ws_url,
                "http_url": current_ngrok_url,
                "source": "ngrok"
            }
    
    # Fallback: URL local
    return {
        "ws_url": "ws://localhost:8000/ws",
        "http_url": "http://localhost:8000",
        "source": "local"
    }

def get_ngrok_url_from_api() -> Optional[str]:
    """
    Consulta la API local de ngrok para obtener la URL pública actual.
    Retorna None si ngrok no está disponible.
    """
    try:
        response = requests.get("http://localhost:4040/api/tunnels", timeout=2)
        if response.status_code == 200:
            data = response.json()
            tunnels = data.get("tunnels", [])
            if tunnels:
                # Obtener el primer túnel HTTP/HTTPS
                for tunnel in tunnels:
                    public_url = tunnel.get("public_url", "")
                    if public_url.startswith(("http://", "https://")):
                        return public_url
    except Exception as e:
        # ngrok no está disponible o no está corriendo
        pass
    return None

def update_ngrok_url_periodically():
    """
    Actualiza la URL de ngrok periódicamente consultando su API local.
    Se ejecuta en un hilo separado.
    """
    global current_ngrok_url
    
    while True:
        try:
            new_url = get_ngrok_url_from_api()
            if new_url:
                with url_lock:
                    if current_ngrok_url != new_url:
                        print(f"[URL Discovery] Nueva URL detectada: {new_url}")
                        current_ngrok_url = new_url
            else:
                # Si no se puede obtener la URL, mantener la anterior
                # o limpiar si es necesario
                pass
        except Exception as e:
            print(f"[URL Discovery] Error actualizando URL: {e}")
        
        # Consultar cada 10 segundos
        time.sleep(10)

# Iniciar hilo para actualizar URL periódicamente
url_updater_thread = threading.Thread(target=update_ngrok_url_periodically, daemon=True)
url_updater_thread.start()

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
                        "lastSeen": datetime.now().isoformat()
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
    uvicorn.run(app, host="0.0.0.0", port=8000)

