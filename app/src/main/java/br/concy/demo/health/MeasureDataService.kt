package br.concy.demo.health

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.unregisterMeasureCallback
import br.concy.demo.R
import br.concy.demo.TAG
import br.concy.demo.model.entity.HeartHateMeasurement
import br.concy.demo.model.repository.HeartRateRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MeasureDataService: Service() {

    @Inject
    lateinit var apiService: HeartRateAPIService

    @Inject
    lateinit var healthServicesClient: HealthServicesClient

    @Inject
    lateinit var repository: HeartRateRepository

    private lateinit var callback: MeasureCallback

    private fun setup() {
        callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                if (availability is DataTypeAvailability) {
                    1 + 1
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateBpm = data.getData(DataType.HEART_RATE_BPM)
                val bpm = heartRateBpm.last().value

                if (bpm > 0) {
                    val measurement = HeartHateMeasurement(
                        bpm = bpm.toFloat()
                    )

                    runBlocking {
                        repository.insert(measurement)
                        try {
                            val res = apiService.sendRegister(measurement)
                            Log.d(TAG, "Registered: $res")
                        } catch (e: Exception) {
                            Log.e(TAG, "Fail: ${e.message}")
                        }
                    }
                }
            }
        }

        healthServicesClient.measureClient.registerMeasureCallback(
            DataType.HEART_RATE_BPM,
            callback
        )
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "CHANNEL_ID",
            "Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Medindo dados")
            .setContentText("O serviço está rodando em segundo plano.")
            .setSmallIcon(R.drawable.baseline_monitor_heart_24)
            .build()

        startForeground(11, notification)

        setup()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::callback.isInitialized) {
            runBlocking {
                healthServicesClient.measureClient.unregisterMeasureCallback(
                    DataType.HEART_RATE_BPM,
                    callback
                )
            }
        }
    }
}