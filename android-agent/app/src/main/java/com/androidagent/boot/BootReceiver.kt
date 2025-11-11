package com.androidagent.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.androidagent.service.MetricsService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, starting MetricsService")
            val serviceIntent = Intent(context, MetricsService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}

