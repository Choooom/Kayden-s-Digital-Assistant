package com.example.kaydensdigitalassistant.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.room.util.copy
import com.example.kaydensdigitalassistant.GenerateReceipt
import com.example.kaydensdigitalassistant.LocalCustomerViewModel


class ReceiptViewModel  : ViewModel() {
    var receiptItemsState = mutableStateListOf<ReceiptItem>()
    val productList = mutableStateListOf<InventoryItems>()

    var showProductList by mutableStateOf(false)
    var currentIndex by mutableStateOf(0)

    var toClear by mutableStateOf(false)

    companion object {
        var isCustomerSelected by mutableStateOf(false)

        fun customerPickedCallback(resetSelection: Boolean = false) {
            if (resetSelection) {
                isCustomerSelected = false
            } else {
                isCustomerSelected = true
            }
        }
    }

    init {
        productList.addAll(listOf(
            // Beer
            InventoryItems("Red Horse 1000 ML (Mucho)", "Beer", 640.0, 100.0),
            InventoryItems("Red Horse 500 ML", "Beer", 628.0, 100.0),
            InventoryItems("Red Horse 330 ML (Stallion)", "Beer", 945.0, 11.0),
            InventoryItems("Pale Pilsen 1000 ML (Grande)", "Beer", 610.0, 100.0),
            InventoryItems("Pale Pilsen 320 ML", "Beer", 880.0, 10.0),
            InventoryItems("San Mig Light 330 ML", "BeerFlavored", 1035.0, 100.0),
            InventoryItems("San Mig Apple 330 ML", "BeerFlavored", 860.0, 100.0),
            // Softdrink
            InventoryItems("RC Original Small 240 ML", "Softdrink", 174.0, 100.0),
            InventoryItems("RC Orange Small 240 ML", "Softdrink", 174.0, 340.0),
            InventoryItems("RC Lemon Small 240 ML", "Softdrink", 174.0, 60.0),
            InventoryItems("RC Root Beer Small 240 ML", "Softdrink", 174.0, 100.0),
            InventoryItems("RC Mega Original 800 ML", "Softdrink", 260.0, 40.0),
            InventoryItems("RC Mega Orange 800 ML", "Softdrink", 260.0, 2.0),
            InventoryItems("RC Mega Lemon 800 ML", "Softdrink", 260.0, 100.0),
            // Energy Drinks
            InventoryItems("Cobra Original (Yellow) 240 ML", "Energy-Drink", 300.0, 10.0),
            InventoryItems("Cobra Citrus (Green) 240 ML", "Energy-Drink", 300.0, 23.0),
        ))

        // Add an empty item initially
        receiptItemsState.add(
            ReceiptItem("", "", 0.0, 0.0)
        )
    }

    fun addReceiptItem(item: ReceiptItem) {
        receiptItemsState.add(item)
    }

    fun removeReceiptItem(index: Int) {
        receiptItemsState.removeAt(index)
    }

    fun addQuantity(quantity: Double, index: Int) {
        if (index in receiptItemsState.indices) {
            receiptItemsState[index] = receiptItemsState[index].copy(quantity = quantity)
        }
        println(getReceiptList())
    }

    fun subtractQuantity(quantity: Double, index: Int) {
        if (index in receiptItemsState.indices) {
            if (receiptItemsState[index].quantity >= quantity) {
                receiptItemsState[index] = receiptItemsState[index].copy(quantity = quantity)
            } else {
                receiptItemsState[index] = receiptItemsState[index].copy(quantity = 0.0)
            }
        }
    }

    fun addItem(product: String, type: String) {
        if (currentIndex in receiptItemsState.indices) {
            val productItem = productList.find { it.name == product && it.type == type }
            if (productItem != null) {
                val currentItem = receiptItemsState[currentIndex]
                val newReceiptItemsState = receiptItemsState.toMutableList()
                receiptItemsState[currentIndex] = receiptItemsState[currentIndex].copy(
                    name = productItem.name,
                    type = productItem.type,
                    amount = productItem.price,
                    quantity = 0.0
                )
            }
        }
        println(receiptItemsState[currentIndex])
        println(getReceiptList())
    }

    fun getTotalAmount(): Double {
        return receiptItemsState.sumOf { it.amount * it.quantity }
    }

    fun addProductItem(item: ReceiptItem) {
        receiptItemsState.add(item)
    }

    fun getReceiptList(): List<ReceiptItem> {
        return receiptItemsState
    }

    fun removeProductItem(index: Int) {
        receiptItemsState.removeAt(index)
    }

    fun clearReceiptItems() {
        receiptItemsState.clear()
        receiptItemsState.add(ReceiptItem("", "", 0.0, 0.0))
    }

    fun updateInventory(viewModel: ReceiptViewModel){
        val inventory = viewModel.productList
        val receipt = viewModel.receiptItemsState
        println(receipt[1].name)

        for (receiptItem in receipt) {

            val matchingInventoryItem = inventory.firstOrNull { it.name == receiptItem.name }

            matchingInventoryItem?.let {
                it.stock -= receiptItem.quantity
                println("Product: ${it.name}, Stock: ${it.stock}")
            }
        }
    }
}