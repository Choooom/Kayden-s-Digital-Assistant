@file:Suppress("DEPRECATION")

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.font_archivo

@Composable
fun WindowLink(navController: NavController, message: String, annotation:String) {
    val annotatedText = buildAnnotatedString {
        append("")

        // Push a string annotation for navigation
        pushStringAnnotation(tag = "SCREEN", annotation = annotation)

        // Using withStyle correctly with SpanStyle
        withStyle(style = SpanStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = font_archivo,
        )
        ) {
            append(message)
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations("SCREEN", offset, offset).firstOrNull()?.let { annotation ->
                navController.navigate(annotation.item) // Navigate to the screen
            }
        }
    )
}

@Composable
fun WindowLink(navController: NavController, message: String, annotation:String, startPadding: Int) {
    val annotatedText = buildAnnotatedString {
        append("")

        // Push a string annotation for navigation
        pushStringAnnotation(tag = "SCREEN", annotation = annotation)

        // Using withStyle correctly with SpanStyle
        withStyle(style = SpanStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = font_archivo,
        )
        ) {
            append(message)
        }
        pop()
    }

    ClickableText(
        modifier = Modifier.padding(start = startPadding.dp),
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations("SCREEN", offset, offset).firstOrNull()?.let { annotation ->
                navController.navigate(annotation.item) // Navigate to the screen
            }
        }
    )
}

@Composable
fun WindowLink(navController: NavController, message: String, annotation:String, startPadding: Int, textColor: Color) {
    val annotatedText = buildAnnotatedString {
        append("")

        pushStringAnnotation(tag = "SCREEN", annotation = annotation)

        withStyle(style = SpanStyle(
            color = textColor,
            fontSize = 16.sp,
            fontFamily = font_archivo,
        )
        ) {
            append(message)
        }
        pop()
    }

    ClickableText(
        modifier = Modifier.padding(start = startPadding.dp),
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations("SCREEN", offset, offset).firstOrNull()?.let { annotation ->
                navController.navigate(annotation.item) // Navigate to the screen
            }
        }
    )
}

@Composable
fun WindowLink(navController: NavController, header:String, message: String, annotation:String, startPadding: Int, textColor: Color) {
    val annotatedText = buildAnnotatedString {
        append("$header ")

        pushStringAnnotation(tag = "SCREEN", annotation = annotation)

        withStyle(style = SpanStyle(
            color = textColor,
            fontSize = 16.sp,
            fontFamily = font_archivo,
        )
        ) {
            append(message)
        }
        pop()
    }

    ClickableText(
        modifier = Modifier.padding(start = startPadding.dp),
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations("SCREEN", offset, offset).firstOrNull()?.let { annotation ->
                navController.navigate(annotation.item) // Navigate to the screen
            }
        }
    )
}
