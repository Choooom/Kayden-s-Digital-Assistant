package com.example.kaydensdigitalassistant.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel


class ReceiptViewModel : ViewModel() {
    val receiptItemsState = mutableStateListOf<ReceiptItem>()
    val productList = mutableStateListOf<ReceiptItem>()

    init {
        productList.addAll(listOf(
            //Beer
            ReceiptItem("Red Horse 1000 ML (Mucho)", "Beer", 640.0, 0.0),
            ReceiptItem("Red Horse 500 ML", "Beer", 628.0, 0.0),
            ReceiptItem("Red Horse 330 ML (Stallion)", "Beer", 945.0, 0.0),
            ReceiptItem("Pale Pilsen 1000 ML (Grande)", "Beer", 610.0, 0.0),
            ReceiptItem("Pale Pilsen 320 ML", "Beer", 880.0, 0.0),
            ReceiptItem("San Mig Light 330 ML", "Beer", 1035.0, 0.0),
            ReceiptItem("San Mig Apple 330 ML", "Beer", 860.0, 0.0),
            //Softdrink
            ReceiptItem("RC Original Small 240 ML", "Softdrink", 174.0, 0.0),
            ReceiptItem("RC Orange Small 240 ML", "Softdrink", 174.0, 0.0),
            ReceiptItem("RC Lemon Small 240 ML", "Softdrink", 174.0, 0.0),
            ReceiptItem("RC Root Beer Small 240 ML", "Softdrink", 174.0, 0.0),
            ReceiptItem("RC Mega Original 800 ML", "Softdrink", 260.0, 0.0),
            ReceiptItem("RC Mega Orange 800 ML", "Softdrink", 260.0, 0.0),
            ReceiptItem("RC Mega Lemon 800 ML", "Softdrink", 260.0, 0.0),
            //Energy Drinks
            ReceiptItem("Cobra Original (Yellow) 240 ML", "Energy-Drink", 300.0, 0.0),
            ReceiptItem("Cobra Citrus (Green) 240 ML", "Energy-Drink", 300.0, 0.0),
        ))
    }

    fun addReceiptItem(item: ReceiptItem) {
        receiptItemsState.add(item)
    }

    fun removeReceiptItem(index: Int) {
        receiptItemsState.removeAt(index)
    }

    fun getTotalAmount(): Double {
        return receiptItemsState.sumOf { it.price }
    }
}