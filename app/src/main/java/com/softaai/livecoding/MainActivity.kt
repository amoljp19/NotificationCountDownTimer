package com.softaai.livecoding

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.softaai.livecoding.ui.theme.LivecodingTheme
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    companion object {
        const val COUNT_KEY = "COUNT_KEY" // const key to save/read value from bundle
    }

    lateinit var hms:String

    //lateinit var serviceReceiver: ServiceToActivity

    lateinit var timerManager: TimerDataStoreManager



    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerManager = TimerDataStoreManager(this)

//
//        //serviceReceiver = ServiceToActivity()
//        val intentSFilter = IntentFilter("ServiceToActivityAction")
//        registerReceiver(serviceReceiver, intentSFilter)

        lifecycleScope.launch {
            timerManager.timer.collect { timer ->
                hms = timer
                setContent {
                    LivecodingTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
//                    hms = "00:00:00"

                            CountDownTimerUI(hms)
                        }
                    }
                }
            }
        }


       /* setContent {
            LivecodingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    hms = "00:00:00"

                   CountDownTimerUI(hms)
                }
            }
        }*/
    }


   /* override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(serviceReceiver);
    }*/

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

//    class ServiceToActivity : BroadcastReceiver() {
//        var newData:String = "00:00:00"
//        override fun onReceive(context: Context?, intent: Intent) {
//            val notificationData = intent.extras
//            newData = notificationData!!.getString("ServiceToActivityKey").toString()
//            //Toast.makeText(context, " " + newData, Toast.LENGTH_SHORT).show()
//
//
//        }
//    }


}



@OptIn(InternalCoroutinesApi::class)
@SuppressLint("UnspecifiedImmutableFlag")
@Composable
fun CountDownTimerUI(
    hms: String
) {

    var count by remember { mutableStateOf("0") }
    //d
    Text(text = hms)
   /* if(!hms.equals("00:00:00")){
        Text(text = "" + hms)
    }
    else{
        Text(text = "" + count)
    }

*/
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


                    count = hms
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
        //CountDownTimerUI(hms = "", lifecycleScope = lifecycleScope, timerManager = timerManager)
    }
}