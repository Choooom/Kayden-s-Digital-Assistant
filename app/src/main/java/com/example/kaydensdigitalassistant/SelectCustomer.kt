package com.example.kaydensdigitalassistant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.CustomerDetailViewModel
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite
import com.example.kaydensdigitalassistant.font_archivo
import com.example.kaydensdigitalassistant.font_notosans_bold
import com.example.kaydensdigitalassistant.font_notosans_regular

@Composable
fun SelectCustomer(navController: NavController){
    var isOldCustomer by remember { mutableStateOf(false) }
    var isNewCustomer by remember { mutableStateOf(false) }
    var isClosed by remember { mutableStateOf(true) }
    val receiptViewModel = LocalReceiptViewModel.current

    /*
        val appViewModel = LocalAppViewModel.current
        appViewModel.clearAllData()
   */
    /*
        val productViewModel = LocalProductsViewModel.current
        val context = LocalContext.current

        fun getBitmap(drawableId: Int): Bitmap {
            return BitmapFactory.decodeResource(context.resources, drawableId)
        }

        val productsList = listOf(
            Products(productName = "Red Horse 1000 ML (Mucho)", type = "Beer", normalPrice = 630.0, discountedPrice = 628.0, stock = 100.0, productIcon = getBitmap(R.drawable.mucho)),
            Products(productName = "Red Horse 500 ML", type = "Beer", normalPrice = 620.0, discountedPrice = 615.0, stock = 100.0, productIcon = getBitmap(R.drawable.redhorse_500)),
            Products(productName = "Red Horse 330 ML (Stallion)", type = "Beer", normalPrice = 860.0, discountedPrice = 853.0, stock = 100.0, productIcon = getBitmap(R.drawable.stallion)),
            Products(productName = "Pale Pilsen 1000 ML (Grande)", type = "Beer", normalPrice = 550.0, discountedPrice = 544.0, stock = 100.0, productIcon = getBitmap(R.drawable.grande)),
            Products(productName = "Pale Pilsen 320 ML", type = "Beer", normalPrice = 820.0, discountedPrice = 800.0, stock = 100.0, productIcon = getBitmap(R.drawable.pilsen_small)),
            Products(productName = "San Mig Light 330 ML", type = "Beer", normalPrice = 1040.0, discountedPrice = 1020.0, stock = 100.0, productIcon = getBitmap(R.drawable.sanmig_light)),
            Products(productName = "San Mig Apple 330 ML", type = "Beer", normalPrice = 800.0, discountedPrice = 778.0, stock = 100.0, productIcon = getBitmap(R.drawable.sanmig_apple)),
            Products(productName = "RC Original Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.rc_small)),
            Products(productName = "RC Orange Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.orange_small)),
            Products(productName = "RC Lemon Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.lemon_small)),
            Products(productName = "RC Root Beer Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.rootbeer_small)),
            Products(productName = "RC Mega Original 800 ML", type = "Softdrink", normalPrice = 260.0, discountedPrice = 253.0, stock = 100.0, productIcon = getBitmap(R.drawable.rc_mega)),
            Products(productName = "RC Mega Orange 800 ML", type = "Softdrink", normalPrice = 260.0, discountedPrice = 253.0, stock = 100.0, productIcon = getBitmap(R.drawable.orange_mega)),
            Products(productName = "RC Mega Lemon 800 ML", type = "Softdrink", normalPrice = 260.0, discountedPrice = 253.0, stock = 100.0, productIcon = getBitmap(R.drawable.lemon_mega)),
            Products(productName = "Cobra Original (Yellow) 240 ML", type = "Energy-Drink", normalPrice = 300.0, discountedPrice = 295.0, stock = 100.0, productIcon = getBitmap(R.drawable.cobra_yellow)),
            Products(productName = "Cobra Citrus (Green) 240 ML", type = "Energy-Drink", normalPrice = 300.0, discountedPrice = 295.0, stock = 100.0, productIcon = getBitmap(R.drawable.cobra_green))
        )

        LaunchedEffect(key1 = Unit) {
            productsList.forEach { product ->
                productViewModel.insertProduct(product)
            }
        }
    */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.4f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(
                onClick = { isOldCustomer = true
                    isClosed = false},
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(text = "Old Customer", color = Color.Black, fontWeight = FontWeight.Normal, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f))

            Button(
                onClick = { isNewCustomer = true
                    isClosed = false},
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(text = "New Customer", color = Color.Black, fontWeight = FontWeight.Normal, fontSize = 15.sp)
            }
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
            },customerPicked = {
                isClosed = true
                receiptViewModel.receiptItemsState.clear()
                navController.navigate("receipt")
            }
            )
        }
    }
}

@Composable
fun SelectOldCustomer(onClose: () -> Unit, customerPicked: (CustomerDetail) -> Unit, navController: NavController) {
    val customerViewModel = LocalCustomerViewModel.current
    val customerDetails by customerViewModel.customerDetails.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

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
                .fillMaxHeight(0.5f)
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
                Text("Old Customer", fontFamily = font_archivo, color = BlueEnd, fontSize = 30.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        customerViewModel.setSearchQuery(it)
                    },
                    placeholder = { Text("Search customers") },
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { customerViewModel.setSortOrder(CustomerDetailViewModel.SortOrder.BY_NAME) }) {
                    Icon(Icons.Default.KeyboardArrowDown, "Sort by name")
                }

                IconButton(onClick = { customerViewModel.setSortOrder(CustomerDetailViewModel.SortOrder.BY_ADDRESS) }) {
                    Icon(Icons.Default.LocationOn, "Sort by address")
                }
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
                .fillMaxHeight(0.6f)
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
