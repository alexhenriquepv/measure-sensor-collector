package br.concy.demo.health

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import br.concy.demo.TAG
import br.concy.demo.util.NotificationHelper
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.ValueKey

class ShsService: Service() {

    private lateinit var shc: SamsungHealthConnection

    override fun onCreate() {
        super.onCreate()
        shc = SamsungHealthConnection(
            successCallback = {
                startTracking()
            },
            errorCallback = {
                Log.e(TAG, "Fail to connect Samsung Health")
            }
        )
        shc.connect(this)

        val notification = NotificationHelper.createNotification(this)
        startForeground(111, notification)
    }

    private val hrListener = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            Log.d(TAG, "onDataReceived")
            if (list.isNotEmpty()) {
                for (dp in list) {
                    val status = dp.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS)
                    val value = dp.getValue(ValueKey.HeartRateSet.HEART_RATE)
                    val ibiValues = dp.getValue(ValueKey.HeartRateSet.IBI_LIST)

                    Log.d(TAG, "status: $status")
                    Log.d(TAG, "value: $value")
                    Log.d(TAG, "ibiValues: $ibiValues")
                }
            }
        }

        override fun onFlushCompleted() {
            Log.e(TAG, "Flush completed")
        }

        override fun onError(err: HealthTracker.TrackerError?) {
            Log.e(TAG, "TrackerError: " + err.toString())
        }
    }

    private fun startTracking() {
        Log.d(TAG, "startTracking")
        shc.hrTracker.setEventListener(hrListener)
    }

    private fun stopTracking() {
        Log.d(TAG, "stopTracking")
        shc.hrTracker.unsetEventListener()
        shc.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
        stopForeground(STOP_FOREGROUND_REMOVE)

        Log.d(TAG, "onDestroy::ShsService")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}