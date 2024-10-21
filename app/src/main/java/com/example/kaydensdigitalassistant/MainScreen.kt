package com.example.kaydensdigitalassistant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kaydensdigitalassistant.BottomNavBar
import androidx.compose.runtime.LaunchedEffect

object CustomerSelectionHelper {
    var isCustomerSelected by mutableStateOf(false)

    fun customerPickedCallback(resetSelection: Boolean = false) {
        if (resetSelection) {
            isCustomerSelected = false
        } else {
            isCustomerSelected = true
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    var isCustomerSelected by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "receipt", modifier = Modifier.weight(1f)) {

            composable("login") {
                LogIn(
                    modifier = Modifier.padding(0.dp),
                    backgroundColor = Color.White,
                    navController = navController
                )
            }
            composable("admin_login") {
                Admin_LogIn(modifier = Modifier.padding(0.dp),
                    backgroundColor = Color.White,
                    navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("receipt") {
                GenerateReceipt(
                    navController = navController,
                    customerPicked = CustomerSelectionHelper::customerPickedCallback,
                    isCustomerSelected = CustomerSelectionHelper.isCustomerSelected
                )
            }
            composable("selectCustomer") {
                SelectCustomer(
                    navController = navController,
                    customerPicked = {
                        CustomerSelectionHelper.customerPickedCallback()
                        navController.popBackStack()
                    }
                )
            }
            composable("productList") {
                ProductList(navController = navController){}
            }
            composable("receiptPreview/{paymentOption}/{pricingOption}") { backStackEntry ->
                val paymentOption = backStackEntry.arguments?.getString("paymentOption") ?: "Cash"
                val pricingOption = backStackEntry.arguments?.getString("pricingOption") ?: "Regular"
                ReceiptPreview(navController = navController, paymentOption, pricingOption)
            }

            composable("confirmReceipt"){
                ConfirmPurchase(navController = navController, "")
            }

        }

        if (currentRoute != "login" && currentRoute != "admin_login") {
            BottomNavBar(navController = navController)
        }
    }
    LaunchedEffect(key1 = CustomerSelectionHelper.isCustomerSelected) {
        if (!CustomerSelectionHelper.isCustomerSelected) {
            navController.navigate("selectCustomer")
        }
    }
}