import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.example.kaydensdigitalassistant.CurrentDateTime
import com.example.kaydensdigitalassistant.data.ReceiptItem

@Composable
fun PrintToThermalPrinter(
    referenceNumber: String,
    customerName: String,
    customerAddress: String,
    paymentOption: String,
    receiptItems: List<ReceiptItem>,
    totalAmount: Double,
    pricingOption: String,
    deposit: Double = 0.0
) {
    val context = LocalContext.current

    val printString = buildString {
        append("<112>KAYDEN")
        append("<110>Ref. No.: $referenceNumber")

        append("<010>${CurrentDateTime()}")

        append("<010>${customerName}")
        append("<010>${customerAddress}\n")

        append("<010>------------------------------")

        append("<110>$paymentOption")

        if(paymentOption == "Gcash"){
            append("<110>Knyla M. - 09178122285")
        }

        append("<010>------------------------------")

        append("<100>Order Details:\n")
        for (item in receiptItems) {
            append("<000>${item.name}")
            append("<010>${item.amount} x ${item.quantity} = ${item.amount * item.quantity}")
        }

        append("<010>------------------------------")

        append("<100>Total: ${totalAmount + deposit}")
        if(pricingOption == "Discounted"){
            append("<100>(Discounted)")
        }

        if (deposit > 0.0) {
            append("<000>Deposit: $deposit")
        }
        append("<010>------------------------------\n")
        append("<010>THANK YOU!\n\n")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, printString)
        type = "text/plain"
    }

    val packageManager = context.packageManager
    val activities = packageManager.queryIntentActivities(sendIntent, 0)
    if (activities.isNotEmpty()) {
        context.startActivity(Intent.createChooser(sendIntent, "Send to:"))
    } else {
        Toast.makeText(context, "No app found to handle print request.", Toast.LENGTH_SHORT).show()
    }
}
