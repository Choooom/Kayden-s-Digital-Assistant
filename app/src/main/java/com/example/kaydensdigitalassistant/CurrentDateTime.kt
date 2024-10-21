package com.example.kaydensdigitalassistant

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Manila"))

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val formattedDateTime = currentDateTime.format(formatter)

    return formattedDateTime
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTimeDate(): String {

    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a")
    val currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Manila"))

    return currentDateTime.format(formatter)
}
