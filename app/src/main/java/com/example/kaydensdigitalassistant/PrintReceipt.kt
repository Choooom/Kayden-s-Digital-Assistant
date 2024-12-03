package com.example.kaydensdigitalassistant

import com.example.kaydensdigitalassistant.data.SalesItem
import PrintToThermalPrinter
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.room.withTransaction
import com.example.kaydensdigitalassistant.data.AppDatabase
import com.example.kaydensdigitalassistant.data.ReceiptItem
import com.example.kaydensdigitalassistant.font_abeezee
import com.example.kaydensdigitalassistant.kanit_bold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReceiptPreview(navController: NavController, paymentOption: String, pricingOption: String, deposit: Double){
    val insets = WindowInsets.systemBars.asPaddingValues()
    val viewModel = LocalReceiptViewModel.current
    val customerDetail = LocalCustomerViewModel.current
    val currentCustomer = if(!customerDetail.isNewCustomer.value) customerDetail.fetchCurrentCustomer() else customerDetail.fetchCurrentNewCustomer()
    println("Deposit: $deposit")

    println("Confirmed Receipt: ${viewModel.receiptItemsState}")
    var isConfirmed by remember{ mutableStateOf(false)}
    var print by remember{ mutableStateOf(false) }

    val referenceNumber = remember { generateReferenceNumber() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(Brush.horizontalGradient(colors = listOf(BlueStart, BlueEnd))),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.07f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Button",
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
            TopBar(navController = navController, "RECEIPT")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ){
            Icon(
                painter = painterResource(id = R.drawable.printer),
                contentDescription = "Print",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        print = true
                    },
            )
        }

        val businessDetails = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold)) {
                append("KAYDEN\n")
            }
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append("Reference No.: $referenceNumber\n")
            }
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append(CurrentDateTime() + "\n")
            }

            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append("${currentCustomer?.value?.name}\n")
            }

            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                if (currentCustomer != null) {
                    append("${currentCustomer.value.address}, Palmera Bulacan\n")
                }
            }
            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                append("•••••••••••••••••••••••••••••••••••••••••••••••\n")
            }

            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append("${paymentOption}${if (paymentOption == "Gcash -") " 09178122285" else ""}\n")
            }

            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                append("•••••••••••••••••••••••••••••••••••••••••••••••\n")
            }
        }

        val salesDescription = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)){
                append("Description\n")
            }
            for(item in viewModel.getReceiptList()){
                withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Light)){
                    append("(x${item.quantity}) " + item.name + "\n")
                }
            }
        }

        val priceTable = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)){
                append("Price\n")
            }
            for(item in viewModel.getReceiptList()){
                withStyle(style = SpanStyle(fontSize = 13.sp, fontWeight = FontWeight.Light)){
                    append((item.amount * item.quantity).toString() + "\n")
                }
            }
        }

        val totalBorder = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                append("•••••••••••••••••••••••••••••••••••••••••••••••\n")
            }
            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                append("•••••••••••••••••••••••••••••••••••••••••••••••\n")
            }
        }

        val totalColumn = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)) {
                append("Total: ")
            }
        }

        val breakdown = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 17.sp)) {
                val price = viewModel.getTotalAmount()
                append(
                    (price + deposit).toString()  + "\n"
                )
            }
            if(pricingOption == "Discounted"){
                withStyle(style = SpanStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)) {
                    append("(Discounted)")
                }
            }

            if(deposit > 0.0){
                withStyle(style = SpanStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)) {
                    append("\nDeposit: $deposit")
                }
            }
        }

        val bottomBorder = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                append("•••••••••••••••••••••••••••••••••••••••••••••••\n")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            item{
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp))
            }

            item{
                Text(
                    text = businessDetails,
                    fontFamily = font_abeezee,
                    style = TextStyle(
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(0.dp)
                )
            }

            item{
                Row(modifier = Modifier.fillMaxWidth()){
                    Column(modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(start = 25.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top){
                        Text(text = salesDescription,
                            fontFamily = font_abeezee,
                            style = TextStyle(
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier
                                .padding(0.dp))
                    }
                    Column(modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(end = 25.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Top){
                        Text(text = priceTable,
                            fontFamily = font_abeezee,
                            style = TextStyle(
                                lineHeight = 20.sp,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier
                                .padding(0.dp))
                    }
                }
            }
            item{
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = totalBorder,
                        fontFamily = font_abeezee,
                        style = TextStyle(
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(0.dp)
                    )
                }
            }
            item{
                Row(modifier = Modifier.fillMaxWidth()){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(start = 25.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ){
                        Text(text = totalColumn,
                            fontFamily = font_abeezee,
                            style = TextStyle(
                                lineHeight = 20.sp,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier
                                .padding(0.dp))
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(end = 25.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Top
                    ){
                        Text(text = breakdown,
                            fontFamily = font_abeezee,
                            style = TextStyle(
                                lineHeight = 20.sp,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier
                                .padding(0.dp))
                    }
                }
            }
            item{
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = bottomBorder,
                        fontFamily = font_abeezee,
                        style = TextStyle(
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center)
                    )
                }
            }
            item{
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "THANK YOU!",
                        fontFamily = font_abeezee,
                        style = TextStyle(
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Center)
                    )
                }
            }
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
        )
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically){
            Text(
                text = getCurrentTimeDate(),
                fontFamily = font_abeezee,
                style = TextStyle(
                    lineHeight = 20.sp, color = Color.White)
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically){
            Button(
                onClick = {
                    isConfirmed = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Confirm")
                    },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        if(print){
            if (currentCustomer != null) {
                PrintToThermalPrinter(
                    referenceNumber = referenceNumber,
                    customerName = currentCustomer.value.name,
                    customerAddress = "${currentCustomer.value.address}, Palmera Bulacan",
                    paymentOption = paymentOption,
                    receiptItems = viewModel.receiptItemsState,
                    totalAmount = viewModel.getTotalAmount(),
                    pricingOption = pricingOption,
                    deposit = deposit
                )
            }
            print = false
        }

        if(isConfirmed){
            ConfirmPurchase(navController, paymentOption, pricingOption, referenceNumber, deposit)
            navController.navigateWithPopUp(
                route = "selectCustomer",
                popUpToRoute = "INITIALIZATION_MODE",
                inclusive = true
            )
            isConfirmed = false
        }
    }
}

@Composable
fun ConfirmPurchase(navController: NavController, paymentOption: String, pricingOption: String, referenceNumber: String, deposit: Double) {
    val customerDetailViewModel = LocalCustomerViewModel.current
    val receiptViewModel = LocalReceiptViewModel.current
    val productsViewModel = LocalProductsViewModel.current
    val currentCustomer = if(!customerDetailViewModel.isNewCustomer.value) customerDetailViewModel.fetchCurrentCustomer() else customerDetailViewModel.fetchCurrentNewCustomer()
    val receiptItemsState = receiptViewModel.receiptItemsState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentCustomer) {
        println("DEBUG: Confirm Purchase - Current Customer: $currentCustomer")
        try {
            val appDatabase = AppDatabase.getInstance(context)

            withContext(Dispatchers.IO) {
                appDatabase.withTransaction {
                    val updatedCustomer = currentCustomer.value.copy(
                        preferredOrder = receiptItemsState.map { it.name }
                    )

                    appDatabase.customerDetailDao().updateCustomer(updatedCustomer)

                    val salesItem = SalesItem(
                        customerId = currentCustomer.value.customerId,
                        employeeId = 123456789L,
                        orderDetails = receiptItemsState,
                        totalAmount = calculateTotalAmount(receiptItemsState),
                        dateDelivered = getCurrentDate(),
                        timeDelivered = getCurrentTime(),
                        paymentMethod = paymentOption,
                        paymentOption = pricingOption,
                        referenceNumber = referenceNumber,
                        deposit = deposit
                    )

                    appDatabase.salesItemDao().insertSalesItem(salesItem)

                    receiptItemsState.forEach { item ->
                        productsViewModel.updateStockAfterSale(
                            productName = item.name,
                            quantity = item.quantity
                        )
                    }
                }
            }

            withContext(Dispatchers.Main) {
                receiptViewModel.receiptItemsState.clear()
                customerDetailViewModel.resetCurrentCustomer()
                customerDetailViewModel.resetNewCurrentCustomer()
                println("Sale processed, customer reset")
                navController.navigateWithPopUp(
                    route = "selectCustomer",
                    popUpToRoute = "INITIALIZATION_MODE",
                    inclusive = true
                )
            }
        } catch (e: Exception) {
            println("Error processing sale: ${e.message}")
        }
    }
}


private fun calculateTotalAmount(receiptItemsState: List<ReceiptItem>): Double {
    // Calculate the total amount from the receiptItems
    return receiptItemsState.sumOf { it.amount * it.quantity}
}

private fun getCurrentTime(): String {
    // Get the current time in the Philippine timezone
    val philippineTimeZone = TimeZone.getTimeZone("Asia/Manila")
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    dateFormat.timeZone = philippineTimeZone
    return dateFormat.format(Date())
}

fun getCurrentDate(): String {
    // Get the current date in the Philippine timezone
    val philippineTimeZone = TimeZone.getTimeZone("Asia/Manila")
    val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    dateFormat.timeZone = philippineTimeZone
    return dateFormat.format(Date())
}

fun generateReferenceNumber(): String {
    val random = Random.Default
    val digits = (1..12).map { random.nextInt(0, 10) }

    return digits.chunked(4)
        .joinToString(" ") { group ->
            group.joinToString("")
        }
}



