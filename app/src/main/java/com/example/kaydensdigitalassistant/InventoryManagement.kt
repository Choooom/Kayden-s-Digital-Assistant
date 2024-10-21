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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.Orange
import com.example.kaydensdigitalassistant.ui.theme.Red
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite


@Composable
fun Inventory(navController: NavController){
    val inventory = LocalReceiptViewModel.current
    val inventoryList = inventory.productList

    val insets = WindowInsets.systemBars.asPaddingValues()
    var itemType by remember{ mutableStateOf("Beer")}
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(Brush.horizontalGradient(colors = listOf(BlueStart, BlueEnd))),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(modifier = Modifier.fillMaxWidth().padding(0.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
            Text(text = "INVENTORY",
                fontFamily = kanit_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White)
            Spacer(modifier = Modifier.fillMaxWidth(0.21f))
            Icon(painter = painterResource(id = R.drawable.face_man)
                , contentDescription = "Profile",
                modifier = Modifier.size(70.dp).padding(end = 15.dp).clickable {  },)
        }

        Row(modifier = Modifier.fillMaxWidth(0.98f)
            .height(70.dp).padding(top = 20.dp).clip(RoundedCornerShape(5.dp)).background(Color.White), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically){
            Text(text = "All Items >",
                color = Color.Gray,
                fontFamily = font_abeezee,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 15.dp)
            )

            Text(text = itemType, fontFamily = font_archivo_bold, fontSize = 14.sp, modifier = Modifier.padding(start = 5.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.98f)
                .fillMaxHeight(0.98f)
                .padding(top = 5.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(45.dp)
                    .border(1.dp, dirtyWhite),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    onSearch = {

                    },
                    modifier = Modifier.fillMaxWidth(0.5f)
                        .height(25.dp)
                        .padding(start = 10.dp)
                        .border(1.dp, Color.Black,RoundedCornerShape(3.dp))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.98f)
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(dirtyWhite),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(0.7f)
                        .height(60.dp)
                        .padding(top = 15.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painter = painterResource(id = R.drawable.beers)
                        , contentDescription = "Beers",
                        modifier = Modifier
                            .clickable { itemType = "Beer" }
                            .size(30.dp),
                        tint = if(itemType == "Beer") Color.Black else Color.Gray
                    )
                    Icon(painter = painterResource(id = R.drawable.softdrink)
                        , contentDescription = "Softdrink",
                        modifier = Modifier
                            .clickable { itemType = "Softdrink" }
                            .size(30.dp),
                        tint = if(itemType == "Softdrink") Color.Black else Color.Gray
                    )
                    Icon(painter = painterResource(id = R.drawable.energy_drink)
                        , contentDescription = "Energy-Drink",
                        modifier = Modifier
                            .clickable { itemType = "Energy-Drink" }
                            .size(30.dp),
                        tint = if(itemType == "Energy-Drink") Color.Black else Color.Gray
                    )
                    Icon(painter = painterResource(id = R.drawable.milk)
                        , contentDescription = "ETC",
                    modifier = Modifier
                        .clickable { itemType = "BeerFlavored" }
                        .size(30.dp),
                        tint = if(itemType == "BeerFlavored") Color.Black else Color.Gray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(0.9f).padding(top = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "ITEMS: 8", fontFamily = font_archivo_bold, fontSize = 11.sp)
                    Text(text = "TOTAL UNITS: 87", fontFamily = font_archivo_bold, fontSize = 11.sp)
                    Text(text = "TOTAL VALUE: 12,562", fontFamily = font_archivo_bold, fontSize = 11.sp)
                }
                InventorySection(itemType)
            }
        }
    }
}

@Composable
fun InventorySection(condition: String) {
    val inventoryViewModel = LocalReceiptViewModel.current
    val inventoryList = inventoryViewModel.productList

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.9f)
            .padding(top = 10.dp)
    ) {
        itemsIndexed(inventoryList) { index, item ->
            if(condition == item.type) InventoryItem(item.name, item.price, item.stock)
        }
    }
}


@Composable
fun InventoryItem(
    name: String,
    amount: Double,
    quantity: Double,
) {

    val backgroundColor = when {
        quantity >= 50 -> ButtonGreen
        quantity < 50 && amount >= 10 -> Orange
        else -> Red
    }

    val inventory = LocalReceiptViewModel.current
    val inventoryItems = inventory.productList

    var isEmpty by remember { mutableStateOf(false) }

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
                        .width(80.dp)
                        .fillMaxHeight()
                        .drawBehind {
                            val borderSize = 1.dp.toPx()
                            drawLine(
                                color = dirtyWhite,
                                start = Offset(size.width - borderSize / 2, 0f),
                                end = Offset(size.width - borderSize / 2, size.height),
                                strokeWidth = borderSize
                            )
                        },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.65f)
                            .padding(start = 5.dp, top = 15.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
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
                                .fillMaxWidth()
                                .background(dirtyWhite)
                                .height(1.dp)
                        )
                        Spacer(modifier = Modifier.fillMaxHeight(0.55f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.97f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(dirtyWhite),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(
                                text = "Stocks Available: $quantity Units",
                                fontFamily = font_notosans_bold,
                                fontSize = 7.sp
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(0.2f),
                            verticalAlignment = Alignment.Top
                        ){
                            Text(text = "Status: " + if(quantity > 0) "Available" else "Not Available", fontFamily = font_notosans_bold, fontSize = 5.sp, color = Color.Gray)
                            Canvas(modifier = Modifier.size(15.dp).padding(top = 7.dp)) {
                                drawCircle(
                                    color = backgroundColor,
                                    radius = size.minDimension / 2
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(0.69f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            var text = if(quantity > 0) "IN STOCK" else "OUT OF STOCK"
                            Text(text = text, fontFamily = font_archivo_bold, fontSize = 11.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight()
                                .drawBehind {
                                    val borderWidth = 1.dp.toPx()
                                    drawLine(
                                        color = dirtyWhite,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = borderWidth
                                    )
                                },
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ){
                            Text(text = "Price: ₱$amount", fontFamily = font_archivo_bold, fontSize = 7.sp,
                                modifier = Modifier.padding(start = 2.dp))
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(125.dp)
                .width(14.dp)
                .padding(start = 7.dp, top = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor)
        )
    }
}