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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite

@Composable
fun SelectCustomer(navController: NavController, customerPicked: () -> Unit){
    var isOldCustomer by remember { mutableStateOf(false) }
    var isNewCustomer by remember { mutableStateOf(false) }
    var isClosed by remember { mutableStateOf(true) }

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
                Text(text = "Old Customer", color = Color.Black, fontWeight = FontWeight.Normal, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f))

            Button(
                onClick = { isNewCustomer = true},
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(text = "New Customer", color = Color.Black, fontWeight = FontWeight.Normal, fontSize = 20.sp)
            }
        }

        if (isOldCustomer && !isClosed) {
            SelectOldCustomer(onClose = {
                isClosed = true
                customerPicked()
            }, customerPicked = {
                isClosed = true
                customerPicked()
            })
        }

        if (isNewCustomer && !isClosed) {
            SelectNewCustomer(onClose = {
                isClosed = true
            })
        }
    }
}

@Composable
fun SelectOldCustomer(onClose: () -> Unit, customerPicked: () -> Unit) {
    val customerViewModel = LocalCustomerViewModel.current
    val customerDetails = customerViewModel.customerDetails

    // Use rememberLazyListState for LazyColumn
    val lazyListState = rememberLazyListState()

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

            // Wrap LazyColumn in another Column
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight(0.9f)
            ) {
                Text(text = "Search", modifier = Modifier.padding(8.dp)) // Added padding for the search text

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
                            area = customer.area,
                            blockLot = customer.blockLot
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
fun SelectNewCustomer(onClose: () -> Unit){

}

@Composable
fun CustomerItem(
    name: String,
    gender: String,
    contact: String,
    area: String,
    blockLot: String,
    isSelected: () -> Unit
){
    val customerDetail = LocalCustomerViewModel.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 10.dp)
            .clickable {
                isSelected()
                customerDetail.setCustomerDetail(CustomerDetail(name, gender, area, blockLot, contact))
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
                    append("$contact\t  ")
                    append("$area, $blockLot")
                }
            }
            Text(text = detailText, style = TextStyle(lineHeight = 13.sp,), modifier = Modifier.padding(start = 5.dp))
        }
    }
}