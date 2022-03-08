package com.softaai.livecoding

import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit


class NotificationService : Service() {
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    var TAG = "Timers"
    var Your_X_SECS = 1L
    var hms = ""
    var hmsTime = 0L

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.softaai.livecoding.notifications"
    private val description = "Test Notification"

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        //val intent = getIntent()
        val time = intent?.extras!!.getString("time")
        startTimer(time)
        createTimerNotification(intent)
        //createNotification()

        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        //stopTimerTask()
        super.onDestroy()
    }

    //we are going to use a handler to be able to run in our TimerTask
    val handler: Handler = Handler()
    fun startTimer(time : String?) {
        //timer = Timer()
        initializeTimerTask(time)
       // timer!!.schedule(timerTask, 1000, time!!.toLong() * 1000)
    }

    fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask(time:String?) {
//        timerTask = object : TimerTask(){
//            override fun run() {
//             createNotification(time)
//            }
//        }

        object : CountDownTimer(time!!.toLong() * 1000 * 60, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val hour = millisUntilFinished / 3600000 % 24
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                hms =
                    f.format(hour).toString() + ":" + f.format(min) + ":" + f.format(sec)

                raiseNotification(builder, hms)

            }

            override fun onFinish() {
                hms = "00:00:00"
            }
        }.start()

//        val timer = object: CountDownTimer(time!!.toLong(), 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//               //createNotification(millisUntilFinished.toString())
//                hmsTime = millisUntilFinished
//            }
//
//            override fun onFinish() {
//
//            }
//        }
//        timer.start()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createTimerNotification(intent: Intent) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

       // val intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("Timer Notification")
                .setContentText(hms)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(1234, builder.build())
    }

    private fun raiseNotification(b: Notification.Builder, hms: String) {
        b.setContentText(hms)
        b.setOngoing(true)

        notificationManager.notify(1234, b.build())
    }

    private fun createNotification() {

        hms = String.format(
            "%02d : %02d : %02d", TimeUnit.MILLISECONDS.toHours(hmsTime),
            TimeUnit.MILLISECONDS.toMinutes(hmsTime) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(hmsTime)
            ),
            TimeUnit.MILLISECONDS.toSeconds(hmsTime) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(hmsTime)
            )
        )

        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        val mBuilder = NotificationCompat.Builder(
           applicationContext,
            default_notification_channel_id
        )

        mBuilder.setContentTitle("Time : " + hms)
       // mBuilder.setContentText("Notification Listener Service Example")
        mBuilder.setTicker("Notification Listener Service Example")
        mBuilder.setSmallIcon(R.drawable.ic_dialog_alert)
        mBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            assert(mNotificationManager != null)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager!!.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"
    }
}