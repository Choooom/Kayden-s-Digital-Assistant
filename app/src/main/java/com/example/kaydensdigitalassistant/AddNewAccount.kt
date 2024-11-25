package com.example.kaydensdigitalassistant

import WindowLink
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBackground
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBorder
import com.example.kaydensdigitalassistant.font_archivo

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AddNewAccount(navController = NavController(LocalContext.current))
}

@Composable
fun AddNewAccount(navController: NavController){
    val employeeViewModel = LocalEmployeeViewModel.current

    var fullName: String by remember{
        mutableStateOf("")
    }
    var username:String by remember{
        mutableStateOf("")
    }
    var dateOfBirth:String by remember{
        mutableStateOf("")
    }
    var password:String by remember{
        mutableStateOf("")
    }

    var isIconToggled by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf(false) }

    val toggleIcon = painterResource(id = R.drawable.eye_outline)
    val initialIcon = painterResource(id = R.drawable.eye_off_outline)


    var adminAttempt by remember { mutableStateOf(0)}
    var adminAttemptMessage by remember{mutableStateOf(false)}

    val insets = WindowInsets.systemBars.asPaddingValues()

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = insets.calculateTopPadding())
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(BlueStart, BlueEnd)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.25f)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(modifier = Modifier.height(15.dp))

                Image(
                    painter = painterResource(id = R.drawable.company_logo),
                    contentDescription = "Kayden Trdg. Logo",
                    modifier = Modifier
                        .size(170.dp)
                        .alpha(0.7f)
                        .clickable {
                            adminAttemptMessage = true
                            adminAttempt++
                        },
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = "Add New",
                fontFamily = font_archivo,
                fontWeight = FontWeight.W100,
                fontSize = 35.sp,
                color = Color.Black
            )
            Text(
                text = "Account",
                fontFamily = font_archivo,
                fontWeight = FontWeight.W100,
                fontSize = 35.sp,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 50.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "FULL NAME",
                    fontFamily = font_notosans_regular,
                    fontWeight = FontWeight.W100,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = BlueEnd,
                    unfocusedIndicatorColor = BlueEnd
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 50.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "USERNAME",
                    fontFamily = font_notosans_regular,
                    fontWeight = FontWeight.W100,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp),
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = BlueEnd,
                    unfocusedIndicatorColor = BlueEnd
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 50.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "DATE OF BIRTH",
                    fontFamily = font_notosans_regular,
                    fontWeight = FontWeight.W100,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                placeholder = {
                    Text(
                        text = "DD/MM/YYYY",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp),
                trailingIcon = {
                    Icon(
                        painterResource(id = R.drawable.calendar_month),
                        contentDescription = "Home Icon",
                        tint = Color.Black
                    )
                },
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = BlueEnd,
                    unfocusedIndicatorColor = BlueEnd
                )
            )
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        employeeViewModel.name = fullName
                        employeeViewModel.username = username
                        employeeViewModel.birthdate = dateOfBirth
                        navController.navigate("emailVerification")
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .height(55.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
                ){
                    Text("CONTINUE", color = Color.White, fontFamily = font_archivo)
                }
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Arrow",
            modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 20.dp))
        if(errorMessage){
            PopOffMessage(
                message = "Invalid username and/or password",
                onDismiss = { errorMessage = false },
                messageIcon = Icons.Filled.Info,
                backgroundColor = errorMessageBackground,
                backgroundBorder = errorMessageBorder
            )
        }
    }
}

