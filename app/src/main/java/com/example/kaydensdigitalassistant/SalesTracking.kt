package com.example.kaydensdigitalassistant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.ReceiptItem
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.bookmark
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite
import com.example.kaydensdigitalassistant.font_abeezee
import com.example.kaydensdigitalassistant.font_archivo
import com.example.kaydensdigitalassistant.font_notosans_bold
import com.example.kaydensdigitalassistant.font_notosans_regular
import com.example.kaydensdigitalassistant.kanit_bold

@Composable
fun SalesTracking(navController: NavController) {
    val salesViewModel = LocalSalesViewModel.current
    val customerViewModel = LocalCustomerViewModel.current
    val salesList by salesViewModel.selectedSalesItems.observeAsState(emptyList())
    println("SalesList: $salesList")
    val insets = WindowInsets.systemBars.asPaddingValues()
    val customerDetailsMap = remember { mutableStateMapOf<Long, CustomerDetail?>() }

    var isReceiptExpanded by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf<Long?>(null) }
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(salesList.firstOrNull()?.dateDelivered ?: "Today") }

    var isFirstLoad by remember { mutableStateOf(true) }

    LaunchedEffect(isFirstLoad) {
        val todayDate = getCurrentDate() // Get today's date
        selectedDate = "Sales Today" // Set the default selected date text
        salesViewModel.filterSalesByDate(todayDate) // Filter for today's sales
        isFirstLoad = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(Brush.horizontalGradient(colors = listOf(BlueStart, BlueEnd))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header section remains the same
        TopBar(navController = navController, "SALES")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.992f)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .drawBehind {
                        val borderSize = 2.dp.toPx()
                        drawLine(
                            color = dirtyWhite,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = borderSize
                        )
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "RECEIPT")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedDate ⌄",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable { isDropdownExpanded = true }
                    )

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        val allSalesList by salesViewModel.allSalesItems.observeAsState(emptyList())

                        // Extract unique dates and sort them in descending order
                        val uniqueDates = allSalesList.map { it.dateDelivered }
                            .distinct()
                            .sortedDescending()

                        // Add "All Sales" as the first dropdown option
                        DropdownMenuItem(
                            onClick = {
                                selectedDate = "All Sales"
                                salesViewModel.filterSalesByDate(null) // Show all sales when "All Sales" is clicked
                                isDropdownExpanded = false
                            },
                            text = { Text("All Sales") }
                        )

                        // Generate a dropdown item for each unique date
                        uniqueDates.forEach { date ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedDate = if (date == getCurrentDate()) "Sales Today" else date
                                    salesViewModel.filterSalesByDate(date) // Trigger filtering by the selected date
                                    isDropdownExpanded = false
                                },
                                text = { Text(if (date == getCurrentDate()) "Sales Today" else date) }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.99f)
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(dirtyWhite)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(salesList) { index, salesItem ->
                        LaunchedEffect(salesItem.salesId) {
                            if (!customerDetailsMap.containsKey(salesItem.salesId)) {
                                val customerDetails = customerViewModel.fetchCustomerById(salesItem.customerId)
                                customerDetailsMap[salesItem.salesId] = customerDetails
                            }
                        }
                        Spacer(modifier = Modifier.fillMaxWidth().height(if (index == 0) 20.dp else 10.dp))
                        val customerDetails = customerDetailsMap[salesItem.salesId]
                        SalesItem(
                            navController = navController,
                            name = customerDetails?.name ?: "Unknown Customer",
                            address = customerDetails?.address ?: "Unknown Address",
                            orderNumber = salesItem.salesId,
                            date = salesItem.timeDelivered,
                            orderBreakdown = salesItem.orderDetails
                        ) {
                            selectedOrderId = salesItem.salesId
                            selectedCustomerId = salesItem.customerId
                            println("Selected Order ID: $selectedOrderId")
                            isReceiptExpanded = true
                        }
                    }
                }
            }
        }
    }

    if (isReceiptExpanded) {
        ReceiptDetails(selectedOrderId, selectedCustomerId) {
            isReceiptExpanded = false
        }
    }
}

