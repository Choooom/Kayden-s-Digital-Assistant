package com.example.kaydensdigitalassistant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.data.ReceiptItem
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite
import com.example.kaydensdigitalassistant.font_archivo
import com.example.kaydensdigitalassistant.font_archivo_bold
import com.example.kaydensdigitalassistant.font_archivo_light
import com.example.kaydensdigitalassistant.font_notosans_bold
import com.example.kaydensdigitalassistant.font_notosans_regular
import com.example.kaydensdigitalassistant.kanit_bold



@Composable
fun GenerateReceipt(navController: NavController) {
    val insets = WindowInsets.systemBars.asPaddingValues()

    var isCustomerSelected by remember { mutableStateOf(false) }

    fun customerPickedCallback(){
        isCustomerSelected = true
    }

    val receiptViewModel = LocalReceiptViewModel.current
    val receiptViewModels = LocalProductsViewModel.current
    val customerViewModel = LocalCustomerViewModel.current
    val currentCustomer = customerViewModel.currentCustomer.value

    var deposit by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        if (currentCustomer.preferredOrder.isNotEmpty() && receiptViewModel.receiptItemsState.isEmpty()) {
            // Get product details for each preferred item
            currentCustomer.preferredOrder.forEach { productName ->
                // Assuming you have a method to get product details by name
                val product = receiptViewModels.getProductByName(productName)
                product?.let {
                    receiptViewModel.addProductItem(
                        ReceiptItem(
                            name = it.productName,
                            type = it.type,
                            amount = if (receiptViewModel.isDiscounted) it.discountedPrice else it.normalPrice,
                            quantity = 1.0
                        )
                    )
                }
            }
        }
    }


    val totalAmount by rememberUpdatedState(newValue = receiptViewModel.receiptItemsState.sumOf { it.amount * it.quantity } + deposit)

    val totalAmountDisplay = buildAnnotatedString {
        withStyle(style = SpanStyle(fontFamily = font_archivo_light, fontSize = 15.sp)) {
            append("Total Amount: ")
        }
        withStyle(style = SpanStyle(
            fontFamily = font_archivo_bold,
            fontSize = 17.sp
        )) {
            append(totalAmount.toString())
        }
    }

    var selectedPaymentOption by remember { mutableStateOf("Cash") }
    var selectedPricingOption by remember { mutableStateOf("Regular") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BlueStart, BlueEnd)
                )
            )
    ) {
        TopBar(navController = navController, "RECEIPT")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.98f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.05f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    modifier = Modifier.clickable {
                        navController.navigateWithPopUp(
                            route = "selectCustomer",
                            popUpToRoute = "INITIALIZATION_MODE",
                            inclusive = true
                        )}
                )
                Spacer(modifier = Modifier.width(20.dp))
                CustomDropdownMenu(
                    options = listOf("Cash", "Gcash", "Consignment"),
                    selectedOption = selectedPaymentOption,
                    onOptionSelected = { selectedPaymentOption = it },
                    placeholder = "Cass",
                )

                Spacer(modifier = Modifier.width(16.dp))

                CustomDropdownMenu(
                    options = listOf("Regular", "Discounted"),
                    selectedOption = selectedPricingOption,
                    onOptionSelected = { selectedPricingOption = it },
                    placeholder = "Regular"
                )
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp, end = 10.dp), horizontalArrangement = Arrangement.End) {
                    CustomDoubleTextField(
                        value = deposit,
                        onValueChange = { deposit = it },
                        placeholder = "Deposit"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(dirtyWhite),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReceiptSection(navController)

                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Product",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(bottom = 10.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = {
                            receiptViewModel.addProductItem(ReceiptItem("", "", 0.0, 0.0))
                        }),
                    tint = BlueStart,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
                    .padding(top = 5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SkyBlue),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = totalAmountDisplay,
                    fontFamily = font_archivo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonGreen
                    ),
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp)
                            .clickable {
                                println(selectedPaymentOption)
                                if (totalAmount != 0.0) navController.navigateWithPopUp(
                                    route = "receiptPreview/$selectedPaymentOption/$selectedPricingOption/${deposit.toFloat()}",
                                    popUpToRoute = "INITIALIZATION_MODE",
                                    inclusive = true
                                )
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "Proceed",
                            fontFamily = font_notosans_bold,
                        )
                    }
                }
            }
        }
    }

    if(selectedPricingOption == "Discounted") receiptViewModel.isDiscounted = true
    if(selectedPricingOption == "Regular") receiptViewModel.isDiscounted = false

    if(receiptViewModel.showProductList){
        ProductList(navController, onClose = { receiptViewModel.showProductList = false })
    }
}


