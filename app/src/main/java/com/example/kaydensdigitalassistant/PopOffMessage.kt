package com.example.kaydensdigitalassistant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PopOffMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.White, shape = MaterialTheme.shapes.small)
                .padding(16.dp)
        ) {
            Text(text = message, color = Color.Black)
        }
    }
}

@Composable
fun PopOffMessage(
    message: String,
    onDismiss: () -> Unit,
    messageIcon: ImageVector,
    backgroundColor: Color,
    backgroundBorder: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(5.dp))
                .background(backgroundColor.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)
                .border(BorderStroke(2.dp, backgroundBorder.copy(alpha = 0.8f)))
                .padding(15.dp)
        ) {
            Icon(imageVector = messageIcon,
                contentDescription = "Information Icon",
                modifier = Modifier.padding(end = 8.dp),)
            Text(text = message, color = Color.Black)
        }
    }
}