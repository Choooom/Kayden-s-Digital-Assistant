package com.example.kaydensdigitalassistant

import android.app.DatePickerDialog
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.data.SalesItem
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ExcelExporter(private val context: Context) {
    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun exportSalesToExcel(
        sales: List<SalesItem>,
        customers: Map<Long, CustomerDetail>,
        products: List<Products>,
        selectedDate: String
    ): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sales Report - $selectedDate")

        // Styles
        val titleStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            fillPattern = FillPatternType.SOLID_FOREGROUND
            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
            borderRight = BorderStyle.THICK
            borderBottom = BorderStyle.THICK
            rightBorderColor = IndexedColors.BLACK.index
            bottomBorderColor = IndexedColors.BLACK.index
        }

        val mergedHeaderStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

        val numberStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.RIGHT
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            dataFormat = workbook.createDataFormat().getFormat("#,##0.00")
        }

        // Title Row
        val titleRow = sheet.createRow(0)
        for (i in 0..4) {
            val cell = titleRow.createCell(i)
            cell.cellStyle = titleStyle
            if (i == 0) cell.setCellValue("Sales Report ($selectedDate)")
        }
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 4))

        // Sales Summary Table Header
        val summaryHeaderRow = sheet.createRow(3)
        val summaryHeaderCell = summaryHeaderRow.createCell(0)
        summaryHeaderCell.setCellValue("Sales Summary")
        summaryHeaderCell.cellStyle = mergedHeaderStyle
        summaryHeaderRow.createCell(1).cellStyle = mergedHeaderStyle
        sheet.addMergedRegion(CellRangeAddress(3, 3, 0, 1))

        // Payment Methods and Totals
        val paymentMethods = listOf("Cash", "GCash", "Consignment")
        paymentMethods.forEachIndexed { index, method ->
            val rowNum = 4 + index
            val row = sheet.createRow(rowNum)

            val methodCell = row.createCell(0)
            methodCell.setCellValue(method)
            methodCell.cellStyle = mergedHeaderStyle

            val methodTotal = sales
                .filter { it.paymentMethod.equals(method, ignoreCase = true) }
                .sumOf { it.totalAmount + it.deposit }

            val totalCell = row.createCell(1)
            totalCell.setCellValue(methodTotal)
            totalCell.cellStyle = numberStyle
        }

        // Grand Total row
        val totalRow = sheet.createRow(7)
        val totalLabelCell = totalRow.createCell(0)
        totalLabelCell.setCellValue("Total")
        totalLabelCell.cellStyle = mergedHeaderStyle

        val grandTotal = sales.sumOf { it.totalAmount + it.deposit }
        val grandTotalCell = totalRow.createCell(1)
        grandTotalCell.setCellValue(grandTotal)
        grandTotalCell.cellStyle = numberStyle

        // Product Inventory Table
        val inventoryHeaderRow = sheet.createRow(10)
        for (i in 0..1) {
            val cell = inventoryHeaderRow.createCell(i)
            cell.cellStyle = mergedHeaderStyle
            if (i == 0) cell.setCellValue("Product Inventory")
        }
        sheet.addMergedRegion(CellRangeAddress(10, 10, 0, 1))

        var currentRow = 11
        products.forEach { product ->
            val row = sheet.createRow(currentRow++)
            row.createCell(0).apply {
                setCellValue(product.productName)
                cellStyle = mergedHeaderStyle
            }
            row.createCell(1).apply {
                setCellValue(product.stock)
                cellStyle = mergedHeaderStyle
            }
        }

        // Order Breakdown Table - Starting from row 3 but column D
        val breakdownHeaderRow = sheet.getRow(3)
        for (i in 3..8) {
            val cell = breakdownHeaderRow.createCell(i)
            cell.cellStyle = mergedHeaderStyle
            if (i == 3) cell.setCellValue("Order Breakdown")
        }
        sheet.addMergedRegion(CellRangeAddress(3, 3, 3, 8))

        // Order Breakdown Headers - Using existing row 4
        val breakdownColumns = listOf("Customer Name", "Address", "Payment Option",
            "Payment Method", "Time", "Total Amount")
        val headerRow = sheet.getRow(4) ?: sheet.createRow(4)

        breakdownColumns.forEachIndexed { index, header ->
            headerRow.createCell(index + 3).apply {
                setCellValue(header)
                cellStyle = mergedHeaderStyle
            }
        }

        var productColumnStart = 3 + breakdownColumns.size

        /*
        products.forEach { product ->
            headerRow.createCell(productColumnStart++).apply {
                setCellValue(product.productName)
                cellStyle = mergedHeaderStyle
            }
        }

        headerRow.createCell(productColumnStart).apply {
            setCellValue("Deposit")
            cellStyle = mergedHeaderStyle
        }
        */
        // Order Breakdown Data - Starting from row 5
        var breakdownRow = 5
        sales.forEach { sale ->
            val customer = customers[sale.customerId]
            val row = sheet.getRow(breakdownRow) ?: sheet.createRow(breakdownRow)
            breakdownRow++
            var col = 3

            listOf(
                customer?.name ?: "Unknown",
                customer?.address ?: "Unknown",
                sale.paymentOption,
                sale.paymentMethod,
                sale.timeDelivered,
                sale.totalAmount + sale.deposit
            ).forEach { value ->
                row.createCell(col++).apply {
                    setCellValue(value.toString())
                    cellStyle = mergedHeaderStyle
                }
            }

            /*
            val productQuantities = sale.orderDetails.associate { it.name to it.quantity }
            products.forEach { product ->
                val quantity = productQuantities[product.productName]
                row.createCell(col++).apply {
                    if (quantity != null && quantity > 0) {
                        setCellValue(quantity.toDouble())
                    }
                    cellStyle = mergedHeaderStyle
                }
            }

            row.createCell(col).apply {
                setCellValue(sale.deposit)
                cellStyle = mergedHeaderStyle
            }

             */

        }

        (0 until productColumnStart + 1).forEach { columnIndex ->
            var maxWidth = 0
            // Get max content width for each row in this column
            for (rowNum in 0..sheet.lastRowNum) {
                val cell = sheet.getRow(rowNum)?.getCell(columnIndex)
                if (cell != null) {
                    val cellValue = cell.toString()
                    maxWidth = maxOf(maxWidth, cellValue.length)
                }
            }
            // Set column width with some padding (256 units = 1 character width)
            sheet.setColumnWidth(columnIndex, (maxWidth + 2) * 256)
        }

        val fileName = "Sales_Report_${selectedDate}.xlsx"
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use {
            workbook.write(it)
        }
        workbook.close()

        return file
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun exportSalesRangeToExcel(
        sales: List<SalesItem>,
        customers: Map<Long, CustomerDetail>,
        products: List<Products>,
        startDate: String,
        endDate: String
    ): File {
        val workbook = XSSFWorkbook()
        // Date formatter to match the format used in sales.timeDelivered (assumed "MM-dd-yyyy")
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        // Parse start and end dates
        val startCal = Calendar.getInstance().apply { time = dateFormat.parse(startDate) }
        val endCal = Calendar.getInstance().apply { time = dateFormat.parse(endDate) }

        // Iterate through the date range (inclusive)
        while (!startCal.after(endCal)) {
            val currentDateStr = dateFormat.format(startCal.time)
            // Filter sales for the current day. (Assumes sale.timeDelivered is in "MM-dd-yyyy" format.)
            val salesForDay = sales.filter { it.timeDelivered == currentDateStr }
            // Create a new sheet for the current day only if there is data
            if (salesForDay.isNotEmpty()) {
                val sheet = workbook.createSheet("Sales Report - $currentDateStr")

                // Styles (reuse same styles as before)
                val titleStyle = workbook.createCellStyle().apply {
                    alignment = HorizontalAlignment.CENTER
                    verticalAlignment = VerticalAlignment.CENTER
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                    fillForegroundColor = IndexedColors.LIGHT_GREEN.index
                    borderRight = BorderStyle.THICK
                    borderBottom = BorderStyle.THICK
                    rightBorderColor = IndexedColors.BLACK.index
                    bottomBorderColor = IndexedColors.BLACK.index
                }

                val mergedHeaderStyle = workbook.createCellStyle().apply {
                    alignment = HorizontalAlignment.CENTER
                    verticalAlignment = VerticalAlignment.CENTER
                    borderTop = BorderStyle.THIN
                    borderBottom = BorderStyle.THIN
                    borderLeft = BorderStyle.THIN
                    borderRight = BorderStyle.THIN
                }

                val numberStyle = workbook.createCellStyle().apply {
                    alignment = HorizontalAlignment.RIGHT
                    borderTop = BorderStyle.THIN
                    borderBottom = BorderStyle.THIN
                    borderLeft = BorderStyle.THIN
                    borderRight = BorderStyle.THIN
                    dataFormat = workbook.createDataFormat().getFormat("#,##0.00")
                }

                // Title Row
                val titleRow = sheet.createRow(0)
                for (i in 0..4) {
                    val cell = titleRow.createCell(i)
                    cell.cellStyle = titleStyle
                    if (i == 0) cell.setCellValue("Sales Report ($currentDateStr)")
                }
                sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 4))

                // Sales Summary Table Header
                val summaryHeaderRow = sheet.createRow(3)
                val summaryHeaderCell = summaryHeaderRow.createCell(0)
                summaryHeaderCell.setCellValue("Sales Summary")
                summaryHeaderCell.cellStyle = mergedHeaderStyle
                summaryHeaderRow.createCell(1).cellStyle = mergedHeaderStyle
                sheet.addMergedRegion(CellRangeAddress(3, 3, 0, 1))

                // Payment Methods and Totals
                val paymentMethods = listOf("Cash", "GCash", "Consignment")
                paymentMethods.forEachIndexed { index, method ->
                    val rowNum = 4 + index
                    val row = sheet.createRow(rowNum)

                    val methodCell = row.createCell(0)
                    methodCell.setCellValue(method)
                    methodCell.cellStyle = mergedHeaderStyle

                    val methodTotal = salesForDay
                        .filter { it.paymentMethod.equals(method, ignoreCase = true) }
                        .sumOf { it.totalAmount + it.deposit }

                    val totalCell = row.createCell(1)
                    totalCell.setCellValue(methodTotal)
                    totalCell.cellStyle = numberStyle
                }

                // Grand Total row
                val totalRow = sheet.createRow(7)
                val totalLabelCell = totalRow.createCell(0)
                totalLabelCell.setCellValue("Total")
                totalLabelCell.cellStyle = mergedHeaderStyle

                val grandTotal = salesForDay.sumOf { it.totalAmount + it.deposit }
                val grandTotalCell = totalRow.createCell(1)
                grandTotalCell.setCellValue(grandTotal)
                grandTotalCell.cellStyle = numberStyle

                // Product Inventory Table
                val inventoryHeaderRow = sheet.createRow(10)
                for (i in 0..1) {
                    val cell = inventoryHeaderRow.createCell(i)
                    cell.cellStyle = mergedHeaderStyle
                    if (i == 0) cell.setCellValue("Product Inventory")
                }
                sheet.addMergedRegion(CellRangeAddress(10, 10, 0, 1))

                var currentRow = 11
                products.forEach { product ->
                    val row = sheet.createRow(currentRow++)
                    row.createCell(0).apply {
                        setCellValue(product.productName)
                        cellStyle = mergedHeaderStyle
                    }
                    row.createCell(1).apply {
                        setCellValue(product.stock)
                        cellStyle = mergedHeaderStyle
                    }
                }

                // Order Breakdown Table - Starting from row 3 but column D
                val breakdownHeaderRow = sheet.getRow(3)
                for (i in 3..8) {
                    val cell = breakdownHeaderRow.createCell(i)
                    cell.cellStyle = mergedHeaderStyle
                    if (i == 3) cell.setCellValue("Order Breakdown")
                }
                sheet.addMergedRegion(CellRangeAddress(3, 3, 3, 8))

                // Order Breakdown Headers - Using existing row 4
                val breakdownColumns = listOf(
                    "Customer Name", "Address", "Payment Option",
                    "Payment Method", "Time", "Total Amount"
                )
                val headerRow = sheet.getRow(4) ?: sheet.createRow(4)
                breakdownColumns.forEachIndexed { index, header ->
                    headerRow.createCell(index + 3).apply {
                        setCellValue(header)
                        cellStyle = mergedHeaderStyle
                    }
                }

                var breakdownRow = 5
                salesForDay.forEach { sale ->
                    val customer = customers[sale.customerId]
                    val row = sheet.getRow(breakdownRow) ?: sheet.createRow(breakdownRow)
                    breakdownRow++
                    var col = 3
                    listOf(
                        customer?.name ?: "Unknown",
                        customer?.address ?: "Unknown",
                        sale.paymentOption,
                        sale.paymentMethod,
                        sale.timeDelivered,
                        sale.totalAmount + sale.deposit
                    ).forEach { value ->
                        row.createCell(col++).apply {
                            setCellValue(value.toString())
                            cellStyle = mergedHeaderStyle
                        }
                    }
                }

                // Auto-size columns
                val productColumnStart = 3 + breakdownColumns.size
                (0 until productColumnStart + 1).forEach { columnIndex ->
                    var maxWidth = 0
                    for (rowNum in 0..sheet.lastRowNum) {
                        val cell = sheet.getRow(rowNum)?.getCell(columnIndex)
                        if (cell != null) {
                            val cellValue = cell.toString()
                            maxWidth = maxOf(maxWidth, cellValue.length)
                        }
                    }
                    sheet.setColumnWidth(columnIndex, (maxWidth + 2) * 256)
                }
            }
            // Move to next day
            startCal.add(Calendar.DAY_OF_MONTH, 1)
        }

        val fileName = "Sales_Report_${startDate}_to_${endDate}.xlsx"
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use {
            workbook.write(it)
        }
        workbook.close()

        return file
    }
}


@Composable
fun CustomDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Use Philippine timezone for the calendar
    val timeZone = TimeZone.getTimeZone("Asia/Manila")
    val calendar = Calendar.getInstance(timeZone)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val context = LocalContext.current

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            // Create a new calendar instance for the selected date using Philippine timezone
            val selectedCalendar = Calendar.getInstance(timeZone)
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            // Format the date to "MM-dd-yyyy"
            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            dateFormat.timeZone = timeZone
            val formattedDate = dateFormat.format(selectedCalendar.time)
            onDateSelected(formattedDate)
        },
        year,
        month,
        day
    ).apply {
        setOnDismissListener { onDismiss() }
    }.show()
}
