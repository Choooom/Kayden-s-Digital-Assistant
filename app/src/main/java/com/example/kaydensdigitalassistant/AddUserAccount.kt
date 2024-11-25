package com.example.kaydensdigitalassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart

@Composable
fun AddUserAccount(navController: NavController){
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(
        Brush.horizontalGradient(
        colors = listOf(BlueStart, BlueEnd)
    )).clickable { navController.navigate("manageAccounts") },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text(
            text = "Your Account has been made!",
            fontFamily = kanit_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
        )
        Text(
            text = "Click anywhere to continue",
            fontFamily = kanit_light,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White,
        )
    }
}