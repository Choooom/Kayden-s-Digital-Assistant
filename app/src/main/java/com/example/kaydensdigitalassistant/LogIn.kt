package com.example.kaydensdigitalassistant

import WindowLink
import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SnackbarDefaults.color
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBackground
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBorder

@Composable
fun LogIn(modifier: Modifier, backgroundColor: Color, navController: NavController){
    var username: String by remember{
        mutableStateOf("")
    }
    var password:String by remember{
        mutableStateOf("")
    }

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
                  Row(
                      modifier = Modifier
                          .fillMaxWidth(),
                      horizontalArrangement = Arrangement.Start
                  ) {
                      Icon(
                          imageVector = Icons.Default.Menu,
                          contentDescription = "Menu",
                          modifier = Modifier
                              .size(40.dp)
                              .padding(start = 10.dp, top = 10.dp),
                          tint = BlueEnd
                      )
                  }

                  Spacer(modifier = Modifier.height(40.dp))

                  Image(
                      painter = painterResource(id = R.drawable.company_logo),
                      contentDescription = "Kayden Trdg. Logo",
                      modifier = Modifier
                          .size(170.dp)
                          .alpha(0.7f),
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

                WindowLink(navController, "FORGOT PASSWORD", "home", 15)

                Row(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        //baguhen nalang yan if statement later pag may database functionality na
                        onClick = {
                            if(username == "admin" && password == "admin"){
                                navController.navigate("receipt")
                            }
                            else{
                                errorMessage = !errorMessage
                            }
                        },
                        modifier = Modifier
                            .padding(20.dp)
                            .height(55.dp)
                            .fillMaxWidth(0.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
                    ){
                        Text("LOGIN", color = Color.White, fontFamily = font_archivo)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ){
                    WindowLink(navController, "DON'T HAVE AN ACCOUNT?", "SIGN UP NOW", "home", 0, Color.Blue)
                }
            }
        }
    }

    if(username != "" || password != ""){
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

