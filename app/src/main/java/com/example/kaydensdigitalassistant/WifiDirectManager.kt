package com.example.kaydensdigitalassistant

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kaydensdigitalassistant.data.AppDatabase
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.CustomerLocation
import com.example.kaydensdigitalassistant.data.EmployeeDetail
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.data.SalesItem
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.util.UUID

class WifiDirectManager(private val context: Context) {

    companion object {
        private const val TAG = "WifiDirectManager"
        private const val PORT = 8888
    }

    private val manager: WifiP2pManager? = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    private var channel: WifiP2pManager.Channel? = manager?.initialize(context, context.mainLooper, null)

    private val deviceId = UUID.randomUUID().toString()

    // LiveData for UI updates
    private val _availablePeers = MutableLiveData<List<WifiP2pDevice>>()
    val availablePeers: LiveData<List<WifiP2pDevice>> = _availablePeers

    private val _connectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String> = _connectionStatus

    private val _syncProgress = MutableLiveData<Int>()
    val syncProgress: LiveData<Int> = _syncProgress

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        _connectionStatus.postValue("WiFi P2P is enabled")
                    } else {
                        _connectionStatus.postValue("WiFi P2P is disabled")
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    Log.d(TAG, "Peers changed action received")
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                        == PackageManager.PERMISSION_GRANTED) {
                        manager?.requestPeers(channel) { peers: WifiP2pDeviceList ->
                            val deviceList = peers.deviceList.toList()
                            Log.d(TAG, "Found ${deviceList.size} peers")
                            _availablePeers.postValue(deviceList)

                            // Update connection status if we found devices
                            if (deviceList.isNotEmpty()) {
                                _connectionStatus.postValue("Found ${deviceList.size} devices")
                            }
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    Log.d(TAG, "Connection changed action received")
                    val networkInfo = intent.getParcelableExtra<android.net.NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)

                    if (networkInfo?.isConnected == true) {
                        manager?.requestConnectionInfo(channel) { info: WifiP2pInfo ->
                            if (info.groupFormed) {
                                if (info.isGroupOwner) {
                                    _connectionStatus.postValue("Connected as group owner")
                                    // Start server socket
                                    CoroutineScope(Dispatchers.IO).launch {
                                        startServerSocket()
                                    }
                                } else {
                                    _connectionStatus.postValue("Connected as client to ${info.groupOwnerAddress}")
                                    // Start client socket
                                    CoroutineScope(Dispatchers.IO).launch {
                                        startClientSocket(info.groupOwnerAddress.hostAddress)
                                    }
                                }
                            }
                        }
                    } else {
                        _connectionStatus.postValue("Disconnected")
                    }
                }
            }
        }
    }

    // Database access
    private lateinit var appDatabase: AppDatabase

    fun initialize(database: AppDatabase) {
        appDatabase = database
        context.registerReceiver(receiver, intentFilter)
    }

    fun cleanup() {
        context.unregisterReceiver(receiver)
    }

    fun restartDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                != PackageManager.PERMISSION_GRANTED) {
                _connectionStatus.postValue("Permission required: NEARBY_WIFI_DEVICES")
                return
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                _connectionStatus.postValue("Permission required: ACCESS_FINE_LOCATION")
                return
            }
        }

        // Instead of cancelDiscovery (which doesn't exist), we'll use stopPeerDiscovery
        manager?.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Successfully stopped previous discovery")
                // Start new discovery after a short delay
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    discoverPeers()
                }, 1000)
            }

            override fun onFailure(reason: Int) {
                Log.e(TAG, "Failed to stop discovery: $reason")
                // Try to start discovery anyway
                discoverPeers()
            }
        })
    }

    fun discoverPeers() {
        // Check permissions first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                != PackageManager.PERMISSION_GRANTED) {
                _connectionStatus.postValue("Permission required: NEARBY_WIFI_DEVICES")
                return
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                _connectionStatus.postValue("Permission required: ACCESS_FINE_LOCATION")
                return
            }
        }

        // Clear previous peer list before starting new discovery
        _availablePeers.postValue(emptyList())

        // Add timeout handling for discovery
        val discoveryTimeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
        val discoveryTimeoutRunnable = Runnable {
            if (_connectionStatus.value?.contains("Discovery started") == true) {
                // If we're still in discovery after timeout, update status
                _connectionStatus.postValue("Discovery timed out, retrying...")
                // Try again
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                    == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    manager?.stopPeerDiscovery(channel, null)
                    startDiscovery()
                }
            }
        }

        // Start discovery with timeout
        startDiscovery()
        // Set 30-second timeout
        discoveryTimeoutHandler.postDelayed(discoveryTimeoutRunnable, 30000)
    }

    private fun startDiscovery() {
        // Check permissions before starting discovery
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                != PackageManager.PERMISSION_GRANTED) {
                _connectionStatus.postValue("Permission required: NEARBY_WIFI_DEVICES")
                return
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                _connectionStatus.postValue("Permission required: ACCESS_FINE_LOCATION")
                return
            }
        }

        // Now that we've checked permissions, we can safely call discoverPeers
        try {
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _connectionStatus.postValue("Discovery started")
                }

                override fun onFailure(reason: Int) {
                    val errorMsg = when(reason) {
                        WifiP2pManager.P2P_UNSUPPORTED -> "WiFi Direct is not supported on this device"
                        WifiP2pManager.BUSY -> "System is busy, try again later"
                        WifiP2pManager.ERROR -> "Internal error occurred"
                        else -> "Discovery failed: $reason"
                    }
                    _connectionStatus.postValue(errorMsg)
                }
            })
        } catch (e: SecurityException) {
            // Handle the case where permission might be revoked at runtime
            _connectionStatus.postValue("Permission denied: ${e.message}")
            Log.e(TAG, "Security exception during discovery: ${e.message}")
        }
    }

    fun connectToPeer(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        manager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                _connectionStatus.postValue("Connection initiated")
            }

            override fun onFailure(reason: Int) {
                _connectionStatus.postValue("Connection failed: $reason")
            }
        })
    }

    private suspend fun startServerSocket() {
        withContext(Dispatchers.IO) {
            try {
                val serverSocket = ServerSocket(PORT)
                _connectionStatus.postValue("Server socket open on port $PORT")

                while (true) {
                    // Accept incoming connections
                    val client = serverSocket.accept()
                    _connectionStatus.postValue("Client connected: ${client.inetAddress}")

                    // Handle the client connection in a new coroutine
                    CoroutineScope(Dispatchers.IO).launch {
                        handleClientConnection(client)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Server socket error: ${e.message}")
                _connectionStatus.postValue("Server error: ${e.message}")
            }
        }
    }

    private suspend fun startClientSocket(hostAddress: String) {
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(hostAddress, PORT), 5000)
                _connectionStatus.postValue("Connected to server")

                // Start sync process
                syncDataToServer(socket)

                // Receive updates from server
                receiveUpdatesFromServer(socket)
            } catch (e: IOException) {
                Log.e(TAG, "Client socket error: ${e.message}")
                _connectionStatus.postValue("Client error: ${e.message}")
            }
        }
    }

    private suspend fun handleClientConnection(client: Socket) {
        try {
            val input = ObjectInputStream(client.getInputStream())
            val output = ObjectOutputStream(client.getOutputStream())

            // Identify what kind of sync request this is
            val requestType = input.readUTF()

            when (requestType) {
                "FULL_SYNC" -> {
                    // Handle full database sync
                    handleFullSync(input, output)
                }
                "INCREMENTAL_SYNC" -> {
                    // Handle incremental sync
                    val lastSyncTime = input.readLong()
                    handleIncrementalSync(lastSyncTime, input, output)
                }
            }

            client.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error handling client: ${e.message}")
        }
    }

    private suspend fun handleFullSync(input: ObjectInputStream, output: ObjectOutputStream) {
        // Send all data to client
        sendDatabaseState(output)

        // Receive all data from client and merge
        receiveDatabaseState(input)
    }

    private suspend fun handleIncrementalSync(lastSyncTime: Long, input: ObjectInputStream, output: ObjectOutputStream) {
        // Send only changes since lastSyncTime
        sendIncrementalChanges(lastSyncTime, output)

        // Receive changes from client and merge
        receiveIncrementalChanges(input)
    }

    private suspend fun syncDataToServer(socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val output = ObjectOutputStream(socket.getOutputStream())
                val input = ObjectInputStream(socket.getInputStream())

                // Request a full sync
                output.writeUTF("FULL_SYNC")
                output.flush()

                // Send our database state
                sendDatabaseState(output)

                // Receive server's database state
                receiveDatabaseState(input)

                output.close()
                input.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error during sync: ${e.message}")
            }
        }
    }

    private suspend fun receiveUpdatesFromServer(socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val input = ObjectInputStream(socket.getInputStream())

                while (socket.isConnected) {
                    // Wait for updates
                    val updateType = input.readUTF()

                    when (updateType) {
                        "CUSTOMER_UPDATE" -> {
                            val customer = Gson().fromJson(input.readUTF(), CustomerDetail::class.java)
                            // Update local database
                            appDatabase.customerDetailDao().insertCustomer(customer)
                        }
                        "EMPLOYEE_UPDATE" -> {
                            val employee = Gson().fromJson(input.readUTF(), EmployeeDetail::class.java)
                            appDatabase.employeeDetailDao().insertEmployee(employee)
                        }
                        // Handle other update types
                    }
                }

                input.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving updates: ${e.message}")
            }
        }
    }

    private suspend fun sendDatabaseState(output: ObjectOutputStream) {
        withContext(Dispatchers.IO) {
            try {
                // Collect customers
                val customers = appDatabase.customerDetailDao().getAllCustomers().first()
                output.writeInt(customers.size)
                customers.forEach { customer ->
                    output.writeUTF(Gson().toJson(customer))
                }
                output.flush()
                _syncProgress.postValue(20)

                // Collect customer locations
                val locations = appDatabase.customerLocationDao().getAllCustomerLocations().first()
                output.writeInt(locations.size)
                locations.forEach { location ->
                    output.writeUTF(Gson().toJson(location))
                }
                output.flush()
                _syncProgress.postValue(40)

                // Collect employees
                val employees = appDatabase.employeeDetailDao().getAllEmployees().first()
                output.writeInt(employees.size)
                employees.forEach { employee ->
                    output.writeUTF(Gson().toJson(employee))
                }
                output.flush()
                _syncProgress.postValue(60)

                // Collect products
                val products = appDatabase.productsDao().getAllProducts().first()
                output.writeInt(products.size)
                products.forEach { product ->
                    output.writeUTF(Gson().toJson(product))
                }
                output.flush()
                _syncProgress.postValue(80)

                // Collect sales items
                val sales = appDatabase.salesItemDao().getAllSalesItems().first()
                output.writeInt(sales.size)
                sales.forEach { sale ->
                    output.writeUTF(Gson().toJson(sale))
                }
                output.flush()
                _syncProgress.postValue(100)

            } catch (e: IOException) {
                Log.e(TAG, "Error sending database state: ${e.message}")
            }
        }
    }


    private suspend fun receiveDatabaseState(input: ObjectInputStream) {
        withContext(Dispatchers.IO) {
            try {
                // Receive and merge customers
                val customerCount = input.readInt()
                for (i in 0 until customerCount) {
                    val customerJson = input.readUTF()
                    val customer = Gson().fromJson(customerJson, CustomerDetail::class.java)
                    mergeCustomer(customer)
                }
                _syncProgress.postValue(20)

                // Receive and merge customer locations
                val locationCount = input.readInt()
                for (i in 0 until locationCount) {
                    val locationJson = input.readUTF()
                    val location = Gson().fromJson(locationJson, CustomerLocation::class.java)
                    mergeCustomerLocation(location)
                }
                _syncProgress.postValue(40)

                // Receive and merge employees
                val employeeCount = input.readInt()
                for (i in 0 until employeeCount) {
                    val employeeJson = input.readUTF()
                    val employee = Gson().fromJson(employeeJson, EmployeeDetail::class.java)
                    mergeEmployee(employee)
                }
                _syncProgress.postValue(60)

                // Receive and merge products
                val productCount = input.readInt()
                for (i in 0 until productCount) {
                    val productJson = input.readUTF()
                    val product = Gson().fromJson(productJson, Products::class.java)
                    mergeProduct(product)
                }
                _syncProgress.postValue(80)

                // Receive and merge sales
                val salesCount = input.readInt()
                for (i in 0 until salesCount) {
                    val saleJson = input.readUTF()
                    val sale = Gson().fromJson(saleJson, SalesItem::class.java)
                    mergeSalesItem(sale)
                }
                _syncProgress.postValue(100)

                _connectionStatus.postValue("Sync completed")

            } catch (e: IOException) {
                Log.e(TAG, "Error receiving database state: ${e.message}")
            }
        }
    }

    private suspend fun sendIncrementalChanges(lastSyncTime: Long, output: ObjectOutputStream) {
        withContext(Dispatchers.IO) {
            // Similar to sendDatabaseState but only send entries modified after lastSyncTime
            // Implementation will depend on your DAO having methods to get changed records
        }
    }

    private suspend fun receiveIncrementalChanges(input: ObjectInputStream) {
        withContext(Dispatchers.IO) {
            // Similar to receiveDatabaseState but for incremental changes
        }
    }

    // Merge strategies for different entity types
    private suspend fun mergeCustomer(remoteCustomer: CustomerDetail) {
        val localCustomer = appDatabase.customerDetailDao().getCustomerById(remoteCustomer.customerId)
        if (localCustomer == null) {
            // New customer, just insert
            appDatabase.customerDetailDao().insertCustomer(remoteCustomer)
        } else {
            // Customer exists, use the newer version
            // This assumes you've added a lastModified timestamp to CustomerDetail
            if ((remoteCustomer as SyncableEntity).lastModified > (localCustomer as SyncableEntity).lastModified) {
                appDatabase.customerDetailDao().updateCustomer(remoteCustomer)
            }
        }
    }

    private suspend fun mergeCustomerLocation(remoteLocation: CustomerLocation) {
        val localLocation = appDatabase.customerLocationDao().getLocationByCustomerId(remoteLocation.locationId)
        if (localLocation == null) {
            appDatabase.customerLocationDao().insertLocation(remoteLocation)
        } else {
            // Use the newer version
            if ((remoteLocation as SyncableEntity).lastModified > (localLocation as SyncableEntity).lastModified) {
                appDatabase.customerLocationDao().updateLocation(remoteLocation)
            }
        }
    }

    private suspend fun mergeEmployee(remoteEmployee: EmployeeDetail) {
        val localEmployee = appDatabase.employeeDetailDao().getEmployeeById(remoteEmployee.employeeId)
        if (localEmployee == null) {
            appDatabase.employeeDetailDao().insertEmployee(remoteEmployee)
        } else {
            if ((remoteEmployee as SyncableEntity).lastModified > (localEmployee as SyncableEntity).lastModified) {
                appDatabase.employeeDetailDao().updateEmployee(remoteEmployee)
            }
        }
    }

    private suspend fun mergeProduct(remoteProduct: Products) {
        val localProduct = appDatabase.productsDao().getProductById(remoteProduct.productId)
        if (localProduct == null) {
            appDatabase.productsDao().insertProduct(remoteProduct)
        } else {
            if ((remoteProduct as SyncableEntity).lastModified > (localProduct as SyncableEntity).lastModified) {
                appDatabase.productsDao().updateProduct(remoteProduct)
            }
        }
    }

    private suspend fun mergeSalesItem(remoteSale: SalesItem) {
        val localSale = appDatabase.salesItemDao().getSalesById(remoteSale.salesId)
        if (localSale == null) {
            appDatabase.salesItemDao().insertSalesItem(remoteSale)
        } else {
            if ((remoteSale as SyncableEntity).lastModified > (localSale as SyncableEntity).lastModified) {
                appDatabase.salesItemDao().updateSales(remoteSale)
            }
        }
    }
}

// Interface for synchable entities
interface SyncableEntity {
    val lastModified: Long
    val isDeleted: Boolean
    val deviceId: String
}