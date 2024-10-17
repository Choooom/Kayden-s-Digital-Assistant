package com.example.kaydensdigitalassistant

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LogIn(modifier = Modifier.padding(0.dp),
                backgroundColor = Color.White, navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("receipt"){
            GenerateReceipt(navController = navController)
        }
        composable("productList"){
            ProductList(navController = navController)
        }
    }
}