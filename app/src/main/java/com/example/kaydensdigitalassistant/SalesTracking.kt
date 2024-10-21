package com.example.kaydensdigitalassistant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import androidx.navigation.compose.rememberNavController
import com.example.kaydensdigitalassistant.data.ReceiptItem
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.bookmark
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite

@Composable
fun SalesTracking(navController: NavController){
    val salesList = LocalSalesViewModel.current
    val insets = WindowInsets.systemBars.asPaddingValues()

    var isReceiptExpanded by remember { mutableStateOf(false) }
    var itemIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BlueStart, BlueEnd)
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Row(modifier = Modifier.fillMaxWidth().padding(0.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
            Text(text = "SALES",
                fontFamily = kanit_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White)
            Spacer(modifier = Modifier.fillMaxWidth(0.28f))
            Icon(painter = painterResource(id = R.drawable.face_man)
                , contentDescription = "Profile",
                modifier = Modifier.size(70.dp).padding(end = 15.dp).clickable {  },)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.992f)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
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
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "RECEIPT")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "Today ⌄", fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.99f)
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(dirtyWhite),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(salesList.salesList) { index, item ->
                        if(index == 0) Spacer(modifier = Modifier.height(20.dp))

                        Spacer(modifier = Modifier.height(8.dp))

                        SalesItem(
                            navController,
                            item.customerName,
                            item.customerArea,
                            item.orderNumber,
                            item.timeDelivered,
                            item.orderBreakdown
                        ){
                            itemIndex = index
                            isReceiptExpanded = !isReceiptExpanded
                        }
                    }
                }
            }
        }
    }
    if (isReceiptExpanded) {
        ReceiptDetails(itemIndex) {
            isReceiptExpanded = false
        }
    }
}

@Composable
fun SalesItem(navController: NavController,
              name: String,
              address: String,
              orderNumber: Int,
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
                horizontalArrangement = Arrangement.End
            ){
                Icon(painter = painterResource(id = R.drawable.bookmark), contentDescription = "Bookmark", tint = bookmark)
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
fun ReceiptDetails(index: Int, onClose: () -> Unit){
    val salesViewModel = LocalSalesViewModel.current
    val salesList = salesViewModel.salesList[index].orderBreakdown
    val insets = WindowInsets.systemBars.asPaddingValues()

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
                val orderNumber = salesViewModel.salesList[index].orderNumber
                Text(text = "#$orderNumber",
                    fontFamily = font_notosans_regular,
                    fontSize = 20.sp,
                    color = Color.Gray)
            }
            Row(modifier = Modifier.fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp), horizontalArrangement = Arrangement.Start)
            {
                val dateDelivered = salesViewModel.salesList[index].timeDelivered
                Text(text = "$dateDelivered",
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
                Text(salesViewModel.salesList[index].customerArea, fontFamily = font_notosans_bold, fontSize = 12.sp,
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
                        Text(text = salesViewModel.salesList[index].customerName, fontFamily = font_notosans_bold, fontSize = 15.sp)
                        Text(text = salesViewModel.salesList[index].contactNumber, fontFamily = font_notosans_bold, fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End
                ){
                    OrderDetails(salesList)

                    val getTotal = salesViewModel.salesList[index].orderBreakdown.sumOf { it.amount * it.quantity }

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
        itemsIndexed(orderList) { index, item ->
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
                    Image(
                        painter = painterResource(
                            id =
                            when (name) {
                                "Red Horse 1000 ML (Mucho)" -> R.drawable.mucho
                                "Red Horse 500 ML" -> R.drawable.redhorse_500
                                "Red Horse 330 ML (Stallion)" -> R.drawable.stallion
                                "Pale Pilsen 1000 ML (Grande)" -> R.drawable.grande
                                "Pale Pilsen 320 ML" -> R.drawable.pilsen_small
                                "San Mig Light 330 ML" -> R.drawable.sanmig_light
                                "San Mig Apple 330 ML" -> R.drawable.sanmig_apple
                                "RC Original Small 240 ML" -> R.drawable.rc_small
                                "RC Orange Small 240 ML" -> R.drawable.orange_small
                                "RC Lemon Small 240 ML" -> R.drawable.lemon_small
                                "RC Root Beer Small 240 ML" -> R.drawable.rootbeer_small
                                "RC Mega Original 800 ML" -> R.drawable.rc_mega
                                "RC Mega Orange 800 ML" -> R.drawable.orange_mega
                                "RC Mega Lemon 800 ML" -> R.drawable.lemon_mega
                                "Cobra Original (Yellow) 240 ML" -> R.drawable.cobra_yellow
                                "Cobra Citrus (Green) 240 ML" -> R.drawable.cobra_green
                                else -> R.drawable.plus
                            }
                        ),
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