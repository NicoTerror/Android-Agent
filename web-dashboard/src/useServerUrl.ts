import { useState, useEffect } from 'react'

// URL base para consultar el endpoint de descubrimiento
// En desarrollo: http://localhost:8000
// En producción: la URL de ngrok o servidor
const BASE_URL = import.meta.env.VITE_BASE_URL || 'http://localhost:8000'

interface ServerUrlResponse {
  ws_url: string
  http_url: string
  source: string
}

export function useServerUrl() {
  const [wsUrl, setWsUrl] = useState<string>('ws://localhost:8000/ws')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let isMounted = true
    let intervalId: number | null = null

    const discoverUrl = async () => {
      try {
        const base = BASE_URL.trim().replace(/\/$/, '')
        const discoveryUrl = `${base}/server-url`
        
        const response = await fetch(discoveryUrl)
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`)
        }

        const data: ServerUrlResponse = await response.json()
        
        if (isMounted && data.ws_url) {
          // Agregar parámetros de conexión
          const fullWsUrl = `${data.ws_url}?mode=viewer&api_key=devkey`
          setWsUrl(fullWsUrl)
          setError(null)
          console.log(`[URL Discovery] URL descubierta: ${fullWsUrl} (fuente: ${data.source})`)
        }
      } catch (e) {
        if (isMounted) {
          console.warn('[URL Discovery] Error consultando endpoint, usando fallback:', e)
          setError('No se pudo descubrir URL del servidor')
          // Usar fallback local
          setWsUrl('ws://localhost:8000/ws?mode=viewer&api_key=devkey')
        }
      } finally {
        if (isMounted) {
          setLoading(false)
        }
      }
    }

    // Consultar inmediatamente
    discoverUrl()

    // Consultar cada 30 segundos para detectar cambios
    intervalId = window.setInterval(discoverUrl, 30000)

    return () => {
      isMounted = false
      if (intervalId) {
        clearInterval(intervalId)
      }
    }
  }, [])

  return { wsUrl, loading, error }
}


