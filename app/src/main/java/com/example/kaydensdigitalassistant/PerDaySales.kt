package com.example.kaydensdigitalassistant

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.font_archivo_bold
import com.example.kaydensdigitalassistant.kanit_bold

@Composable
fun PerDaySale(navController: NavController) {
    val insets = WindowInsets.systemBars.asPaddingValues()

    // Accessing the SalesViewModel via LocalSalesViewModel.current
    val salesViewModel = LocalSalesViewModel.current

    // Collecting the salesFlow from the SalesViewModel
    val salesList = salesViewModel.selectedSalesItem

    // Calculating the total sales amount and number of sales from the fetched salesList
    val totalSalesAmount = remember { mutableStateOf(0.0) }
    val numberOfSales = remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = insets.calculateTopPadding())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.43f)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(
                    Brush.linearGradient(colors = listOf(BlueStart, BlueEnd))
                )
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
            ) {
                Text(
                    text = "Sales", fontFamily = kanit_bold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painterResource(id = R.drawable.face_man), contentDescription = "Profile",
                    modifier = Modifier.size(70.dp).clickable { }.align(Alignment.CenterEnd)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 30.dp, top = 10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Per Day Sales",
                    fontFamily = font_archivo_bold,
                    fontSize = 25.sp,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 30.dp, top = 10.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "₱ ${totalSalesAmount.value}",
                    fontFamily = font_archivo_bold,
                    fontSize = 45.sp,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    .padding(start = 30.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sales vs Yesterday Column
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.33f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(id = R.drawable.calendar_month),
                        contentDescription = "Sales Vs. Yesterday",
                        modifier =
                        Modifier.size(70.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(
                        text = "Sales Vs. Yesterday",
                        fontFamily = font_archivo_bold,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$numberOfSales",
                        fontFamily = font_archivo_bold,
                        fontSize = 20.sp,
                        color =
                        if (numberOfSales.value > 20) ButtonGreen
                        else if (numberOfSales.value in 10..19) Color.Yellow
                        else Color.Red
                    )
                }

                // Profit Column
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(id = R.drawable.money_bag),
                        contentDescription = "Profit",
                        modifier =
                        Modifier.size(70.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(
                        text = "Profit",
                        fontFamily = font_archivo_bold,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$numberOfSales",
                        fontFamily = font_archivo_bold,
                        fontSize = 20.sp,
                        color =
                        if (numberOfSales.value > 20) ButtonGreen
                        else if (numberOfSales.value in 10..19) Color.Yellow
                        else Color.Red
                    )
                }

                // Number of Sales Column
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painterResource(id = R.drawable.sales),
                        contentDescription = "Number of Sales",
                        modifier =
                        Modifier.size(70.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(
                        text = "Number of Sales",
                        fontFamily = font_archivo_bold,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$numberOfSales",
                        fontFamily = font_archivo_bold,
                        fontSize = 20.sp,
                        color =
                        if (numberOfSales.value > 20) ButtonGreen
                        else if (numberOfSales.value in 10..19) Color.Yellow
                        else Color.Red
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.97f)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Brush.linearGradient(colors = listOf(BlueStart, BlueEnd)))
        )
    }
}
