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

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Use Column to manage layout
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
                Admin_LogIn(modifier = Modifier.padding(0.dp),
                    backgroundColor = Color.White,
                    navController = navController)
            }
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("receipt") {
                GenerateReceipt(navController = navController)
            }
            composable("productList") {
                ProductList(navController = navController)
            }
        }

        // Add BottomNavBar at the bottom of the screen
        if (currentRoute != "login" && currentRoute != "admin_login") {
            BottomNavBar(navController = navController)
        }
    }
}