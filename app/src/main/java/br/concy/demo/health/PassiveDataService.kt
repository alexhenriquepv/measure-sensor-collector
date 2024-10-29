package br.concy.demo.health

import android.content.ContentValues
import android.util.Log
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.HeartRateAccuracy

class PassiveDataService: PassiveListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d(ContentValues.TAG, "onCreate PassiveListenerService")
    }

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        super.onNewDataPointsReceived(dataPoints)
        val heartRateBpm = dataPoints.getData(DataType.HEART_RATE_BPM)
        heartRateBpm.forEach { hr ->
            Log.i(
                ContentValues.TAG, "Heart rate: ${hr.value}, " +
                        "Accuracy: ${(hr.accuracy as HeartRateAccuracy).sensorStatus}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ContentValues.TAG, "onDestroy PassiveListenerService")
    }
}