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
import br.concy.demo.model.entity.AccelMeasurement
import br.concy.demo.model.entity.GyroscopeMeasurement
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.GyroRepository
import br.concy.demo.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private lateinit var bufferJob: Job

    private lateinit var sharedPreferences: SharedPreferences

    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    private val samplingFrequency = 1000L

    private val accelBuffer = mutableListOf<AccelMeasurement>()
    private val gyroBuffer = mutableListOf<GyroscopeMeasurement>()

    @Inject
    lateinit var accelRepository: AccelRepository

    @Inject
    lateinit var gyroRepository: GyroRepository

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

        val notification = NotificationHelper.createNotification(this)
        startForeground(111, notification)
        startBufferJob()

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

            val date = Date()

            when(it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val accelMeasurement = AccelMeasurement(
                        x = it.values[0],
                        y = it.values[1],
                        z = it.values[2],
                        registeredAt = sdf.format(date)
                    )
                    accelBuffer.add(accelMeasurement)
                }
                Sensor.TYPE_GYROSCOPE -> {
                    val gyroMeasurement = GyroscopeMeasurement(
                        x = it.values[0],
                        y = it.values[1],
                        z = it.values[2],
                        registeredAt = sdf.format(date)
                    )
                    gyroBuffer.add(gyroMeasurement)
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
        bufferJob.cancel()
        sharedPreferences.edit().putBoolean("isRunning", false).apply()
        stopForeground(STOP_FOREGROUND_REMOVE)

        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        Log.d(TAG, "onDestroy::SensorsService")
    }

    private fun startBufferJob() {
        bufferJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                processBuffer()
                delay(samplingFrequency)
            }
        }
    }

    private fun processBuffer() {
        if (accelBuffer.isNotEmpty()) {
            val accelMeasurement = accelBuffer.last()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    accelRepository.insert(accelMeasurement)
                    Log.d(TAG, "Inserted new accel measurement")
                    accelBuffer.clear()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }

        if (gyroBuffer.isNotEmpty()) {
            val gyroMeasurement = gyroBuffer.last()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    gyroRepository.insert(gyroMeasurement)
                    Log.d(TAG, "Inserted new gyro measurement")
                    gyroBuffer.clear()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}