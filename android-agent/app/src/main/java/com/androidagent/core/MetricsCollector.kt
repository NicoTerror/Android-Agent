package com.androidagent.core

import android.app.AppOpsManager
import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.PowerManager
import java.util.concurrent.TimeUnit

data class Metrics(
    val screenOn: Boolean,
    val volume: VolumeInfo,
    val foregroundApp: String
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
        
        return Metrics(
            screenOn = screenOn,
            volume = volume,
            foregroundApp = foregroundApp
        )
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