@Composable
fun SalesItem(navController: NavController,
              name: String,
              address: String,
              orderNumber: Long,
              date: String,
              orderBreakdown: List<ReceiptItem>,
              onItemClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(100.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White)
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier.fillMaxWidth(0.5f)
                    .drawBehind {
                        drawLine(
                            color = dirtyWhite,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = name, fontFamily = font_archivo, fontSize = 15.sp, modifier = Modifier.padding(start = 15.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 10.dp)
                    .drawBehind {
                        drawLine(
                            color = dirtyWhite,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painter = painterResource(id = R.drawable.bookmark), contentDescription = "Bookmark", tint = bookmark, modifier = Modifier.size(20.dp))
                Text(text = "#$orderNumber", fontFamily = font_archivo, fontSize = 10.sp)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight()
                    .padding(start = 15.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ){
                val details = buildAnnotatedString {
                    withStyle(SpanStyle(fontFamily = font_archivo, fontSize = 10.sp)) {
                        append(address)
                        append("\n")
                        append(date)
                    }
                }

                Text(text = details, style = TextStyle(lineHeight = 15.sp), modifier = Modifier.padding(top = 5.dp))
                Row(
                    modifier = Modifier.padding(top = 10.dp).clip(
                        RoundedCornerShape(5.dp)).background(ButtonGreen).width(50.dp). height(30.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painter = painterResource(id = R.drawable.truck), contentDescription = "Delivered", tint = Color.White, modifier = Modifier.size(25.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(end= 5.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Icon(
                    painter = painterResource(id = R.drawable.receipt),
                    contentDescription = "Receipt",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            onItemClick()
                        }
                )
            }
        }
    }
}

@Composable
fun ReceiptDetails(orderId: Long?, customerId: Long?, onClose: () -> Unit){
    println("OrderId: $orderId")
    val salesViewModel = LocalSalesViewModel.current
    salesViewModel.fetchSalesBySalesId(orderId!!)
    val customerViewModel = LocalCustomerViewModel.current
    val selectedSalesItem by salesViewModel.selectedSalesItem.observeAsState()
    val customerDetailState = remember { mutableStateOf<CustomerDetail?>(null) }
    val insets = WindowInsets.systemBars.asPaddingValues()

    LaunchedEffect(orderId) {
        orderId?.let {
            salesViewModel.fetchSalesById(it)
        }
    }

    LaunchedEffect(customerId) {
        customerId?.let {
            val fetchedCustomerDetail = customerViewModel.fetchCustomerById(it)
            customerDetailState.value = fetchedCustomerDetail
        }
    }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(top = insets.calculateTopPadding())
        .background(Brush.horizontalGradient(listOf(BlueStart, BlueEnd))),
        horizontalAlignment = Alignment.CenterHorizontally,){
        Row(modifier = Modifier.fillMaxWidth().padding(0.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
            Text(text = "SALES",
                fontFamily = kanit_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White)
            Spacer(modifier = Modifier.fillMaxWidth(0.28f))
            Icon(painter = painterResource(id = R.drawable.face_man)
                , contentDescription = "Profile",
                modifier = Modifier.size(70.dp).padding(end = 15.dp).clickable {},)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.Start
        ){
            Text(text = "Receipt",
                color = Color.White,
                fontFamily = font_abeezee,
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp
            )
        }
        Column(modifier = Modifier
            .fillMaxWidth(0.98f)
            .fillMaxHeight(0.99f)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
        ){
            Row(modifier = Modifier.fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp), horizontalArrangement = Arrangement.Start){
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back",
                    modifier = Modifier.clickable {
                        onClose()
                    })
            }
            Row(modifier = Modifier.fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp), horizontalArrangement = Arrangement.Start)
            {
                Text(text = "#$orderId",
                    fontFamily = font_notosans_regular,
                    fontSize = 20.sp,
                    color = Color.Gray)
            }
            Row(modifier = Modifier.fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp), horizontalArrangement = Arrangement.Start)
            {
                val dateDelivered = selectedSalesItem?.dateDelivered
                val timeDelivered = selectedSalesItem?.timeDelivered
                Text(text = "$dateDelivered",
                    fontFamily = font_notosans_regular,
                    fontSize = 15.sp,)
                Text(text = "\t$timeDelivered",
                    fontFamily = font_notosans_regular,
                    fontSize = 15.sp,)
                Spacer(modifier = Modifier.fillMaxWidth(0.75f))
                Icon(painter = painterResource(id = R.drawable.bookmark), contentDescription = "Delivered", tint = bookmark, modifier = Modifier.size(35.dp))
            }
            Row(modifier = Modifier.fillMaxWidth().drawBehind {
                val borderSize = 1.dp.toPx()
                drawLine(
                    color = dirtyWhite,
                    start = Offset(0f, size.height - borderSize / 2),
                    end = Offset(size.width, size.height - borderSize / 2),
                    strokeWidth = borderSize
                )
            }.padding(bottom = 20.dp, top = 10.dp)
                , horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top,)
            {
                Text(text = "STATUS", fontFamily = font_notosans_bold, fontSize = 30.sp,
                    modifier = Modifier
                        .padding(start = 20.dp).align(Alignment.Top))
                Spacer(modifier = Modifier.width(20.dp))
                Text("  Delivered  ", fontFamily = font_notosans_bold,
                    modifier = Modifier.padding(top = 13.dp).background(ButtonGreen))

            }
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f)
                .drawBehind {
                    val borderSize = 1.dp.toPx()
                    drawLine(
                        color = dirtyWhite,
                        start = Offset(0f, size.height - borderSize / 2),
                        end = Offset(size.width, size.height - borderSize / 2),
                        strokeWidth = borderSize
                    )
                }.padding(start = 20.dp, top = 20.dp)){

                Row(modifier = Modifier.fillMaxWidth()){
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Warehouse", tint = Color.Red)
                    Text("Warehouse",
                        fontFamily = font_notosans_bold, fontSize = 15.sp
                    )
                }
                Text("B20 L15, Delaware Street, Phase 7, Palmera Bulacan", fontFamily = font_notosans_bold, fontSize = 12.sp,
                    modifier = Modifier.padding(start = 40.dp))

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(start = 5.dp), verticalAlignment = Alignment.CenterVertically){
                    Canvas(modifier = Modifier.size(15.dp)) {
                        drawCircle(
                            color = Color.Green,
                            radius = size.minDimension / 2
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Deliver Location", fontFamily = font_notosans_bold, fontSize = 15.sp)
                }
                Text(customerDetailState.value?.address ?: "", fontFamily = font_notosans_bold, fontSize = 12.sp,
                    modifier = Modifier.padding(start = 40.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight()
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 10.dp)
                ){
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile",
                        modifier = Modifier.size(55.dp))
                    Column(
                        modifier = Modifier.padding(start = 10.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(text = customerDetailState.value?.name ?: "", fontFamily = font_notosans_bold, fontSize = 15.sp)
                        Text(text = customerDetailState.value?.contactNumber ?: "", fontFamily = font_notosans_bold, fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End
                ){

                    OrderDetails(salesViewModel.selectedSalesItem.value?.orderDetails ?: emptyList())

                    val getTotal = selectedSalesItem?.totalAmount ?: 0.0

                    Text(text = "Total: ₱${getTotal}", fontFamily = font_notosans_bold, fontSize = 13.sp, modifier = Modifier.padding(end = 20.dp, top = 5.dp))
                }
            }
        }
    }
}

@Composable
fun OrderDetails(orderList: List<ReceiptItem>) {
    println("OrderList: $orderList")
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f)
            .padding(end = 10.dp, top = 10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(dirtyWhite)
    ) {
        items(orderList) { item ->
            ReceiptItem(item.name,
                item.amount, item.quantity
            )
        }
    }
}

