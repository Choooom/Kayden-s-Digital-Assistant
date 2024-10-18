package com.example.kaydensdigitalassistant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.ReceiptViewModel
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite


@Composable
fun GenerateReceipt(navController: NavController){
    val insets = WindowInsets.systemBars.asPaddingValues()

    var totalAmount:Double by remember{
        mutableStateOf(0.0)
    }

    var totalAmountDisplay = remember(totalAmount) {
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontFamily = font_archivo_light, fontSize = 15.sp)){
                append("Total Amount: ")
            }
            withStyle(style = SpanStyle(
                fontFamily = font_archivo_bold,
                fontSize = 17.sp
            )) {
                append(totalAmount.toString())
            }
        }
    }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "RECEIPT",
                fontFamily = font_archivo,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.98f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.9f)
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(dirtyWhite),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReceiptSection { onTotalAmountCalculated ->
                    totalAmount = onTotalAmountCalculated
                }
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Product",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(bottom = 10.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = {

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
            ){
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
                        modifier = Modifier.fillMaxSize()
                                    .padding(0.dp),
                       horizontalArrangement = Arrangement.Center,
                       verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Proceed",
                            fontFamily = font_notosans_bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptSection(receiptViewModel: ReceiptViewModel = viewModel(), onTotalAmountCalculated: (Double) -> Unit) {
    val receiptItemsState = remember {receiptViewModel.receiptItemsState}

    val totalAmount by remember {
        derivedStateOf {
            receiptViewModel.getTotalAmount()
        }
    }

    LaunchedEffect(totalAmount) {
        onTotalAmountCalculated(totalAmount)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(top = 10.dp)
    ) {
        itemsIndexed(receiptItemsState) { index, item ->
            ProductItem(item.name, item.amount, item.quantity, index) { indexToRemove ->
                receiptViewModel.removeReceiptItem(indexToRemove)
            }
        }
    }
}

@Composable
fun ProductItem(
    name: String,
    amount: Double,
    quantity: Double,
    index: Int,
    onDelete: (Int) -> Unit
){

    var isLongPressed by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isLongPressed) Color.White.copy(alpha = 0.2f) else Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isLongPressed = false
                    },
                    onLongPress = {
                        isLongPressed = !isLongPressed
                    }
                )
            },
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.05f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueStart)
            )
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .height(70.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(dirtyWhite),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                if(name == "Red Horse 1000 ML (Mucho)"){
                    Image(
                        painter = painterResource(id = R.drawable.mucho),
                        contentDescription = "Red Horse Mucho",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }else if(name == "Red Horse 500 ML"){
                    Image(
                        painter = painterResource(id = R.drawable.redhorse_500),
                        contentDescription = "Red Horse Mucho",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }else if(name == "RC Original Small 240 ML"){
                    Image(
                        painter = painterResource(id = R.drawable.rc_small),
                        contentDescription = "Red Horse Mucho",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }else if(name == "RC Lemon Small 240 ML"){
                    Image(
                        painter = painterResource(id = R.drawable.lemon_small),
                        contentDescription = "Red Horse Mucho",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 19.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom,

                ){
                    Text(
                        name,
                        style = TextStyle(
                            lineHeight = 15.sp
                        ),
                        fontFamily = font_notosans_bold,
                        fontSize = 10.sp)
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(dirtyWhite)
                        .height(1.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom,
                ){
                    Text(
                        text = buildAnnotatedString {
                            append("Amount: ")

                            withStyle(SpanStyle(fontFamily = font_notosans_bold)){
                                append(amount.toString())
                            }
                        },
                        fontFamily = font_notosans_regular,
                        fontSize = 10.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom,
                ){
                    Text(
                        text = buildAnnotatedString {
                            append("Quantity: ")

                            withStyle(SpanStyle(fontFamily = font_notosans_bold, background = dirtyWhite)){
                                append("  ${quantity.toString()}  ")
                            }
                        },
                        fontFamily = font_notosans_regular,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .padding(end = 5.dp)
                    )
                }
            }
        }
        if(isLongPressed){
            Icon(
                painter = painterResource(id = R.drawable.alpha_x_box),
                contentDescription = "Delete Product",
                tint = Color.Red,
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 10.dp, top = 10.dp)
                    .clickable {
                        onDelete(index)
                    },
            )
        }
    }
}
