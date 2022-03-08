package com.softaai.livecoding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import java.lang.String
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LivecodingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        startService(Intent(this, NotificationService::class.java))
    }
}

@Composable
fun Greeting() {

    var count by remember { mutableStateOf("0") }

    Text(text = "" + count)

        //timer.start()

    var textFieldState by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)) {


        OutlinedTextField(
            modifier = Modifier
                .width(400.dp)
                .padding(30.dp),
            value = textFieldState,
            onValueChange = {
                textFieldState = it
            },
            placeholder = {
                Text(text = "Enter your time")
            },
            shape = RoundedCornerShape(24.dp),
        )
        val v = if (textFieldState.equals("")) "0" else textFieldState
        val timer = object: CountDownTimer(2000, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val hms = String.format(
                    "%02d : %02d : %02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                count = hms
            }

            override fun onFinish() {

            }
        }


        Button(onClick = {
            val intent = Intent(Intent(context, NotificationService::class.java))
            intent.putExtra("time", textFieldState);
            context.startService(intent)
            //timer.start()
        }) {
            Text("Start")
        }
    }







}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LivecodingTheme {
        Greeting()
    }
}