package com.example.kaydensdigitalassistant

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

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

// Add this extension function at the top level of your file
fun Double.safeRoundToInt(): Int {
    return if (this.isNaN() || this.isInfinite()) 0 else this.toInt()
}

fun Float.safeRoundToInt(): Int {
    return if (this.isNaN() || this.isInfinite()) 0 else this.toInt()
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val userRoleViewModel = LocalUserRoleViewModel.current
    val isAdmin by userRoleViewModel.isAdmin.collectAsState()

        Column(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "login", modifier = Modifier.weight(1f)) {

                composable("login") {
                    LogIn(
                        modifier = Modifier.padding(0.dp),
                        backgroundColor = Color.White,
                        navController = navController
                    )
                }
                composable("admin_login") {
                    Admin_LogIn(
                        modifier = Modifier.padding(0.dp),
                        backgroundColor = Color.White,
                        navController = navController)
                }
                composable("home") {
                    HomeScreen(navController = navController)
                }
                composable("receipt") {
                    GenerateReceipt(
                        navController = navController,
                    )
                }
                composable("selectCustomer") {
                    SelectCustomer(
                        navController = navController)
                }
                composable("productList") {
                    ProductList(navController = navController){}
                }
                composable("salesTracking") {
                    SalesTracking(navController = navController)
                }
                composable(
                    "receiptPreview/{paymentOption}/{pricingOption}/{deposit}",
                    arguments = listOf(
                        navArgument("deposit") { type = NavType.FloatType }
                    )
                ) { backStackEntry ->
                    val paymentOption = backStackEntry.arguments?.getString("paymentOption") ?: "Cash"
                    val pricingOption = backStackEntry.arguments?.getString("pricingOption") ?: "Regular"
                    val deposit = backStackEntry.arguments?.getFloat("deposit")?.toDouble() ?: 0.0
                    ReceiptPreview(navController = navController, paymentOption, pricingOption, deposit)
                }

                composable("confirmReceipt"){
                    ConfirmPurchase(navController = navController, "","", "", 0.0)
                }

                composable(
                    "inventory",
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) {
                    Inventory(navController = navController)
                }

                composable("addAccount"){
                    AddUserAccount(navController = navController)
                }
                composable("salesReport"){
                    PerDaySale(navController = navController)
                }

                composable("addNewAccount"){
                    AddNewAccount(navController = navController)
                }

                composable("numberVerification"){
                    NumberVerification(navController = navController)
                }

                composable("userVerification"){
                    UserVerification(navController = navController)
                }

                composable("manageAccounts"){
                    ManageAccount(navController = navController)
                }

                composable("numberVerification"){
                    NumberVerification(navController = navController)
                }
                composable("otp"){
                    UserVerification(navController = navController)
                }
                composable("success"){
                    SuccessScreen(navController = navController)
                }

                composable("emailVerification"){
                    EmailVerification(navController = navController)
                }

                composable("verifyEmail"){
                    VerifyEmailScreen(navController = navController)
                }

                composable("resetPassword") {
                    PasswordResetScreen(navController = navController)
                }

                composable(
                    "map/{customerId}",
                    arguments = listOf(navArgument("customerId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                    MapScreen(customerId = customerId)
                }

                composable(
                    "map/{customerId}",
                    arguments = listOf(navArgument("customerId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                    CustomerLocationScreen(navController = navController, customerId = customerId)
                }

                composable(
                    "maps/{customerId}",
                    arguments = listOf(navArgument("customerId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                    CustomerLocationViewScreen(navController = navController, customerId = customerId)
                }
            }

            if (currentRoute != "login" && currentRoute != "admin_login" && currentRoute != "resetPassword"){
                BottomNavBar(navController = navController, isAdmin)
            }
        }
}