package com.example.kaydensdigitalassistant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.wifi.p2p.WifiP2pDevice
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.AppDatabase
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.CustomerDetailViewModel
import com.example.kaydensdigitalassistant.data.EmployeeDetail
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite
import com.example.kaydensdigitalassistant.font_archivo
import com.example.kaydensdigitalassistant.font_notosans_bold
import com.example.kaydensdigitalassistant.font_notosans_regular
import kotlinx.coroutines.launch

@Composable
fun SelectCustomer(navController: NavController){
    var isOldCustomer by remember { mutableStateOf(false) }
    var isNewCustomer by remember { mutableStateOf(false) }
    var isClosed by remember { mutableStateOf(true) }
    var showSyncDialog by remember { mutableStateOf(false) }
    val receiptViewModel = LocalReceiptViewModel.current

    val syncViewModel = LocalSyncViewModel.current

    // Observe the sync status and progress
    val syncStatus by syncViewModel.syncStatus.observeAsState(SyncStatus.IDLE)
    val syncProgress by syncViewModel.syncProgress.observeAsState(0)
    val availableDevices by syncViewModel.availableDevices.observeAsState(emptyList())
    val currentDevice by syncViewModel.currentlyConnectedDevice.observeAsState()

    Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.4f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isOldCustomer = true
                        isClosed = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(
                        text = "Old Customer",
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.1f)
                )

                Button(
                    onClick = {
                        isNewCustomer = true
                        isClosed = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(
                        text = "New Customer",
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }
            }

            FloatingActionButton(
                onClick = { showSyncDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync with other devices"
                )

                // Show a circular indicator when syncing
                if (syncStatus == SyncStatus.SYNCING) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(56.dp)
                            .padding(4.dp),
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }

            // Sync Dialog
            if (showSyncDialog) {
                SyncDialog(
                    syncViewModel = syncViewModel,
                    syncStatus = syncStatus,
                    syncProgress = syncProgress,
                    availableDevices = availableDevices,
                    currentDevice = currentDevice,
                    onDismiss = { showSyncDialog = false }
                )
            }

            if (isOldCustomer && !isClosed) {
                SelectOldCustomer(onClose = {
                    isClosed = true
                    isOldCustomer = false
                }, customerPicked = {
                    isClosed = true
                    receiptViewModel.receiptItemsState.clear()
                    navController.navigate("receipt")
                }, navController = navController)
            }

            if (isNewCustomer && !isClosed) {
                SelectNewCustomer(onClose = {
                    isClosed = true
                    isNewCustomer = false
                }, customerPicked = {
                    isClosed = true
                    receiptViewModel.receiptItemsState.clear()
                    navController.navigateWithPopUp(
                        route = "receipt",
                        popUpToRoute = "INITIALIZATION_MODE",
                        inclusive = true
                    )
                }
                )
            }
        }

}

