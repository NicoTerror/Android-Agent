import { useWs } from './useWs'
import { useServerUrl } from './useServerUrl'
import './App.css'

interface Device {
  deviceId: string
  screenOn: boolean
  volume: {
    level: number
    max: number
    percent: number
  }
  foregroundApp: string
  lastSeen: string
}

function App() {
  const { wsUrl, loading: urlLoading, error: urlError } = useServerUrl()
  const { devices, connected, error: wsError } = useWs(wsUrl)
  
  const error = urlError || wsError

  return (
    <div className="app">
      <header className="header">
        <h1>Android Control Dashboard</h1>
        <div className="status">
          <span className={`status-indicator ${connected ? 'connected' : 'disconnected'}`}>
            {connected ? '●' : '○'}
          </span>
          <span>
            {urlLoading ? 'Descubriendo servidor...' : connected ? 'Conectado' : 'Desconectado'}
          </span>
        </div>
      </header>

      {error && (
        <div className="error">
          Error: {error}
        </div>
      )}

      <main className="main">
        {Object.keys(devices).length === 0 ? (
          <div className="empty-state">
            No hay dispositivos conectados
          </div>
        ) : (
          <table className="devices-table">
            <thead>
              <tr>
                <th>Device ID</th>
                <th>Pantalla</th>
                <th>Volumen %</th>
                <th>App en Foreground</th>
                <th>Última Actualización</th>
              </tr>
            </thead>
            <tbody>
              {Object.values(devices).map((device: Device) => (
                <tr key={device.deviceId}>
                  <td>{device.deviceId}</td>
                  <td>
                    <span className={`badge ${device.screenOn ? 'on' : 'off'}`}>
                      {device.screenOn ? 'ON' : 'OFF'}
                    </span>
                  </td>
                  <td>{device.volume.percent.toFixed(1)}%</td>
                  <td className="app-name">{device.foregroundApp}</td>
                  <td>{new Date(device.lastSeen).toLocaleString('es-ES', { 
                    hour: '2-digit', 
                    minute: '2-digit', 
                    second: '2-digit',
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric'
                  })}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>
    </div>
  )
}

export default App

