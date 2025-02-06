package br.concy.demo.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import br.concy.demo.R

object NotificationHelper {

    private const val CHANNEL_ID = "sensor_service_channel"

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sensor Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(context: Context): Notification {
        createNotificationChannel(context)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Sensors Service")
            .setContentText("Sensors are running in the background.")
            .setSmallIcon(R.drawable.baseline_monitor_heart_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
}