package com.androidagent.core

import android.content.Context
import android.content.SharedPreferences

object DeviceIdManager {
    private const val PREFS_NAME = "device_prefs"
    private const val KEY_DEVICE_ID = "custom_device_id"
    
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEVICE_ID, null) ?: ""
    }
    
    fun saveDeviceId(context: Context, deviceId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }
    
    fun hasDeviceId(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.contains(KEY_DEVICE_ID) && !prefs.getString(KEY_DEVICE_ID, null).isNullOrEmpty()
    }
}






