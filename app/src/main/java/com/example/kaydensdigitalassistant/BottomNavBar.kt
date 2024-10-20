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
fun BottomNavBar(navController: NavController) {
    val bottomNavItems = listOf(
        BottomNavItem("Sales", ImageVector.vectorResource(id = R.drawable.sales), "home"),
        BottomNavItem("Receipt", ImageVector.vectorResource(id = R.drawable.bill), "receipt"),
        BottomNavItem("Inventory", ImageVector.vectorResource(id = R.drawable.inventory), "productList")
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Blue
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon,
                         contentDescription = item.title,
                         tint = (if (currentRoute == item.route) Color.White else SkyBlue),
                         modifier = Modifier
                             .size(30.dp)
                    )},
                label = {
                    Text(item.title,
                        color = if (currentRoute == item.route) Color.Black else Color.Gray,
                         fontFamily = font_notosans_bold
                    ) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
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
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
