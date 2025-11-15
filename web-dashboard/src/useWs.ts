import { useState, useEffect, useRef } from 'react'

interface Device {
  deviceId: string
  screenOn: boolean
  volume: {
    level: number
    max: number
    percent: number
  }
  foregroundApp: string
  screenBlocked: boolean | null  // null si no se puede determinar
  lastSeen: string
}

interface UseWsResult {
  devices: Record<string, Device>
  connected: boolean
  error: string | null
}

export function useWs(url: string): UseWsResult {
  const [devices, setDevices] = useState<Record<string, Device>>({})
  const [connected, setConnected] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const wsRef = useRef<WebSocket | null>(null)
  const reconnectTimeoutRef = useRef<number | null>(null)
  const reconnectDelayRef = useRef(1000)

  useEffect(() => {
    let isMounted = true

    const connect = () => {
      if (wsRef.current?.readyState === WebSocket.OPEN) {
        return
      }

      try {
        const ws = new WebSocket(url)
        wsRef.current = ws

        ws.onopen = () => {
          if (isMounted) {
            setConnected(true)
            setError(null)
            reconnectDelayRef.current = 1000 // Reset delay
          }
        }

        ws.onmessage = (event) => {
          if (!isMounted) return

          try {
            const message = JSON.parse(event.data)

            if (message.type === 'snapshot') {
              // Normalizar dispositivos para asegurar que screenBlocked esté presente
              const normalizedDevices: Record<string, Device> = {}
              if (message.devices) {
                Object.keys(message.devices).forEach((key) => {
                  const device = message.devices[key]
                  normalizedDevices[key] = {
                    ...device,
                    screenBlocked: device.screenBlocked ?? null
                  }
                })
              }
              setDevices(normalizedDevices)
            } else if (message.type === 'update') {
              const data = message.data
              if (data.deviceId) {
                setDevices((prev) => ({
                  ...prev,
                  [data.deviceId]: {
                    ...data,
                    screenBlocked: data.screenBlocked ?? null
                  }
                }))
              } else if (data.type === 'device_disconnected') {
                setDevices((prev) => {
                  const updated = { ...prev }
                  delete updated[data.deviceId]
                  return updated
                })
              }
            }
          } catch (e) {
            console.error('Error parsing message:', e)
          }
        }

        ws.onerror = (e) => {
          if (isMounted) {
            setError('Error de conexión WebSocket')
            console.error('WebSocket error:', e)
          }
        }

        ws.onclose = () => {
          if (isMounted) {
            setConnected(false)
            // Reconexión automática con backoff exponencial
            reconnectTimeoutRef.current = window.setTimeout(() => {
              if (isMounted) {
                reconnectDelayRef.current = Math.min(
                  reconnectDelayRef.current * 2,
                  60000
                )
                connect()
              }
            }, reconnectDelayRef.current)
          }
        }
      } catch (e) {
        if (isMounted) {
          setError('Error al crear conexión WebSocket')
          console.error('Connection error:', e)
        }
      }
    }

    connect()

    return () => {
      isMounted = false
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current)
      }
      if (wsRef.current) {
        wsRef.current.close()
        wsRef.current = null
      }
    }
  }, [url])

  return { devices, connected, error }
}