@Composable
fun ReceiptItem(
    name: String,
    amount: Double,
    quantity: Double,
) {

    val productsViewModel = LocalProductsViewModel.current
    val productIcon = productsViewModel.productIcons[name]

    LaunchedEffect(name) {
        productsViewModel.fetchProductIconByName(name)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(100.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .height(70.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(dirtyWhite),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(productIcon != null){
                        Image(
                            bitmap = productIcon.asImageBitmap(),
                            contentDescription = name,
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            colorFilter = if (name == "") {
                                ColorFilter.tint(Color.White)
                            } else {
                                null
                            },
                        )
                    }else{
                        Image(
                            painter = painterResource(id = R.drawable.image_area),
                            contentDescription = name,
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom,

                        ) {
                        Text(
                            name,
                            style = TextStyle(
                                lineHeight = 15.sp
                            ),
                            fontFamily = font_notosans_bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(dirtyWhite)
                            .height(1.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Amount: ")

                                withStyle(SpanStyle(fontFamily = font_notosans_bold)) {
                                    append(amount.toString())
                                }
                            },
                            fontFamily = font_notosans_regular,
                            fontSize = 10.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val subtotal = amount * quantity
                        Text(
                            text = buildAnnotatedString {
                                append("Quantity: ")
                                withStyle(SpanStyle(fontFamily = font_notosans_bold)){
                                    append(quantity.toString())
                                }
                            },
                            fontFamily = font_notosans_regular,
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            modifier = Modifier
                                .padding(end = 5.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(100.dp)
                .width(13.dp)
                .padding(start = 7.dp, top = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ButtonGreen)
        )

        Column(
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(end = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Total: ${amount * quantity}", fontFamily = font_notosans_bold,
                fontSize = 10.sp)
        }
    }
}