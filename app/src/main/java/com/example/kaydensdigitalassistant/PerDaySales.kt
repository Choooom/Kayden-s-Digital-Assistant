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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import com.example.kaydensdigitalassistant.font_archivo_bold
import com.example.kaydensdigitalassistant.kanit_bold
import java.text.DecimalFormat

@Composable
fun PerDaySale(navController: NavController) {
    val insets = WindowInsets.systemBars.asPaddingValues()


    val salesViewModel = LocalSalesViewModel.current

    val salesList = salesViewModel.selectedSalesItem

    val formatter = DecimalFormat("#,###.0")

    var showTodayVsYesterdayChart by remember { mutableStateOf(false) }
    var showProfitChart by remember { mutableStateOf(false) }
    var showNumberOfSalesChart by remember { mutableStateOf(false) }
    var showProductSalesChart by remember { mutableStateOf(false) }

    val todayVsYesterdayData by salesViewModel.getTodayVsYesterdaySales().collectAsState(initial = Pair(0.0, 0.0))
    val salesDifference = (todayVsYesterdayData.first - todayVsYesterdayData.second).toInt()

    val profitData by salesViewModel.getProfit().collectAsState(initial = emptyList())
    val totalProfit = profitData.firstOrNull()?.second?.toInt() ?: 0

// Number of Sales
    val salesCountData by salesViewModel.getNumberOfSales().collectAsState(initial = emptyList())
    val totalSalesCount = salesCountData.firstOrNull()?.second ?: 0

    val totalSales by salesViewModel.allSalesItems.observeAsState(initial = emptyList())
    val totalSalesAmount = totalSales.sumOf { it.totalAmount }



    Box(modifier = Modifier.fillMaxSize()) {
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
                TopBar(navController = navController, "Stats")

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Total Sales: ",
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
                        text = "₱ ${formatter.format(totalSalesAmount.toInt())}",
                        fontFamily = font_archivo_bold,
                        fontSize = 45.sp,
                        color = Color.White,
                        modifier = Modifier.clickable { showProductSalesChart = true}
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                        .padding(start = 30.dp, end = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            modifier = Modifier
                                .size(70.dp)
                                .clickable { showTodayVsYesterdayChart = true },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Text(
                            text = "Sales Vs. Yesterday",
                            fontFamily = font_archivo_bold,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                        Text(
                            text = formatter.format(salesDifference),
                            fontFamily = font_archivo_bold,
                            fontSize = 20.sp,
                            color =
                            if (salesDifference > 20) Color.Green
                            else if (salesDifference in 10..19) Color.Yellow
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
                            modifier = Modifier
                                .size(70.dp)
                                .clickable { showProfitChart = true },
                            colorFilter = ColorFilter.tint(Color.White)
                        )

                        Text(
                            text = "Profit",
                            fontFamily = font_archivo_bold,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                        Text(
                            text = formatter.format(totalProfit),
                            fontFamily = font_archivo_bold,
                            fontSize = 20.sp,
                            color =
                            if (totalProfit > 20) Color.Green
                            else if (totalProfit in 10..19) Color.Yellow
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
                            modifier = Modifier
                                .size(70.dp)
                                .clickable { showNumberOfSalesChart = true },
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Text(
                            text = "Number of Sales",
                            fontFamily = font_archivo_bold,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                        Text(
                            text = "$totalSalesCount",
                            fontFamily = font_archivo_bold,
                            fontSize = 20.sp,
                            color =
                            if (totalSalesCount > 20) Color.Green
                            else if (totalSalesCount in 10..19) Color.Yellow
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
                val salesViewModel = LocalSalesViewModel.current
                val salesItems by salesViewModel.allSalesItems.observeAsState(initial = emptyList())

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(salesItems) { sale ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ButtonGreen)
                                    .padding(16.dp)
                            ) {
                                item {
                                    Column {
                                        Text(
                                            text = "Date: ${sale.dateDelivered}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Time: ${sale.timeDelivered}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Amount: ₱${sale.totalAmount}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Payment: ${sale.paymentMethod}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "Order Details:",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        sale.orderDetails.forEach { item ->
                                            Text(
                                                text = "${item.quantity}x ${item.name}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Brush.linearGradient(colors = listOf(BlueStart, BlueEnd)))
            )
        }
        if (showTodayVsYesterdayChart) {
            Dialog(onDismissRequest = { showTodayVsYesterdayChart = false }) {
                Surface(
                    modifier = Modifier
                        .width(300.dp)
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    TodayVsYesterdayChart(salesViewModel)
                }
            }
        }

        if (showProfitChart) {
            Dialog(onDismissRequest = { showProfitChart = false }) {
                Surface(
                    modifier = Modifier
                        .width(300.dp)
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ProfitChart(salesViewModel)
                }
            }
        }

        if (showNumberOfSalesChart) {
            Dialog(onDismissRequest = { showNumberOfSalesChart = false }) {
                Surface(
                    modifier = Modifier
                        .width(300.dp)
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    NumberOfSalesChart(salesViewModel)
                }
            }
        }

        if(showProductSalesChart){
            Dialog(onDismissRequest = { showProductSalesChart = false }) {
                Surface(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ProductSalesChart(salesViewModel)
                }
            }
        }
    }
}
