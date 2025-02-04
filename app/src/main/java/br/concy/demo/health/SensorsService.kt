package br.concy.demo.health

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.core.app.NotificationCompat
import br.concy.demo.R
import br.concy.demo.TAG
import br.concy.demo.model.entity.AccelMeasurement
import br.concy.demo.model.repository.AccelRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SensorsService: Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gyroscope: Sensor
    private lateinit var wakeLock: WakeLock

    private lateinit var sharedPreferences: SharedPreferences

    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    private var lastUpdateTime = 0L
    private val delay = 1000L
    private val CHANNEL_ID = "sensor_service_channel"
    private val notificationId = 111

    @Inject
    lateinit var accelRepository: AccelRepository

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "$TAG::wakelock"
        )
        wakeLock.acquire(12*60*60*1000L)

        accelerometer.let {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )

            sensorManager.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        sharedPreferences = getSharedPreferences("SensorServiceState", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isRunning", true).apply()

        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sensors Service")
            .setContentText("Sensors are running in the background.")
            .setSmallIcon(R.drawable.baseline_monitor_heart_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(notificationId, notification)

        Log.d(TAG, "onCreate::SensorsService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent) {
        event.let {

            val currentTime = System.currentTimeMillis()

            if (currentTime - lastUpdateTime > delay) {
                val date = Date()

                when(it.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        val accelMeasurement = AccelMeasurement(
                            x = it.values[0],
                            y = it.values[1],
                            z = it.values[2],
                            registeredAt = sdf.format(date)
                        )

                        try {
                            CoroutineScope(Dispatchers.IO).launch {
                                accelRepository.insert(accelMeasurement)
                                Log.d(TAG, "Inserted new accel measurement")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, e.message.toString())
                        }
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]
                        Log.d(TAG, "Gyroscope: x=$x, y=$y, z=$z")
                    }
                    else -> {}
                }

                lastUpdateTime = currentTime
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "${sensor.name} precision: $accuracy")
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sensor Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        sharedPreferences.edit().putBoolean("isRunning", false).apply()
        stopForeground(STOP_FOREGROUND_REMOVE)
        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        Log.d(TAG, "onDestroy::SensorsService")
    }
}