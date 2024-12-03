package com.example.kaydensdigitalassistant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, Color.Transparent)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = Color.Black,
                unfocusedPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.Black)
        )
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    initialIcon: Painter,
    toggleIcon: Painter
) {
    var isIconToggled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            visualTransformation = if(!isIconToggled) PasswordVisualTransformation() else VisualTransformation.None,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, Color.Transparent)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            trailingIcon = {
                IconButton(onClick = {
                    isIconToggled = !isIconToggled
                }) {
                    Icon(
                        painter = if (isIconToggled) toggleIcon else initialIcon,
                        contentDescription = "Toggle Icon",
                        tint = Color.Black
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.Black)
        )
    }
}

@Composable
fun CustomDoubleTextField(
    value: Double,
    onValueChange: (Double) -> Unit,
    placeholder: String
) {
    var textValue by remember { mutableStateOf(if (value == 0.0) "" else value.toString()) }

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp)
    ) {
        BasicTextField(
            value = textValue,
            onValueChange = { newValue ->
                if (newValue.isEmpty()) {
                    textValue = ""
                    onValueChange(0.0)
                } else {
                    newValue.toDoubleOrNull()?.let { number ->
                        textValue = newValue
                        onValueChange(number)
                    }
                }
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (textValue.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )
    }
}

