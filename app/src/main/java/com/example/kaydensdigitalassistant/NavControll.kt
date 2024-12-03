package com.example.kaydensdigitalassistant

import androidx.navigation.NavController

fun NavController.navigateWithPopUp(
    route: String,
    popUpToRoute: String? = null,
    inclusive: Boolean = false
) {
    this.navigate(route) {
        (popUpToRoute ?: this@navigateWithPopUp.graph.startDestinationRoute)?.let {
            popUpTo(it) {
                this.inclusive = inclusive
            }
        }
        launchSingleTop = true
    }
}