@Composable
fun ReceiptSection(navController: NavController) {
    val receiptViewModel = LocalReceiptViewModel.current
    val currentCustomer = LocalCustomerViewModel.current

    LaunchedEffect(key1 = currentCustomer.currentCustomer.value) {
        if (receiptViewModel.preferenceAvailable(currentCustomer.currentCustomer.value)) {
            println(receiptViewModel.customerPreference[0].orderPreference)
            val customerIndex = receiptViewModel.customerPreference.indexOf(
                receiptViewModel.customerPreference.find {
                    it.customerDetails.name == currentCustomer.currentCustomer.value?.name ?: ""
                }
            )

            if (customerIndex != -1) {
                receiptViewModel.initializeReceiptItem(receiptViewModel.customerPreference[customerIndex].orderPreference)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(top = 10.dp)
    ) {
        itemsIndexed(receiptViewModel.receiptItemsState) { index, item ->

            ProductItem(navController, item.name, receiptViewModel.getReceiptList()[index].amount, item.quantity, item.type, index,
                { indexToRemove ->
                    receiptViewModel.removeReceiptItem(indexToRemove)
                },
                {
                        quantity, index -> receiptViewModel.addQuantity(quantity, index)
                },
                {
                        quantity, index -> receiptViewModel.subtractQuantity(quantity, index)
                },
                {
                    receiptViewModel.showProductList = true
                    receiptViewModel.currentIndex = it
                }
            )

        }
    }
}

@Composable
fun ProductItem(
    navController: NavController,
    name: String,
    amount: Double,
    quantity: Double,
    type: String,
    index: Int,
    onDelete: (Int) -> Unit,
    onAddQuantity: (Double, Int) -> Unit,
    onSubtractQuantity: (Double, Int) -> Unit,
    onDisplayProductList: (Int) -> Unit
) {

    var isEmpty by remember { mutableStateOf(false) }
    val productsViewModel = LocalProductsViewModel.current
    val icon = productsViewModel.productIcons[name]

    var product by remember { mutableStateOf<Products?>(null) }

    LaunchedEffect(name) {
        product = productsViewModel.getProductByName(name)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
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

                    if(name != ""){
                        if (icon != null) {
                            Image(
                                bitmap = icon.asImageBitmap(),
                                contentDescription = name,
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        isEmpty = true
                                    }
                            )
                        }else{
                            Image(
                                painter = painterResource(id = R.drawable.plus),
                                contentDescription = name,
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        isEmpty = true
                                    },
                                colorFilter = if (name == "") {
                                    ColorFilter.tint(Color.White)
                                } else {
                                    null
                                },
                            )
                        }
                    }else{
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = name,
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    isEmpty = true
                                },
                            colorFilter = ColorFilter.tint(Color.White),
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
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom,
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.trash_can_outline),
                            contentDescription = "Delete Item",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    onDelete(index)
                                },
                            colorFilter = ColorFilter.tint(Color.Red)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                            .fillMaxWidth(0.4f)
                            .height(20.dp)
                            .padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.9f)
                                .clickable {
                                    var updatedQuantity = quantity

                                    if (updatedQuantity > 0) {
                                        if (product?.isHalfable == true) {
                                            updatedQuantity -= 0.5
                                            onSubtractQuantity(updatedQuantity, index)
                                        } else {
                                            updatedQuantity -= 1.0
                                            onSubtractQuantity(updatedQuantity, index)
                                        }
                                    }
                                }
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ){
                            Icon(
                                painter = painterResource(id = R.drawable.minus),
                                contentDescription = "Minus"
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(BorderStroke(1.dp, Color.Black))
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quantity.toString(),
                                fontFamily = font_archivo_bold,
                                fontSize = 11.sp,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.9f)
                                .clickable {
                                    var updatedQuantity = quantity
                                    if (product?.isHalfable == true) {
                                        updatedQuantity += 0.5
                                        onAddQuantity(updatedQuantity, index)
                                    } else {
                                        updatedQuantity += 1.0
                                        onAddQuantity(updatedQuantity, index)
                                    }
                                }
                                .border(1.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ){
                            Icon(painter = painterResource(R.drawable.plus), contentDescription = "Plus")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val subtotal = amount * quantity
                        Text(
                            text = buildAnnotatedString {
                                append("Subtotal: ")

                                withStyle(
                                    SpanStyle(
                                        fontFamily = font_notosans_bold,
                                        background = dirtyWhite,
                                    )
                                ) {
                                    append("   " + subtotal.toString() + "   ")
                                }
                            },
                            fontFamily = font_notosans_regular,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(end = 5.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(125.dp)
                .width(15.dp)
                .padding(start = 7.dp, top = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SkyBlue)
        )

        if(isEmpty){
            onDisplayProductList(index)
            isEmpty = false
        }
    }
}

@Composable
fun CustomDropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    placeholder: String = "Select an option"
) {
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .border(1.dp, Color.Black)
            .height(20.dp)
            .width(80.dp)
            .padding(start = 5.dp)
            .background(if(!expanded) Color.White else dirtyWhite)
            .clickable { expanded = true },
        contentAlignment = Alignment.CenterStart
    ){
        Text(
            text = selectedOption.ifEmpty { placeholder },
            fontFamily = font_notosans_bold,
            fontSize = 10.sp,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}