package com.example.kaydensdigitalassistant

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asFlow
import com.example.kaydensdigitalassistant.data.AppDatabase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.collect

class SyncService : Service() {
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "SyncServiceChannel"
        private const val SYNC_INTERVAL = 15 * 60 * 1000L // 15 minutes
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var syncJob: Job? = null
    private lateinit var wifiDirectManager: WifiDirectManager
    private lateinit var appDatabase: AppDatabase
    private lateinit var deviceId: String
    private var lastSyncTime: Long = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Initialize database
        appDatabase = AppDatabase.getInstance(applicationContext)

        // Initialize WiFi Direct Manager
        wifiDirectManager = WifiDirectManager(applicationContext)
        wifiDirectManager.initialize(appDatabase)

        // Generate or retrieve device ID
        val sharedPrefs = getSharedPreferences("sync_prefs", MODE_PRIVATE)
        deviceId = sharedPrefs.getString("device_id", "") ?: ""
        if (deviceId.isEmpty()) {
            deviceId = java.util.UUID.randomUUID().toString()
            sharedPrefs.edit().putString("device_id", deviceId).apply()
        }

        lastSyncTime = sharedPrefs.getLong("last_sync_time", 0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Syncing Data")
            .setContentText("Syncing data with nearby devices")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        startSyncJob()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        syncJob?.cancel()
        wifiDirectManager.cleanup()

        // Save last sync time
        getSharedPreferences("sync_prefs", MODE_PRIVATE)
            .edit()
            .putLong("last_sync_time", lastSyncTime)
            .apply()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sync Service Channel"
            val descriptionText = "Channel for Sync Service notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startSyncJob() {
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            while (true) {
                try {
                    // Discover peers and attempt sync
                    wifiDirectManager.discoverPeers()

                    // Update notification to show sync in progress
                    updateNotification("Syncing in progress...")

                    // Wait for sync to complete
                    wifiDirectManager.syncProgress.asFlow().collect { progress ->
                        updateNotification("Syncing: $progress%")

                        if (progress == 100) {
                            // Sync completed
                            lastSyncTime = System.currentTimeMillis()
                            updateNotification("Sync completed")
                        }
                    }

                } catch (e: Exception) {
                    updateNotification("Sync failed: ${e.message}")
                }

                // Wait for next sync interval
                delay(SYNC_INTERVAL)
            }
        }
    }

    private fun updateNotification(status: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Data Sync")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}