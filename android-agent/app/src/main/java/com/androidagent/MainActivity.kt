package com.androidagent

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidagent.core.DeviceIdManager
import com.androidagent.service.MetricsService

class MainActivity : AppCompatActivity() {
    private val NOTIFICATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar si ya tiene Device ID configurado
        if (!DeviceIdManager.hasDeviceId(this)) {
            showDeviceIdDialog()
            return
        }
        
        // Verificar permiso de Usage Stats
        if (!hasUsageStatsPermission()) {
            showUsageStatsPermissionDialog()
            return
        }
        
        // Solicitar permiso de notificaciones si es necesario (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST
                )
                return // Esperar a que se conceda el permiso
            }
        }
        
        // Iniciar el servicio una vez que tenemos los permisos
        startService()
    }
    
    override fun onResume() {
        super.onResume()
        // Verificar si el usuario concedió el permiso al regresar de la configuración
        if (DeviceIdManager.hasDeviceId(this)) {
            if (hasUsageStatsPermission()) {
                // Si ya tiene el permiso de Usage Stats, continuar con el flujo
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            NOTIFICATION_PERMISSION_REQUEST
                        )
                        return
                    }
                }
                // Si ya tiene todos los permisos, iniciar el servicio
                startService()
            }
        }
    }
    
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    private fun showUsageStatsPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de Uso de Aplicaciones")
            .setMessage("Para detectar qué aplicación está en uso, necesitamos el permiso de 'Uso de acceso'. Se abrirá la configuración del sistema donde deberás activar el permiso para 'Android Agent'.")
            .setPositiveButton("Abrir Configuración") { _, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
                Toast.makeText(
                    this,
                    "Active el permiso para 'Android Agent' y regrese a la app",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showDeviceIdDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Ingrese solo números"
        
        AlertDialog.Builder(this)
            .setTitle("Configurar Device ID")
            .setMessage("Ingrese un ID numérico para este dispositivo:")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val deviceId = input.text.toString().trim()
                if (deviceId.isNotEmpty() && deviceId.all { it.isDigit() }) {
                    DeviceIdManager.saveDeviceId(this, deviceId)
                    Toast.makeText(this, "Device ID guardado: $deviceId", Toast.LENGTH_SHORT).show()
                    
                    // Después de guardar Device ID, verificar Usage Stats
                    if (!hasUsageStatsPermission()) {
                        showUsageStatsPermissionDialog()
                        return@setPositiveButton
                    }
                    
                    // Continuar con el flujo normal
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                NOTIFICATION_PERMISSION_REQUEST
                            )
                            return@setPositiveButton
                        }
                    }
                    startService()
                } else {
                    Toast.makeText(this, "El ID debe contener solo números", Toast.LENGTH_LONG).show()
                    showDeviceIdDialog() // Mostrar de nuevo si es inválido
                }
            }
            .setCancelable(false)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService()
            } else {
                Toast.makeText(
                    this,
                    "Se requiere permiso de notificaciones para el servicio",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun startService() {
        try {
            val serviceIntent = Intent(this, MetricsService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            
            Toast.makeText(this, "Servicio iniciado", Toast.LENGTH_SHORT).show()
            
            // Cerrar la actividad (el servicio seguirá corriendo)
            finish()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error starting service", e)
            Toast.makeText(this, "Error al iniciar servicio: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

