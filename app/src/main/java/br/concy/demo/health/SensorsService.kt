package br.concy.demo.health

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import br.concy.demo.TAG

class SensorsService: Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gyroscope: Sensor
    private lateinit var wakeLock: WakeLock

    private lateinit var sharedPreferences: SharedPreferences

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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent) {
        event.let {
            when(it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    Log.d(TAG, "Gyroscope: x=$x, y=$y, z=$z")
                }
                Sensor.TYPE_GYROSCOPE -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    Log.d(TAG, "Gyroscope: x=$x, y=$y, z=$z")
                }
                else -> {}
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "${sensor.name} precision: $accuracy")
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        sharedPreferences.edit().putBoolean("isRunning", false).apply()

        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}