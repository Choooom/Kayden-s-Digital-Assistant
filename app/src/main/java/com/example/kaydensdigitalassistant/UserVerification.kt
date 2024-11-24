package com.example.kaydensdigitalassistant

import WindowLink
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.kaydensdigitalassistant.data.EmployeeDetailViewModel
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBackground
import com.example.kaydensdigitalassistant.ui.theme.errorMessageBorder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


@Composable
fun NumberVerification(navController: NavController){
    val context = LocalContext.current

    var phoneNumber:String by remember{
        mutableStateOf("")
    }

    var incorrectInput by remember{
        mutableStateOf(false)
    }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(
        Brush.horizontalGradient(
            colors = listOf(BlueStart, BlueEnd)
        ))){
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
            Spacer(modifier = Modifier.height(150.dp).fillMaxWidth())
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically // Ensures vertical alignment
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center // Centers the text inside the Box
                ) {
                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)) {
                            append("Enter Email Address\n")
                        }
                        withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)) {
                            append("We will send you a One Time Password on your email\n")
                        }
                    }
                    Text(
                        text = text,
                        fontFamily = font_archivo,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if(it.length <= 10) phoneNumber = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp, top = 50.dp),
                placeholder = {
                    Text(
                        text = "Enter Email Address",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
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

            Spacer(modifier = Modifier.height(50.dp).fillMaxWidth())
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            isLoading = true  // Show loading state
                            onLoginClicked(context, navController, phoneNumber) {
                                isLoading = false  // Hide loading state
                                Log.d("PhoneAuth", "Navigating to OTP screen")
                                navController.navigate("otp")
                            }
                        } else {
                            incorrectInput = true
                        }
                    },
                    enabled = !isLoading && phoneNumber.length == 10,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(55.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Get OTP", color = Color.White, fontFamily = font_archivo)
                    }
                }
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Arrow",
            modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 20.dp).clickable { navController.navigate("addNewAccount")})
        if(incorrectInput){
            PopOffMessage(
                message = "Invalid Phone Number",
                onDismiss = { incorrectInput = false },
                messageIcon = Icons.Filled.Info,
                backgroundColor = errorMessageBackground,
                backgroundBorder = errorMessageBorder
            )
        }
    }
}

@Composable
fun UserVerification(navController: NavController){
    val context = LocalContext.current

    var otp:String by remember{
        mutableStateOf("")
    }

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(
        Brush.horizontalGradient(
        colors = listOf(BlueStart, BlueEnd)
    ))){
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
            Spacer(modifier = Modifier.height(150.dp).fillMaxWidth())
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically // Ensures vertical alignment
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center // Centers the text inside the Box
                ) {
                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)) {
                            append("Enter Verification Code\n")
                        }
                        withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)) {
                            append("If you're having trouble, contact Alon Tech\n")
                        }
                        withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)) {
                            append("at support@alontech.com")
                        }
                    }
                    Text(
                        text = text,
                        fontFamily = font_archivo,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            BasicTextField(
                value = otp,
                onValueChange = { if(otp.length <= 6) otp = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp, top = 50.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            ){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    repeat(6){index ->
                        val number = when{
                            index >= otp.length -> ""
                            else -> otp[index]
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally){
                            Text(text = number.toString(), style = MaterialTheme.typography.titleLarge)
                            Box(modifier = Modifier.width(40.dp).height(2.dp).background(Color.Green)){

                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp).fillMaxWidth())
            WindowLink(navController, "Resend Code", "home", 0, Color.Green)

            Spacer(modifier = Modifier.height(50.dp).fillMaxWidth())
            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        if (otp.length == 6) {
                            verifyPhoneNumberWithCode(context, storedVerificationId, otp, navController)
                        }
                    },
                    enabled = otp.length == 6,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(55.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
                ){
                    Text("Verify Code", color = Color.White, fontFamily = font_archivo)
                }
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Arrow",
            modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 20.dp).clickable { navController.navigate("addNewAccount")})
    }
}


val auth = FirebaseAuth.getInstance()
var storedVerificationId = ""

