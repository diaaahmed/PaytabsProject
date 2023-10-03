package com.paytabs.paytabsproject

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionClass
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.paytabs.paytabsproject.ui.theme.PaytabsProjectTheme
import kotlin.random.Random


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                TextFieldAndButton()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldAndButton()
{
    val inputValue = remember { mutableStateOf("") }

    val context = LocalContext.current

        TextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = inputValue.value, onValueChange ={inputValue.value = it},
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            placeholder = { Text(text = "Enter amount you want pay") })
    Spacer(modifier = Modifier.height(20.dp))

    Button(onClick = { paytabsClick(context,inputValue.value) })
    {
        Text(text = "Click for test paytabs")
    }

}

fun paytabsClick(context: Context,amount:String)
{
    val number = Random(0).nextInt()

    val configData:PaymentSdkConfigurationDetails = generatePaytabsConfigurationDetails(number.toString(),
    amount)

    PaymentSdkActivity.startCardPayment(context as Activity,configData, object : CallbackPaymentInterface
    {
        override fun onError(error: PaymentSdkError)
        {
            Log.d(TAG, "diaa onError: ${error.msg}")
            Toast.makeText(context, "Error occure", Toast.LENGTH_SHORT).show()
        }

        override fun onPaymentCancel()
        {
            Log.d(TAG, "diaa cancelled")
            Toast.makeText(context, "Payment canceled", Toast.LENGTH_SHORT).show()

        }

        override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails)
        {
            Toast.makeText(context, "Paymeny done ${paymentSdkTransactionDetails.cartAmount}",
                Toast.LENGTH_SHORT).show()

            Log.d(TAG, "diaa finish ${paymentSdkTransactionDetails.token}" +
                    " and ${paymentSdkTransactionDetails.paymentResult}")
        }
    })
}

fun generatePaytabsConfigurationDetails(
    orderNum: String,
    value: String

): PaymentSdkConfigurationDetails {
    // Here you can enter your profile id from payabs account
    val profileId = "122125"
    // Here you can enter server key from payabs account
    val serverKey = ""
    // Here you can enter your client key from payabs account
    val clientKey = ""
    val transactionTitle = "Pay Now"
    val cartDesc = "Test pay" // Description in paytab info
    val currency = "EGP"
    val merchantCountryCode = "EG"
    val amount: Double = value.toDouble()
    val locale = PaymentSdkLanguageCode.EN
    var email = ""

    email = "diaa@gmail.com"

    val billingData = PaymentSdkBillingDetails(
        "City",
        merchantCountryCode,
        email,
        "Diaa Ahmed",
        "01028237267", "zipcode",
        "Minia", ""
    )

    // Customer details
    val shippingData = PaymentSdkShippingDetails(
        "City",
        merchantCountryCode,
        email,
        "Diaa Ahmed",
        "01028237267", "zipcode",
        "Minia", ""
    )

    val configData = PaymentSdkConfigBuilder(
        profileId, serverKey, clientKey, amount, currency
    ).setCartDescription(cartDesc)
        .setLanguageCode(locale)
        .setMerchantCountryCode(merchantCountryCode)
        .setTransactionType(PaymentSdkTransactionType.SALE)
        .setTransactionClass(PaymentSdkTransactionClass.ECOM)
        .setCartId(orderNum)
        .setBillingData(billingData)
        .setShippingData(shippingData)
        .setScreenTitle(transactionTitle)

    return configData.build()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PaytabsProjectTheme {
        Greeting("Android")
    }
}