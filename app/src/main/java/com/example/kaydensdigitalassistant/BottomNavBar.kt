package com.example.kaydensdigitalassistant

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kaydensdigitalassistant.data.BottomNavItem
import com.example.kaydensdigitalassistant.ui.theme.SkyBlue

@Composable
fun BottomNavBar(navController: NavController, isAdminLoggedIn: Boolean) {
    val bottomNavItems = mutableListOf(
        BottomNavItem("Sales", ImageVector.vectorResource(id = R.drawable.sales), "salesTracking"),
        BottomNavItem("Receipt", ImageVector.vectorResource(id = R.drawable.bill), "selectCustomer"),
        BottomNavItem("Inventory", ImageVector.vectorResource(id = R.drawable.inventory), "inventory")
    )

    if (isAdminLoggedIn) {
        bottomNavItems.add(BottomNavItem("Accounts", ImageVector.vectorResource(id = R.drawable.account_group_outline), "addAccount"))
    }

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Blue
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            val isSelected = when (item.route) {
                "receipt" -> currentRoute?.startsWith(item.route) == true
                else -> currentRoute == item.route
            }

            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon,
                        contentDescription = item.title,
                        tint = (if (isSelected) Color.White else SkyBlue),
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(item.title,
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontFamily = font_notosans_bold
                    )
                },
                selected = isSelected,
                onClick = {
                    if (item.route == "receipt" && !CustomerSelectionHelper.isCustomerSelected) {
                        navController.navigate("selectCustomer")
                    } else if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkyBlue,
                    indicatorColor = SkyBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

