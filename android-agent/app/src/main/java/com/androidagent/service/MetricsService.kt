package com.androidagent.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.androidagent.core.DeviceIdManager
import com.androidagent.core.MetricsCollector
import com.androidagent.core.MetricsWsClient
import com.androidagent.core.Notifications
import kotlinx.coroutines.*

class MetricsService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var metricsCollector: MetricsCollector? = null
    private var wsClient: MetricsWsClient? = null
    private var metricsJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        try {
            // Crear notificación persistente (debe hacerse dentro de 5 segundos)
            val notification = Notifications.createForegroundNotification(this)
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Foreground notification started")
            
            // Inicializar componentes
            metricsCollector = MetricsCollector(this)
            
            // Obtener Device ID personalizado
            val deviceId = DeviceIdManager.getDeviceId(this)
            if (deviceId.isEmpty()) {
                Log.e(TAG, "Device ID no configurado. El servicio no puede continuar.")
                stopSelf()
                return
            }
            
            Log.d(TAG, "Device ID: $deviceId")
            
            wsClient = MetricsWsClient(deviceId = deviceId)
            
            // Conectar WebSocket (no bloquea si falla)
            try {
                wsClient?.connect()
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting WebSocket (will retry)", e)
            }
            
            // Iniciar recolección de métricas cada 3 segundos
            startMetricsCollection()
            
            Log.d(TAG, "Service initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            // Aún así, mantener el servicio corriendo para que pueda reintentar
        }
    }

    private fun startMetricsCollection() {
        metricsJob = serviceScope.launch {
            while (isActive) {
                try {
                    val collector = metricsCollector
                    val client = wsClient
                    
                    if (collector != null && client != null) {
                        val metrics = collector.collectMetrics()
                        client.sendMetrics(metrics)
                    } else {
                        Log.w(TAG, "MetricsCollector or WsClient not initialized, skipping")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error collecting metrics", e)
                }
                delay(3000) // 3 segundos
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Reiniciar si el sistema lo mata
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        metricsJob?.cancel()
        try {
            // Limpiar sensores cuando el servicio se detiene
            metricsCollector?.cleanup()
            wsClient?.disconnect()
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting WebSocket", e)
        }
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "MetricsService"
        private const val NOTIFICATION_ID = 1
    }
}

