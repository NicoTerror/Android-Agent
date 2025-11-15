package com.androidagent.core

import android.util.Log
import com.androidagent.BuildConfig
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MetricsWsClient(private val deviceId: String) {
    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null
    private var httpClient: OkHttpClient? = null
    private var reconnectJob: Job? = null
    private var urlDiscoveryJob: Job? = null
    private var reconnectDelay = 1000L // Inicial: 1 segundo
    private val maxReconnectDelay = 60000L // Máximo: 60 segundos
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // URL actual del WebSocket (se actualiza desde el endpoint de descubrimiento)
    private var currentWsUrl: String = BuildConfig.WS_URL
    private var lastDiscoveredUrl: String? = null

    init {
        // Iniciar descubrimiento periódico de URL
        startUrlDiscovery()
    }

    private fun startUrlDiscovery() {
        urlDiscoveryJob?.cancel()
        urlDiscoveryJob = scope.launch {
            while (isActive) {
                try {
                    discoverServerUrl()
                } catch (e: Exception) {
                    Log.w(TAG, "Error en descubrimiento de URL: ${e.message}")
                }
                delay(30000) // Consultar cada 30 segundos
            }
        }
    }

    private suspend fun discoverServerUrl() {
        try {
            val baseUrl = BuildConfig.BASE_URL.removeSuffix("/")
            val discoveryUrl = "$baseUrl/server-url"
            
            Log.d(TAG, "Consultando endpoint de descubrimiento: $discoveryUrl")
            
            val request = Request.Builder()
                .url(discoveryUrl)
                .get()
                .build()
            
            val client = httpClient ?: OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build().also { httpClient = it }
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) {
                    val json = JSONObject(body)
                    val discoveredWsUrl = json.optString("ws_url", "")
                    
                    if (discoveredWsUrl.isNotEmpty() && discoveredWsUrl != lastDiscoveredUrl) {
                        Log.d(TAG, "Nueva URL descubierta: $discoveredWsUrl (anterior: $lastDiscoveredUrl)")
                        lastDiscoveredUrl = discoveredWsUrl
                        currentWsUrl = discoveredWsUrl
                        
                        // Si hay una conexión activa y la URL cambió, reconectar
                        if (webSocket != null) {
                            Log.d(TAG, "URL cambió, reconectando...")
                            // Cerrar solo el WebSocket sin cancelar el scope
                            webSocket?.close(1000, "URL changed")
                            webSocket = null
                            delay(1000)
                            connect()
                        }
                    }
                }
            } else {
                Log.w(TAG, "Endpoint de descubrimiento retornó código: ${response.code}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo consultar endpoint de descubrimiento, usando URL de fallback: ${e.message}")
            // Usar URL de fallback si el descubrimiento falla
            currentWsUrl = BuildConfig.WS_URL
        }
    }

    fun connect() {
        if (webSocket != null) {
            return // Ya conectado
        }

        // Intentar descubrir URL antes de conectar (solo la primera vez)
        if (lastDiscoveredUrl == null) {
            runBlocking {
                discoverServerUrl()
            }
        }

        val url = "$currentWsUrl?mode=agent&api_key=${BuildConfig.API_KEY}&device_id=$deviceId"
        Log.d(TAG, "Connecting to: $url")

        client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client?.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                reconnectDelay = 1000L // Reset delay on successful connection
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code $reason")
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code $reason")
                this@MetricsWsClient.webSocket = null
                scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure", t)
                this@MetricsWsClient.webSocket = null
                scheduleReconnect()
            }
        }
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            delay(reconnectDelay)
            Log.d(TAG, "Attempting reconnect (delay: ${reconnectDelay}ms)")
            connect()
            // Backoff exponencial
            reconnectDelay = minOf(reconnectDelay * 2, maxReconnectDelay)
        }
    }

    fun sendMetrics(metrics: Metrics) {
        val webSocket = this.webSocket ?: run {
            Log.w(TAG, "WebSocket not connected, skipping send")
            return
        }

        try {
            val json = JSONObject().apply {
                put("type", "metrics")
                put("deviceId", deviceId)
                put("ts", System.currentTimeMillis())
                put("screenOn", metrics.screenOn)
                put("volume", JSONObject().apply {
                    put("level", metrics.volume.level)
                    put("max", metrics.volume.max)
                    put("percent", metrics.volume.percent)
                })
                put("foregroundApp", metrics.foregroundApp)
                // Incluir screenBlocked (puede ser null si no hay sensor)
                if (metrics.screenBlocked != null) {
                    put("screenBlocked", metrics.screenBlocked)
                }
            }

            val sent = webSocket.send(json.toString())
            if (!sent) {
                Log.w(TAG, "Failed to send metrics")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending metrics", e)
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        urlDiscoveryJob?.cancel()
        webSocket?.close(1000, "Service stopping")
        client?.dispatcher?.executorService?.shutdown()
        httpClient?.dispatcher?.executorService?.shutdown()
        scope.cancel()
    }

    companion object {
        private const val TAG = "MetricsWsClient"
    }
}

