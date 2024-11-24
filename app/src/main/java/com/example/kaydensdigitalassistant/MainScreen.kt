package com.example.kaydensdigitalassistant

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

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

    val isAdminLoggedIn = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "selectCustomer", modifier = Modifier.weight(1f)) {

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
                    navController = navController,
                    isAdminLoggedIn = isAdminLoggedIn)
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
            composable("receiptPreview/{paymentOption}/{pricingOption}") { backStackEntry ->
                val paymentOption = backStackEntry.arguments?.getString("paymentOption") ?: "Cash"
                val pricingOption = backStackEntry.arguments?.getString("pricingOption") ?: "Regular"
                ReceiptPreview(navController = navController, paymentOption, pricingOption)
            }

            composable("confirmReceipt"){
                ConfirmPurchase(navController = navController, "")
            }

            composable("inventory"){
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
        }

        if (currentRoute != "login" && currentRoute != "admin_login") {
            BottomNavBar(navController = navController, isAdminLoggedIn.value)
        }
    }
}