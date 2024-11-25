package com.example.kaydensdigitalassistant

import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.TextUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaydensdigitalassistant.data.SalesItemViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.legend.horizontalLegend
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.text.VerticalPosition

@Composable
fun TodayVsYesterdayChart(viewModel: SalesItemViewModel) {
    val data by viewModel.getTodayVsYesterdaySales().collectAsState(initial = Pair(0.0, 0.0))

    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Today vs Yesterday", style = MaterialTheme.typography.headlineLarge)
        Chart(
            chart = columnChart(
                columns = listOf(
                    lineComponent(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 8.dp
                    ),
                    lineComponent(
                        color = MaterialTheme.colorScheme.secondary,
                        thickness = 8.dp
                    )
                )
            ),
            model = entryModelOf(0f to data.first.toFloat(), 1f to data.second.toFloat()),
            startAxis = startAxis(),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    when (value.toInt()) {
                        0 -> "Today"
                        1 -> "Yesterday"
                        else -> ""
                    }
                }
            ),
            legend = verticalLegend(
                items = listOf(
                    legendItem(
                        icon = shapeComponent(
                            shape = Shapes.pillShape,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        label = textComponent(
                            color = Color.Black,
                            textSize = 12.sp,
                            typeface = Typeface.DEFAULT,
                            background = null,
                            ellipsize = TextUtils.TruncateAt.END,
                            lineCount = 1,
                            textAlignment = Layout.Alignment.ALIGN_NORMAL
                        ),
                        labelText = "Total Sales"
                    ),
                ),
                iconSize = 8.dp,
                iconPadding = 4.dp
            )
        )
    }
}


@Composable
fun ProfitChart(viewModel: SalesItemViewModel) {
    val data by viewModel.getProfit().collectAsState(initial = emptyList())

    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Text("Profit Trend", style = MaterialTheme.typography.headlineLarge)
        Chart(
            chart = lineChart(
                lines = listOf(
                    LineChart.LineSpec(
                        lineColor = MaterialTheme.colorScheme.tertiary.toArgb(),
                        lineThicknessDp = 4f,
                        lineCap = Paint.Cap.ROUND,
                        pointSizeDp = 8f,
                        dataLabelVerticalPosition = VerticalPosition.Top,
                        dataLabelRotationDegrees = 0f
                    )
                )
            ),
            model = entryModelOf(*data.mapIndexed { index, (_, profit) ->
                index.toFloat() to profit.toFloat()
            }.toTypedArray()),
            startAxis = startAxis(),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    when (value.toInt()) {
                        0 -> "Today"
                        1 -> "Yesterday"
                        else -> ""
                    }
                }
            ),
            legend = verticalLegend(
                items = listOf(
                    legendItem(
                        icon = shapeComponent(
                            shape = Shapes.pillShape,
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        label = textComponent(
                            color = Color.Black,
                            textSize = 12.sp,
                            typeface = Typeface.DEFAULT,
                            background = null,
                            ellipsize = TextUtils.TruncateAt.END,
                            lineCount = 1,
                            textAlignment = Layout.Alignment.ALIGN_NORMAL
                        ),
                        labelText = "Daily Profit"
                    )
                ),
                iconSize = 8.dp,
                iconPadding = 4.dp
            )
        )
    }
}

@Composable
fun NumberOfSalesChart(viewModel: SalesItemViewModel) {
    val data by viewModel.getNumberOfSales().collectAsState(initial = emptyList())

    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Text("Number of Sales", style = MaterialTheme.typography.headlineLarge)
        Chart(
            chart = columnChart(
                columns = listOf(
                    lineComponent(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 8.dp
                    )
                )
            ),
            model = entryModelOf(*data.mapIndexed { index, (_, count) ->
                index.toFloat() to count.toFloat()
            }.toTypedArray()),
            startAxis = startAxis(),
            bottomAxis = bottomAxis(
                valueFormatter = { value, _ ->
                    when (value.toInt()) {
                        0 -> "Today"
                        1 -> "Yesterday"
                        else -> ""
                    }
                }
            ),
            legend = verticalLegend(
                items = listOf(
                    legendItem(
                        icon = shapeComponent(
                            shape = Shapes.pillShape,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        label = textComponent(
                            color = Color.Black,
                            textSize = 12.sp,
                            typeface = Typeface.DEFAULT,
                            background = null,
                            ellipsize = TextUtils.TruncateAt.END,
                            lineCount = 1,
                            textAlignment = Layout.Alignment.ALIGN_NORMAL
                        ),
                        labelText = "Sales Count"
                    )
                ),
                iconSize = 8.dp,
                iconPadding = 4.dp
            )
        )
    }
}




