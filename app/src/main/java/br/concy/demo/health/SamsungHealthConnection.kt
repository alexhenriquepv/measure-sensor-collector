package br.concy.demo.health

import android.content.Context
import android.util.Log
import br.concy.demo.TAG
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class SamsungHealthConnection(
    private val successCallback: () -> Unit,
    private val errorCallback: () -> Unit
) {

    private lateinit var htService: HealthTrackingService
    lateinit var ecgTracker: HealthTracker
    lateinit var hrTracker: HealthTracker
    lateinit var skinTempTracker: HealthTracker
    var initializedService = false

    fun connect(context: Context) {
        val samsungConnectionListener = object : ConnectionListener {

            override fun onConnectionSuccess() {
                Log.d(TAG, "Health data service is connected")
                ecgTracker = htService.getHealthTracker(HealthTrackerType.ECG_ON_DEMAND)
                hrTracker = htService.getHealthTracker(HealthTrackerType.HEART_RATE_CONTINUOUS)
                skinTempTracker = htService.getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE_ON_DEMAND)
                initializedService = true
                successCallback()
            }

            override fun onConnectionEnded() {
                Log.d(TAG, "Health data service is disconnected")
            }

            override fun onConnectionFailed(e: HealthTrackerException) {
                Log.e(TAG, "Conn Failed reason: " + e.errorCode)
                errorCallback()
            }
        }

        Log.d(TAG, "Setting up Health data service..")
        htService = HealthTrackingService(samsungConnectionListener, context)
        htService.connectService()
    }

    fun isInitialized(): Boolean {
        return initializedService
    }

    fun disconnect() {
        htService.disconnectService()
    }
}