@Composable
fun SelectOldCustomer(onClose: () -> Unit, customerPicked: (CustomerDetail) -> Unit, navController: NavController) {

    key(Unit) {
        val customerViewModel = LocalCustomerViewModel.current
        val customerDetails by customerViewModel.customerDetails.collectAsState(initial = emptyList())
        var searchQuery by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            customerViewModel.emptySearchQuery()
            searchQuery = ""
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    customerViewModel.emptySearchQuery()
                    searchQuery = ""
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        LaunchedEffect(Unit) {
            customerViewModel.resetCurrentCustomer()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
                    .clip(RoundedCornerShape(20.dp))
                    .animateContentSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.clickable { onClose() }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Old Customer",
                        fontFamily = font_archivo,
                        color = BlueEnd,
                        fontSize = 30.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            customerViewModel.setSearchQuery(it)
                        },
                        placeholder = { Text("Search customer...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(55.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = BlueStart,
                            unfocusedIndicatorColor = dirtyWhite,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort By Name",
                        modifier = Modifier.clickable {
                            customerViewModel.setSortOrder(
                                CustomerDetailViewModel.SortOrder.BY_NAME
                            )
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = kanit_bold
                    )
                    Text(
                        text = "Sort By Address",
                        modifier = Modifier.clickable {
                            customerViewModel.setSortOrder(
                                CustomerDetailViewModel.SortOrder.BY_ADDRESS
                            )
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = kanit_bold
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(0.9f)
                ) {


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .drawBehind {
                                drawRoundRect(
                                    color = dirtyWhite,
                                    size = size,
                                    style = Stroke(width = 10.dp.toPx()), // Border width
                                    cornerRadius = CornerRadius(
                                        20.dp.toPx(),
                                        20.dp.toPx()
                                    )
                                )
                            }
                            .padding(10.dp)
                    ) {
                        itemsIndexed(customerDetails) { index, customer ->
                            CustomerItem(
                                name = customer.name,
                                contact = customer.contactNumber,
                                address = customer.address,
                                customerId = customer.customerId,
                                isSelected = {
                                    customerViewModel.searchAndUpdateCustomer(customer.name)
                                    customerPicked(customer)
                                },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectNewCustomer(onClose: () -> Unit, customerPicked: () -> Unit) {
    val customerViewModel = LocalCustomerViewModel.current
    var fullName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isCustomerInserted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .animateContentSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    modifier = Modifier.clickable { onClose() }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New Customer", fontFamily = font_archivo, color = BlueEnd, fontSize = 30.sp)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Name Field
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 45.dp), horizontalArrangement = Arrangement.Start) {
                    Text(text = "FULL NAME", fontFamily = font_notosans_regular, fontSize = 15.sp, color = Color.Black)
                }
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = {
                        Text(
                            text = "Enter Full Name",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Person Icon", tint = BlueStart)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = BlueEnd,
                        unfocusedIndicatorColor = BlueEnd,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // Contact Number Field
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 45.dp), horizontalArrangement = Arrangement.Start) {
                    Text(text = "NUMBER", fontFamily = font_notosans_regular, fontSize = 15.sp)
                }
                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = {
                        Text(
                            text = "Enter Number",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Phone Icon", tint = BlueStart)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = BlueEnd,
                        unfocusedIndicatorColor = BlueEnd,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // Address Field
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 45.dp), horizontalArrangement = Arrangement.Start) {
                    Text(text = "ADDRESS", fontFamily = font_notosans_regular, fontSize = 15.sp)
                }
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = {
                        Text(
                            text = "Enter Address",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = "Home Icon", tint = BlueStart)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = BlueEnd,
                        unfocusedIndicatorColor = BlueEnd,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }

            Button(
                onClick = {
                    when {
                        fullName.isBlank() || contactNumber.isBlank() || address.isBlank() -> {
                            // Handle validation
                        }
                        else -> {
                            val newCustomer = CustomerDetail(
                                customerId = 0,  // This will be replaced with the generated ID
                                name = fullName.trim(),
                                address = address.trim(),
                                contactNumber = contactNumber.trim()
                            )

                            customerViewModel.insertCustomer(newCustomer) { insertedCustomer ->
                                // Now insertedCustomer has the correct customerId from the database
                                customerViewModel.setCurrentCustomer(insertedCustomer)
                                isCustomerInserted = true
                                customerViewModel.isNewCustomer.value = true
                                println("Is New Customer: ${customerViewModel.isNewCustomer.value}")
                                customerPicked()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueEnd),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "CONFIRM",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}


@Composable
fun CustomerItem(
    name: String,
    contact: String,
    address: String,
    customerId: Long,
    isSelected: () -> Unit,
    navController: NavController
) {
    val customerDetail = LocalCustomerViewModel.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 10.dp)
            .clickable {
                isSelected()
                customerDetail.setCurrentCustomer(
                    CustomerDetail(
                        customerId = customerId,
                        name = name,
                        address = address,
                        contactNumber = contact
                    )
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.face_man),
                contentDescription = "Icon",
                modifier = Modifier.size(35.dp)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                var detailText = buildAnnotatedString {
                    withStyle(SpanStyle(fontSize = 15.sp, fontFamily = font_notosans_bold)) {
                        append("$name\n")
                    }
                    withStyle(SpanStyle(fontSize = 10.sp, fontFamily = font_notosans_regular)) {
                        append("$contact\n")
                        append(address)
                    }
                }
                Text(
                    text = detailText,
                    color = Color.Black,
                    style = TextStyle(lineHeight = 13.sp),
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }

        IconButton(
            onClick = { navController.navigate("maps/$customerId") }
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "View Location",
                tint = BlueEnd
            )
        }
    }
}

@Composable
fun SyncDialog(
    syncViewModel: SyncViewModel,
    syncStatus: SyncStatus,
    syncProgress: Int,
    availableDevices: List<WifiP2pDevice>,
    currentDevice: WifiP2pDevice?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sync with other devices") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Last sync info
                Text(
                    text = "Last sync: ${syncViewModel.getFormattedLastSyncTime()}",
                    style = MaterialTheme.typography.bodySmall
                )

                // Current status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Status: ${syncStatus.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (syncStatus == SyncStatus.SYNCING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Progress bar for syncing
                if (syncStatus == SyncStatus.SYNCING) {
                    Column {
                        Text(
                            text = "Progress: $syncProgress%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        LinearProgressIndicator(
                            progress = { syncProgress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Connected device
                if (currentDevice != null) {
                    Text(
                        text = "Connected to: ${currentDevice.deviceName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Available devices list
                if (availableDevices.isNotEmpty()) {
                    Text(
                        text = "Available devices:",
                        style = MaterialTheme.typography.titleSmall
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        items(availableDevices) { device ->
                            DeviceItem(
                                device = device,
                                isConnected = device.deviceAddress == currentDevice?.deviceAddress,
                                onClick = { syncViewModel.connectToDevice(device) }
                            )
                        }
                    }
                } else if (syncStatus == SyncStatus.DISCOVERING) {
                    Text(
                        text = "Searching for devices...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Text(
                        text = "No devices found. Tap 'Discover Devices' to search.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { syncViewModel.discoverDevices() },
                    enabled = syncStatus != SyncStatus.SYNCING
                ) {
                    Text(if (syncStatus == SyncStatus.DISCOVERING) "Restart Discovery" else "Discover Devices")
                }

                Button(
                    onClick = { syncViewModel.forceSyncNow() },
                    enabled = syncStatus == SyncStatus.CONNECTED && currentDevice != null
                ) {
                    Text("Sync Now")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DeviceItem(
    device: WifiP2pDevice,
    isConnected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isConnected, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = device.deviceName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isConnected) FontWeight.Bold else FontWeight.Normal
            )

            Text(
                text = device.deviceAddress,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isConnected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Connected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}