fun signInWithPhoneAuthCredential(context: Context, credential: PhoneAuthCredential, navController: NavController, ){
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity){ task ->
            if (task.isSuccessful){
                Toast.makeText(context, "login successful", Toast.LENGTH_SHORT).show()
                navController.navigate("success")
                val user = task.result?.user
            }else{
                if(task.exception is FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(context, "wrong OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
}

private const val TEST_MODE = false

fun onLoginClicked(context: Context, navController: NavController, phoneNumber: String, onCodeSend: () -> Unit){
    auth.setLanguageCode("en")
    Log.d("PhoneAuth", "Starting verification for number: +63$phoneNumber")

    val callback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.d("phoneBook", "verification completed")
            signInWithPhoneAuthCredential(context, p0, navController)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.d("phoneBook", "verification failed$p0")
            
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken){
            Log.d("phoneBook", "code sent$p0")
            storedVerificationId = p0
            onCodeSend()
        }
    }

    val phoneNumberToUse = if (TEST_MODE) {
        // Test phone numbers
        when (phoneNumber) {
            "911111111111" -> "+91 11 1111 1111" // Test number from Firebase
            else -> "+63$phoneNumber"       // Real numbers
        }
    } else {
        "+63$phoneNumber" // Production mode - real numbers only
    }

    val option = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumberToUse)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(callback)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(option)

    try {
        PhoneAuthProvider.verifyPhoneNumber(option)
        Log.d("PhoneAuth", "Verification request sent")
    } catch (e: Exception) {
        Log.e("PhoneAuth", "Error sending verification: ${e.message}")
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun verifyPhoneNumberWithCode(context: Context, verificationId: String, code: String, navController: NavController){
    val p0 = PhoneAuthProvider.getCredential(verificationId, code)
    signInWithPhoneAuthCredential(context, p0, navController)
}

fun signUpWithEmail(
    context: Context,
    email: String,
    password: String,
    navController: NavController,
    viewModel: EmployeeDetailViewModel
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                auth.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            viewModel.emailAddress = email
                            viewModel.password = password
                            Toast.makeText(context, "Verification email sent", Toast.LENGTH_SHORT).show()
                            navController.navigate("verifyEmail")
                        }
                    }
            } else {
                Toast.makeText(context, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

fun checkEmailVerification(navController: NavController) {
    auth.currentUser?.reload()?.addOnCompleteListener {
        if (auth.currentUser?.isEmailVerified == true) {
            navController.navigate("success")
        }
    }
}

fun resetPassword(email: String, context: Context, onSuccess: () -> Unit) {
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

fun changePassword(
    currentPassword: String,
    newPassword: String,
    context: Context,
    onSuccess: () -> Unit
) {
    val user = auth.currentUser
    val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)

    user.reauthenticate(credential).addOnCompleteListener { reauth ->
        if (reauth.isSuccessful) {
            user.updatePassword(newPassword).addOnCompleteListener { update ->
                if (update.isSuccessful) {
                    // Update local database
                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            }
        } else {
            Toast.makeText(context, "Current password is incorrect", Toast.LENGTH_SHORT).show()
        }
    }
}

// Add this Composable
@Composable
fun PasswordResetScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var incorrectInput by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(
            Brush.horizontalGradient(
                colors = listOf(BlueStart, BlueEnd)
            )
        )) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)) {
                            append("Reset Password\n")
                        }
                        withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)) {
                            append("Enter your email to receive a reset link\n")
                        }
                    }
                    Text(
                        text = text,
                        fontFamily = font_archivo,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp, top = 50.dp),
                placeholder = {
                    Text(
                        text = "Enter Email",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                },
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = BlueEnd,
                    unfocusedIndicatorColor = BlueEnd
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        isLoading = true
                        resetPassword(email, context) {
                            isLoading = false
                            navController.navigate("login")
                        }
                    } else {
                        incorrectInput = true
                    }
                },
                modifier = Modifier
                    .padding(20.dp)
                    .height(55.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Send Reset Link", color = Color.White, fontFamily = font_archivo)
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back Arrow",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 20.dp)
                .clickable { navController.navigate("login") }
        )

        if (incorrectInput) {
            PopOffMessage(
                message = "Please enter a valid email",
                onDismiss = { incorrectInput = false },
                messageIcon = Icons.Filled.Info,
                backgroundColor = errorMessageBackground,
                backgroundBorder = errorMessageBorder
            )
        }
    }
}

@Composable
fun EmailVerification(navController: NavController) {
    
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var incorrectInput by remember { mutableStateOf(false) }
    val employeeViewModel = LocalEmployeeViewModel.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(
            Brush.horizontalGradient(
                colors = listOf(BlueStart, BlueEnd)
            )
        )) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(150.dp))

            // Your existing header text style
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)) {
                            append("Create Account\n")
                        }
                        withStyle(style = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)) {
                            append("We will send you a verification link to your email\n")
                        }
                    }
                    Text(
                        text = text,
                        fontFamily = font_archivo,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp, top = 50.dp),
                placeholder = {
                    Text(
                        text = "Enter Email",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                },
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = BlueEnd,
                    unfocusedIndicatorColor = BlueEnd
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp, top = 20.dp),
                placeholder = {
                    Text(
                        text = "Enter Password",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                },
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = BlueEnd,
                    unfocusedIndicatorColor = BlueEnd
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Sign Up Button
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.length >= 6) {
                        isLoading = true
                        signUpWithEmail(context, email, password, navController, employeeViewModel)
                        isLoading = false
                    } else {
                        incorrectInput = true
                    }
                },
                modifier = Modifier
                    .padding(20.dp)
                    .height(55.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(containerColor = BlueEnd)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Create Account", color = Color.White, fontFamily = font_archivo)
                }
            }
        }

        // Back Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back Arrow",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 20.dp)
                .clickable { navController.navigate("addNewAccount") }
        )

        // Error Message
        if (incorrectInput) {
            PopOffMessage(
                message = "Please enter a valid email and password (min. 6 characters)",
                onDismiss = { incorrectInput = false },
                messageIcon = Icons.Filled.Info,
                backgroundColor = errorMessageBackground,
                backgroundBorder = errorMessageBorder
            )
        }
    }
}

@Composable
fun VerifyEmailScreen(navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Please verify your email")
        Text("Check your inbox for the verification link")

        Button(
            onClick = { checkEmailVerification(navController) }
        ) {
            Text("I've verified my email")
        }

        TextButton(
            onClick = {
                auth.currentUser?.sendEmailVerification()
                Toast.makeText(context, "Verification email resent", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("Resend verification email")
        }
    }
}