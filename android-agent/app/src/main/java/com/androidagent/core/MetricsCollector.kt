package com.androidagent.core

import android.app.AppOpsManager
import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager
import java.util.concurrent.TimeUnit

data class Metrics(
    val screenOn: Boolean,
    val volume: VolumeInfo,
    val foregroundApp: String,
    val screenBlocked: Boolean?  // null si no se puede determinar (no hay sensores)
)

data class VolumeInfo(
    val level: Int,
    val max: Int,
    val percent: Float
)

class MetricsCollector(private val context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Sensor de luz ambiental para detectar bloqueo físico
    private var lightSensor: Sensor? = null
    private var lightValue = -1f
    
    // Historial de lecturas de luz para calcular promedio (evitar falsos positivos)
    private val lightHistory = mutableListOf<Float>()
    private val maxHistorySize = 5
    
    // Umbral de luz: si está por debajo, la pantalla está bloqueada
    // Valores típicos: 0-5 lux = muy oscuro (bloqueado), >10 lux = normal
    private val LIGHT_THRESHOLD = 5.0f  // lux
    
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                lightValue = event.values[0]
                // Mantener historial de lecturas para calcular promedio
                lightHistory.add(lightValue)
                if (lightHistory.size > maxHistorySize) {
                    lightHistory.removeAt(0)
                }
            }
        }
        
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // No necesario para esta implementación
        }
    }
    
    init {
        // Inicializar sensor de luz ambiental si está disponible
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensor?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun collectMetrics(): Metrics {
        val screenOn = powerManager.isInteractive
        
        val volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumePercent = if (volumeMax > 0) (volumeLevel.toFloat() / volumeMax) * 100f else 0f
        
        val volume = VolumeInfo(
            level = volumeLevel,
            max = volumeMax,
            percent = volumePercent
        )
        
        val foregroundApp = getForegroundApp()
        
        // Detectar si la pantalla está bloqueada físicamente
        val screenBlocked = detectScreenBlocked()
        
        return Metrics(
            screenOn = screenOn,
            volume = volume,
            foregroundApp = foregroundApp,
            screenBlocked = screenBlocked
        )
    }
    
    /**
     * Detecta si la pantalla está bloqueada físicamente usando el sensor de luz ambiental.
     * Si no hay sensor disponible, retorna null.
     */
    private fun detectScreenBlocked(): Boolean? {
        if (lightSensor == null) {
            return null  // No se puede determinar sin sensor
        }
        
        // Si aún no hay suficientes lecturas, esperar
        if (lightHistory.isEmpty()) {
            return false  // Asumir que no está bloqueado hasta tener datos
        }
        
        // Calcular promedio de luz del historial para mayor precisión
        val sum = lightHistory.sum()
        val count = lightHistory.size
        if (count == 0) {
            return false
        }
        val averageLight = (sum / count)
        
        // Si la luz promedio es muy baja, la pantalla está bloqueada
        return averageLight < LIGHT_THRESHOLD
    }
    
    /**
     * Limpia los recursos del sensor cuando ya no se necesiten
     */
    fun cleanup() {
        if (lightSensor != null) {
            sensorManager.unregisterListener(sensorListener)
        }
        lightHistory.clear()
    }

    private fun getForegroundApp(): String {
        if (!hasUsageStatsPermission()) {
            return "permission_required"
        }
        
        // Usar UsageStatsManager como método principal (más confiable con el permiso)
        val time = System.currentTimeMillis()
        // Usar intervalo más corto para obtener resultados más actuales
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            time - TimeUnit.SECONDS.toMillis(10), // Últimos 10 segundos
            time
        )
        
        var mostRecent = ""
        var mostRecentTime = 0L
        
        stats?.forEach { usageStats ->
            // Verificar que la app se haya usado recientemente (últimos 10 segundos)
            val timeSinceLastUsed = time - usageStats.lastTimeUsed
            if (timeSinceLastUsed <= TimeUnit.SECONDS.toMillis(10)) {
                if (usageStats.lastTimeUsed > mostRecentTime) {
                    mostRecentTime = usageStats.lastTimeUsed
                    mostRecent = usageStats.packageName
                }
            }
        }
        
        // Si no encontramos nada en los últimos 10 segundos, buscar en un rango más amplio
        if (mostRecent.isEmpty()) {
            val statsWider = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - TimeUnit.MINUTES.toMillis(1),
                time
            )
            
            statsWider?.forEach { usageStats ->
                if (usageStats.lastTimeUsed > mostRecentTime) {
                    mostRecentTime = usageStats.lastTimeUsed
                    mostRecent = usageStats.packageName
                }
            }
        }
        
        if (mostRecent.isNotEmpty()) {
            // Intentar obtener el nombre de la app en lugar del package name
            val appName = getAppName(mostRecent)
            return appName ?: mostRecent
        }
        
        // Método alternativo usando ActivityManager si UsageStats no funciona
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses = activityManager.runningAppProcesses
            
            if (runningProcesses != null) {
                // Buscar el proceso con importancia FOREGROUND y más reciente
                var foregroundProcess: ActivityManager.RunningAppProcessInfo? = null
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        foregroundProcess = processInfo
                        break
                    }
                }
                
                if (foregroundProcess != null) {
                    val packageName = foregroundProcess.pkgList?.getOrNull(0)
                    if (packageName != null) {
                        val appName = getAppName(packageName)
                        return appName ?: packageName
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("MetricsCollector", "Error getting foreground app from ActivityManager", e)
        }
        
        return "unknown"
    }
    
    private fun getAppName(packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

