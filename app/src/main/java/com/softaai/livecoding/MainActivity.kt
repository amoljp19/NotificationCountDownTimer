package com.softaai.livecoding

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softaai.livecoding.ui.theme.LivecodingTheme
import java.text.DecimalFormat
import java.text.NumberFormat


class MainActivity : ComponentActivity() {

    companion object {
        const val COUNT_KEY = "COUNT_KEY" // const key to save/read value from bundle
    }

    lateinit var hms:String

    lateinit var serviceReceiver: ServiceToActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceReceiver = ServiceToActivity()
        val intentSFilter = IntentFilter("ServiceToActivityAction")
        registerReceiver(serviceReceiver, intentSFilter)

        setContent {
            LivecodingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    hms = "00:00:00"
                   CountDownTimerUI(hms)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(serviceReceiver);
    }

    override fun onSaveInstanceState(outState: Bundle) { // Here You have to save count value
        super.onSaveInstanceState(outState)
        Log.i("MyTag", "onSaveInstanceState")

        outState.putString(COUNT_KEY, hms)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // Here You have to restore count value
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("MyTag", "onRestoreInstanceState")

        hms = savedInstanceState.getString(COUNT_KEY).toString()
    }
}


class ServiceToActivity : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val notificationData = intent.extras
        val newData1 = notificationData!!.getString("ServiceToActivityKey")
        Toast.makeText(context, " " + newData1, Toast.LENGTH_SHORT).show()
        // newData is from the service
    }
}

@SuppressLint("UnspecifiedImmutableFlag")
@Composable
fun CountDownTimerUI(hms: String) {

    var count by remember { mutableStateOf("0") }
    if(!hms.equals("00:00:00")){
        Text(text = "" + hms)
    }
    else{
        Text(text = "" + count)
    }


    var textFieldState by remember {
        mutableStateOf("")
    }

    var buttonState by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)
    ) {


        OutlinedTextField(
            modifier = Modifier
                .width(400.dp)
                .padding(30.dp),
            value = textFieldState,
            onValueChange = {
                textFieldState = it
            },
            placeholder = {
                Text(text = "Enter time in minutes")
            },
            shape = RoundedCornerShape(24.dp),
        )

        Button(
            onClick = {

                if (!textFieldState.equals("")) {
                    buttonState = false
                /*    object : CountDownTimer(textFieldState.toLong() * 1000 * 60, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val f: NumberFormat = DecimalFormat("00")
                            val hour = millisUntilFinished / 3600000 % 24
                            val min = millisUntilFinished / 60000 % 60
                            val sec = millisUntilFinished / 1000 % 60
                            count =
                                f.format(hour)
                                    .toString() + ":" + f.format(min) + ":" + f.format(sec)

                        }

                        override fun onFinish() {
                            count = "00:00:00"
                            textFieldState = ""
                            buttonState = true
                        }
                    }.start()*/
                    val intent = Intent(Intent(context, NotificationService::class.java))

                    intent.putExtra("time", textFieldState)
                    context.startService(intent)
                } else {
                    count = "Please enter time in minutes"
                }
            },
            enabled = buttonState
        ) {
            Text("Start")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LivecodingTheme {
        CountDownTimerUI(hms = "")
    }
}