package com.softaai.livecoding

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import java.text.DecimalFormat
import java.text.NumberFormat


class NotificationService : Service() {

    var TAG = "Timers"
    var hms = "00:00:00"


    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    lateinit var notification: Notification
    private val channelId = "com.softaai.livecoding.notifications"
    private val description = "Test Notification"


    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        val time = intent?.extras!!.getString("time")

        initializeTimerTask(time)
        createTimerNotification(intent)
        startForeground(
            1,
           notification
        )

        return START_STICKY
    }



    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    fun initializeTimerTask(time: String?) {

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

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createTimerNotification(intent: Intent) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("Timer Notification")
                .setContentText(hms)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
        }

        notification = builder.build()
        //notificationManager.notify(1234, notification)
    }

    private fun raiseNotification(b: Notification.Builder, hms: String) {
        b.setContentText(hms)
        b.setOngoing(true)

        notificationManager.notify(1, b.build())
    }
}