package com.softaai.livecoding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import com.softaai.livecoding.ui.theme.LivecodingTheme
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    lateinit var hms: String

    lateinit var timerManager: TimerDataStoreManager


    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timerManager = TimerDataStoreManager(this)

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
    }
}


@OptIn(InternalCoroutinesApi::class)
@SuppressLint("UnspecifiedImmutableFlag")
@Composable
fun CountDownTimerUI(
    hms: String
) {
    Text(text = hms)

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
                    val intent = Intent(Intent(context, NotificationService::class.java))

                    intent.putExtra("time", textFieldState)
                    context.startService(intent)
                } else {
                    textFieldState = "Please enter time in minutes"
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