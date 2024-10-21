package com.example.kaydensdigitalassistant

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite

@Composable
fun SelectCustomer(navController: NavController){
    var isOldCustomer by remember { mutableStateOf(false) }
    var isNewCustomer by remember { mutableStateOf(false) }
    var isClosed by remember { mutableStateOf(true) }
    val receiptViewModel = LocalReceiptViewModel.current

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

            Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f))

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
                receiptViewModel.clearReceiptItems()
                navController.navigate("receipt")
            })
        }

        if (isNewCustomer && !isClosed) {
            SelectNewCustomer(onClose = {
                isClosed = true
                isNewCustomer = false
            },customerPicked = {
                isClosed = true
                receiptViewModel.clearReceiptItems()
                navController.navigate("receipt")
            }
            )
        }
    }
}

@Composable
fun SelectOldCustomer(onClose: () -> Unit, customerPicked: () -> Unit) {
    val customerViewModel = LocalCustomerViewModel.current
    val customerDetails = customerViewModel.customerDetails

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
                modifier = Modifier.fillMaxWidth().padding(0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Old Customer", fontFamily = font_archivo, color = BlueEnd, fontSize = 30.sp)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(0.9f)
            ) {
                Text(text = "Search", modifier = Modifier.padding(8.dp))

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
                        println(customer)
                        CustomerItem(
                            name = customer.name,
                            gender = customer.gender,
                            contact = customer.contactNumber,
                            address = customer.address
                        ){
                            customerPicked()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SelectNewCustomer(onClose: () -> Unit, customerPicked: () -> Unit){
    val customerViewModel = LocalCustomerViewModel.current
    var fullName by remember{ mutableStateOf("")}
    var contactNumber by remember{ mutableStateOf("")}
    var address by remember{ mutableStateOf("")}

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
                modifier = Modifier.fillMaxWidth().padding(0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New Customer", fontFamily = font_archivo, color = BlueEnd, fontSize = 30.sp)
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                Row(modifier = Modifier.fillMaxWidth().padding(start = 45.dp), horizontalArrangement = Arrangement.Start){
                    Text(text = "FULL NAME", fontFamily = font_notosans_regular, fontSize = 15.sp, color = Color.Black)
                }
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {fullName = it},
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
                        unfocusedIndicatorColor = BlueEnd
                    )
                )
                Row(modifier = Modifier.fillMaxWidth().padding(start = 45.dp), horizontalArrangement = Arrangement.Start){
                    Text(text = "NUMBER", fontFamily = font_notosans_regular, fontSize = 15.sp)
                }
                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = {contactNumber = it},
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
                        unfocusedIndicatorColor = BlueEnd
                    )
                )
                Row(modifier = Modifier.fillMaxWidth().padding(start = 45.dp), horizontalArrangement = Arrangement.Start){
                    Text(text = "ADDRESS", fontFamily = font_notosans_regular, fontSize = 15.sp)
                }
                OutlinedTextField(
                    value = address,
                    onValueChange = {address = it},
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
                        unfocusedIndicatorColor = BlueEnd
                    )
                )
            }
            Button(
                onClick = {
                    customerViewModel.addCustomerDetail(CustomerDetail(fullName, "Male", address, contactNumber))
                    customerViewModel.currentCustomer.value = CustomerDetail(fullName, "Male", address, contactNumber)
                    println(customerViewModel.currentCustomer)
                    customerPicked()
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
    gender: String,
    contact: String,
    address: String,
    isSelected: () -> Unit
){
    val customerDetail = LocalCustomerViewModel.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 10.dp)
            .clickable {
                isSelected()
                customerDetail.setCustomerDetail(CustomerDetail(name, gender, address, contact))
                       },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(painter = if(gender == "Male") painterResource(id = R.drawable.face_man)
        else painterResource(id = R.drawable.face_woman), contentDescription = "Icon",
            modifier = Modifier.size(35.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){
            var detailText = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 15.sp, fontFamily = font_notosans_bold)){
                    append("$name\n")
                }
                withStyle(SpanStyle(fontSize = 10.sp, fontFamily = font_notosans_regular)){
                    append("$contact\t\t")
                    append(address)
                }
            }
            Text(text = detailText, color = Color.Black, style = TextStyle(lineHeight = 13.sp,), modifier = Modifier.padding(start = 5.dp))
        }
    }
}