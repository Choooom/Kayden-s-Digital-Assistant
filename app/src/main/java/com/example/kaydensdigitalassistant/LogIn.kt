package com.example.kaydensdigitalassistant

import WindowLink
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.CustomerDetail
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBackground
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBorder
import com.example.kaydensdigitalassistant.font_archivo
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Composable
fun LogIn(modifier: Modifier, backgroundColor: Color, navController: NavController){
    var username: String by remember{
        mutableStateOf("")
    }
    var password:String by remember{
        mutableStateOf("")
    }

    val productViewModel = LocalProductsViewModel.current

    /*
    val context = LocalContext.current

    fun getBitmap(drawableId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, drawableId)
    }

    val productsList = listOf(
        Products(productName = "Red Horse 1000 ML (Mucho)", type = "Beer", normalPrice = 630.0, discountedPrice = 628.0, stock = 100.0, productIcon = getBitmap(R.drawable.mucho)),
        Products(productName = "Red Horse 500 ML", type = "Beer", normalPrice = 620.0, discountedPrice = 615.0, stock = 100.0, productIcon = getBitmap(R.drawable.redhorse_500)),
        Products(productName = "Red Horse 330 ML (Stallion)", type = "Beer", normalPrice = 860.0, discountedPrice = 853.0, stock = 100.0, productIcon = getBitmap(R.drawable.stallion)),
        Products(productName = "Pale Pilsen 1000 ML (Grande)", type = "Beer", normalPrice = 550.0, discountedPrice = 544.0, stock = 100.0, productIcon = getBitmap(R.drawable.grande)),
        Products(productName = "Pale Pilsen 320 ML", type = "Beer", normalPrice = 820.0, discountedPrice = 800.0, stock = 100.0, productIcon = getBitmap(R.drawable.pilsen_small)),
        Products(productName = "San Mig Light 330 ML", type = "Beer", normalPrice = 1040.0, discountedPrice = 1020.0, stock = 100.0, productIcon = getBitmap(R.drawable.sanmig_light)),
        Products(productName = "San Mig Apple 330 ML", type = "Beer", normalPrice = 800.0, discountedPrice = 778.0, stock = 100.0, productIcon = getBitmap(R.drawable.sanmig_apple)),
        Products(productName = "RC Original Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.rc_small)),
        Products(productName = "RC Orange Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.orange_small)),
        Products(productName = "RC Lemon Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.lemon_small)),
        Products(productName = "RC Root Beer Small 240 ML", type = "Softdrink", normalPrice = 174.0, discountedPrice = 169.0, stock = 100.0, productIcon = getBitmap(R.drawable.rootbeer_small)),
        Products(productName = "RC Mega Original 800 ML", type = "Softdrink", normalPrice = 260.0, discountedPrice = 253.0, stock = 100.0, productIcon = getBitmap(R.drawable.rc_mega)),
        Products(productName = "RC Mega Orange 800 ML", type = "Softdrink", normalPrice = 260.0, discountedPrice = 253.0, stock = 100.0, productIcon = getBitmap(R.drawable.orange_mega)),
        Products(productName = "RC Mega Lemon 800 ML", type = "Softdrink", normalPrice = 260.0, discountedPrice = 253.0, stock = 100.0, productIcon = getBitmap(R.drawable.lemon_mega)),
        Products(productName = "Cobra Original (Yellow) 240 ML", type = "Energy-Drink", normalPrice = 300.0, discountedPrice = 295.0, stock = 100.0, productIcon = getBitmap(R.drawable.cobra_yellow)),
        Products(productName = "Cobra Citrus (Green) 240 ML", type = "Energy-Drink", normalPrice = 300.0, discountedPrice = 295.0, stock = 100.0, productIcon = getBitmap(R.drawable.cobra_green))
    )

    LaunchedEffect(key1 = Unit) {
        productsList.forEach { product ->
            productViewModel.insertProduct(product)
        }
    }
*/
    val annotatedText = buildAnnotatedString {
        append("")

        pushStringAnnotation(
            tag = "URL",
            annotation = "https://www.example.com"
        )
        withStyle(style = SpanStyle(
            color = Color.Black,
            textDecoration = TextDecoration.Underline,
            fontFamily = font_archivo,
            fontSize = 15.sp,)) {
            append("FORGOT PASSWORD")
        }
        pop()
    }

    var errorMessage by remember { mutableStateOf(false) }

    var adminAttempt by remember { mutableStateOf(0)}
    var adminAttemptMessage by remember{mutableStateOf(false)}

    val userRoleViewmodel = LocalUserRoleViewModel.current
    val isAdmin by userRoleViewmodel.isAdmin.collectAsState()

    val insets = WindowInsets.systemBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxSize())
        {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(BlueStart, BlueEnd))
                    )
                    .padding(bottom = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))

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

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight()
                    .fillMaxWidth(0.9f)
                    .padding(top = 240.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(backgroundColor)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.account_group_outline),
                    contentDescription = "Employee Icon",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = Color.Black
                )
                Text(
                    text = "EMPLOYEE LOGIN",
                    fontFamily = font_archivo,
                    fontWeight = FontWeight.W100,
                    fontSize = 25.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )

                CustomTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "USERNAME" ,
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "PASSWORD" ,
                    initialIcon = painterResource(id = R.drawable.eye_off_outline),
                    toggleIcon = painterResource(id = R.drawable.eye_outline),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                }

                WindowLink(navController, "FORGOT PASSWORD", "resetPassword", 15)

                val employeeViewModel = LocalEmployeeViewModel.current
                var isLoading by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                scope.launch {
                                    employeeViewModel.loginEmployee(username, password)
                                    employeeViewModel.currentEmployee.collect { employee ->
                                        if (employee != null) {
                                            isLoading = false
                                            userRoleViewmodel.setAdminStatus(false)
                                            errorMessage = false
                                            navController.navigate("selectCustomer")
                                            scope.cancel()
                                        } else {
                                            isLoading = false
                                            errorMessage = true
                                        }
                                    }
                                }
                            } else {
                                errorMessage = true
                            }
                        },
                        modifier = Modifier
                            .padding(20.dp)
                            .height(55.dp)
                            .fillMaxWidth(0.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text("LOGIN", color = Color.White, fontFamily = font_archivo)
                        }
                    }

                }
            }
        }
    }


        if(errorMessage){
            PopOffMessage(
                message = "Invalid username and/or password",
                onDismiss = { errorMessage = false },
                messageIcon = Icons.Filled.Info,
                backgroundColor = errorMessageBackground,
                backgroundBorder = errorMessageBorder
            )
        }

    if(adminAttemptMessage){
        if(adminAttempt <= 5){
            PopOffMessage(navController, "You are ${6 - adminAttempt} clicks away from being an Admin!", onDismiss = {adminAttemptMessage = false})
        }
        else{
            adminAttemptMessage = false
            adminAttempt = 0
            navController.navigate("admin_login")
        }
    }
}

