package com.example.kaydensdigitalassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart

@Composable
fun SuccessScreen(navController: NavController) {
    val employeeDetailViewModel = LocalEmployeeViewModel.current

    employeeDetailViewModel.insertCompleteEmployee()
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(
        Brush.horizontalGradient(
            colors = listOf(BlueStart, BlueEnd)
        ))){
        Text(text = "Success")
    }
}