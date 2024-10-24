package com.example.kaydensdigitalassistant

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.ReceiptViewModel
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.OrderDetails
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import kotlin.random.Random


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReceiptPreview(navController: NavController, paymentOption: String, pricingOption: String){
    val insets = WindowInsets.systemBars.asPaddingValues()
    val viewModel = LocalReceiptViewModel.current
    val customerDetail = LocalCustomerViewModel.current
    val currentCustomer = customerDetail.currentCustomer.value

    println("Confirmed Receipt: ${viewModel.receiptItemsState}")
    var isConfirmed by remember{ mutableStateOf(false)}

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
            Text(
                text = "RECEIPT",
                fontFamily = kanit_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ){
            Icon(
                painter = painterResource(id = R.drawable.printer),
                contentDescription = "Print",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {

                    },
            )

            Icon(
                painter = painterResource(id = R.drawable.tray_arrow_down),
                contentDescription = "Download",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { },
            )
        }

        val businessDetails = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold)) {
                append("KAYDEN\n")
            }
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append("Employee #023578\n")
            }
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append(CurrentDateTime() + "\n")
            }
            withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Light)) {
                append("${currentCustomer.address}, Palmera Bulacan\n")
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
                    if (pricingOption != "Discounted") {
                        price.toString()
                    } else {
                        val discount = price * (1.875 / 100)
                        (price - discount).toString()
                    } + "\n"
                )
            }
            if(pricingOption == "Discounted"){
                withStyle(style = SpanStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal)) {
                    append("(Discounted 1.875% off)")
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
        if(isConfirmed){
            ConfirmPurchase(navController, pricingOption)
            navController.navigate("selectCustomer")
            isConfirmed = false
        }
    }
}

@Composable
fun ConfirmPurchase(navController: NavController, pricingOption: String) {
    val viewModel = LocalReceiptViewModel.current
    val customerViewModel = LocalCustomerViewModel.current
    val salesViewModel = LocalSalesViewModel.current

    LaunchedEffect(key1 = customerViewModel.currentCustomer.value) {
        customerViewModel.currentCustomer.let {

            val currentCustomerDetails = customerViewModel.currentCustomer.value

            viewModel.updateCustomerPreference(customerViewModel.currentCustomer.value)

            salesViewModel.addSales(OrderDetails(
                currentCustomerDetails.name,
                currentCustomerDetails.address,
                currentCustomerDetails.contactNumber,
                viewModel.getReceiptList(),
                pricingOption,
                viewModel.getTotalAmount(),
                getCurrentTimeDate(),
                Random.nextInt(10_000_000, 100_000_000)
            ))

            viewModel.receiptItemsState.forEach { receiptItem ->
                // Find matching product in productList
                val product = viewModel.productList.find { it.name == receiptItem.name }
                if (product != null) {
                    // Reduce the stock of the product
                    product.stock -= receiptItem.quantity // Assuming 'stock' is the property to reduce
                }
            }

            snapshotFlow { salesViewModel.salesList }
                .collect { updatedSalesList ->
                    println("Updated Sales List: $updatedSalesList")
                    println("Updated Sales List: ${updatedSalesList[0]}")
                }

            viewModel.clearReceiptItems()
            navController.navigate("receipt")
        }
    }
}
