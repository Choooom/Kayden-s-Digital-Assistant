package com.example.kaydensdigitalassistant

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.p2p.WifiP2pDevice
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kaydensdigitalassistant.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class SyncViewModel(application: Application) : AndroidViewModel(application) {

    private val _syncStatus = MutableLiveData<SyncStatus>()
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    private val _availableDevices = MutableLiveData<List<WifiP2pDevice>>()
    val availableDevices: LiveData<List<WifiP2pDevice>> = _availableDevices

    private val _syncProgress = MutableLiveData<Int>()
    val syncProgress: LiveData<Int> = _syncProgress

    private val _lastSyncTime = MutableLiveData<Long>()
    val lastSyncTime: LiveData<Long> = _lastSyncTime

    private val _currentlyConnectedDevice = MutableLiveData<WifiP2pDevice?>()
    val currentlyConnectedDevice: LiveData<WifiP2pDevice?> = _currentlyConnectedDevice

    private val wifiDirectManager: WifiDirectManager by lazy {
        WifiDirectManager(application.applicationContext).apply {
            initialize(AppDatabase.getInstance(application.applicationContext))
        }
    }

    private val conflictResolver: ConflictResolver by lazy {
        ConflictResolver(AppDatabase.getInstance(application.applicationContext))
    }

    private var syncServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            syncServiceBound = true
            _syncStatus.postValue(SyncStatus.SERVICE_CONNECTED)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            syncServiceBound = false
            _syncStatus.postValue(SyncStatus.SERVICE_DISCONNECTED)
        }
    }

    init {
        _syncStatus.value = SyncStatus.IDLE
        _syncProgress.value = 0
        loadLastSyncTime()

        // Observe LiveData from WiFiDirectManager
        wifiDirectManager.availablePeers.observeForever { devices ->
            _availableDevices.postValue(devices)
        }

        wifiDirectManager.connectionStatus.observeForever { status ->
            when {
                status.contains("Connected as") -> {
                    _syncStatus.postValue(SyncStatus.CONNECTED)
                }
                status.contains("Disconnected") -> {
                    _syncStatus.postValue(SyncStatus.DISCONNECTED)
                    _currentlyConnectedDevice.postValue(null)
                }
                status.contains("Discovery started") -> {
                    _syncStatus.postValue(SyncStatus.DISCOVERING)
                }
                status.contains("Sync completed") -> {
                    _syncStatus.postValue(SyncStatus.SYNC_COMPLETE)
                    saveLastSyncTime(System.currentTimeMillis())
                }
                status.contains("Sync failed") -> {
                    _syncStatus.postValue(SyncStatus.SYNC_FAILED)
                }
            }
        }

        wifiDirectManager.syncProgress.observeForever { progress ->
            _syncProgress.postValue(progress)
        }
    }

    /**
     * Start the background sync service
     */
    fun startSyncService() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, SyncService::class.java)

        context.startForegroundService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        _syncStatus.value = SyncStatus.SERVICE_STARTING
    }

    /**
     * Stop the background sync service
     */
    fun stopSyncService() {
        val context = getApplication<Application>().applicationContext
        if (syncServiceBound) {
            context.unbindService(serviceConnection)
            syncServiceBound = false
        }

        context.stopService(Intent(context, SyncService::class.java))
        _syncStatus.value = SyncStatus.SERVICE_STOPPED
    }

    /**
     * Start discovery of nearby devices
     */

    /**
     * Connect to a specific device
     */
    fun connectToDevice(device: WifiP2pDevice) {
        _syncStatus.value = SyncStatus.CONNECTING
        _currentlyConnectedDevice.value = device
        wifiDirectManager.connectToPeer(device)
    }

    /**
     * Force immediate synchronization with currently connected device
     */
    fun forceSyncNow() {
        if (_syncStatus.value == SyncStatus.CONNECTED) {
            viewModelScope.launch {
                _syncStatus.postValue(SyncStatus.SYNCING)
                _syncProgress.postValue(0)

                // The actual sync is handled by WifiDirectManager when connected
                // This would be triggered by the connection state change

                // Update last sync time on successful completion
                // This will be handled by the WifiDirectManager observer
            }
        } else {
            _syncStatus.postValue(SyncStatus.ERROR)
        }
    }

    /**
     * Process a batch of remote data, resolving conflicts
     */
    fun processBatchSync(remoteData: List<SyncableEntity>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                conflictResolver.processBatchSync(remoteData)
            }
            _syncStatus.postValue(SyncStatus.SYNC_COMPLETE)
            saveLastSyncTime(System.currentTimeMillis())
        }
    }

    /**
     * Load the last sync time from shared preferences
     */
    private fun loadLastSyncTime() {
        val prefs = getApplication<Application>().getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        val lastSync = prefs.getLong("last_sync_time", 0L)
        _lastSyncTime.value = lastSync
    }

    /**
     * Save the last sync time to shared preferences
     */
    private fun saveLastSyncTime(time: Long) {
        _lastSyncTime.value = time
        val prefs = getApplication<Application>().getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("last_sync_time", time).apply()
    }

    /**
     * Get a formatted string of the last sync time
     */
    fun getFormattedLastSyncTime(): String {
        val time = _lastSyncTime.value ?: 0L
        return if (time > 0) {
            Date(time).toString()
        } else {
            "Never"
        }
    }

    /**
     * Check if a sync is needed based on time elapsed
     */
    fun isSyncNeeded(intervalHours: Int = 1): Boolean {
        val lastSync = _lastSyncTime.value ?: 0L
        val currentTime = System.currentTimeMillis()
        val interval = intervalHours * 60 * 60 * 1000L

        return currentTime - lastSync > interval
    }

    fun restartDiscovery() {
        wifiDirectManager.restartDiscovery()
        _syncStatus.value = SyncStatus.DISCOVERING
    }

    // Modify the discoverDevices method
    fun discoverDevices() {
        // First check if we're already discovering
        if (_syncStatus.value == SyncStatus.DISCOVERING) {
            // If already discovering, restart the process
            restartDiscovery()
        } else {
            wifiDirectManager.discoverPeers()
            _syncStatus.value = SyncStatus.DISCOVERING

            // Add a timeout to automatically retry if discovery gets stuck
            viewModelScope.launch {
                delay(45000) // 45 seconds timeout
                if (_syncStatus.value == SyncStatus.DISCOVERING && _availableDevices.value?.isEmpty() == true) {
                    _syncStatus.postValue(SyncStatus.IDLE)
                    restartDiscovery()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Clean up observers
        wifiDirectManager.availablePeers.removeObserver {}
        wifiDirectManager.connectionStatus.removeObserver {}
        wifiDirectManager.syncProgress.removeObserver {}

        // Clean up service connection
        if (syncServiceBound) {
            getApplication<Application>().unbindService(serviceConnection)
            syncServiceBound = false
        }

        // Clean up WiFi Direct manager
        wifiDirectManager.cleanup()
    }
}

/**
 * Enum representing the current state of the sync process
 */
enum class SyncStatus {
    IDLE,
    SERVICE_STARTING,
    SERVICE_CONNECTED,
    SERVICE_DISCONNECTED,
    SERVICE_STOPPED,
    DISCOVERING,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    SYNCING,
    SYNC_COMPLETE,
    SYNC_FAILED,
    ERROR
}
