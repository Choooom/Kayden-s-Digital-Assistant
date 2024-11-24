package com.example.kaydensdigitalassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart

@Composable
fun ManageAccount(navController: NavController){
    val insets = WindowInsets.systemBars.asPaddingValues()
    var title by remember{ mutableStateOf("Employee") }

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(top = insets.calculateTopPadding()).background(Brush.horizontalGradient(
        colors = listOf(BlueStart, BlueEnd)
    )), contentAlignment = Alignment.TopCenter){
        Box(modifier = Modifier.fillMaxWidth()){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button",
                modifier = Modifier.align(Alignment.CenterStart).clickable { navController.popBackStack() }
            )
            Text(
                text = title,
                fontFamily = kanit_bold,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp).align(Alignment.CenterEnd)
            )
        }
        Column(modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.9f).padding(top = 100.dp).clip(RoundedCornerShape(20.dp)).background(Color.White),
            verticalArrangement = Arrangement.Top){
            Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f).height(50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Employee",
                        fontFamily = font_notosans_light,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 5.dp, end = 10.dp)
                            .clickable { title = "Employee" }
                    )
                    Spacer(
                        modifier = Modifier.fillMaxHeight(0.8f).width(1.dp).background(Color.Black)
                    )
                    Text(
                        text = "Customer",
                        fontFamily = font_notosans_light,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 10.dp).clickable { title = "Customer" }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth().height(50.dp).padding(end = 10.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
                    Button(
                        onClick = {
                            navController.navigate("addNewAccount")
                        },
                        modifier = Modifier
                            .height(35.dp)
                            .width(120.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueEnd),
                        shape = RectangleShape
                    ) {
                        Text("Add Employee", color = Color.White, fontFamily = font_archivo, fontSize = 10.sp)
                    }

                }
            }
        }
    }
}