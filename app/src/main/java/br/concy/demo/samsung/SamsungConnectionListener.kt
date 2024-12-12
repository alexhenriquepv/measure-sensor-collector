package br.concy.demo.samsung

import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTrackerException

class SamsungConnectionListener: ConnectionListener {
    override fun onConnectionSuccess() {
        TODO("Not yet implemented")
    }

    override fun onConnectionEnded() {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(e: HealthTrackerException) {
        if (e.errorCode == HealthTrackerException.OLD_PLATFORM_VERSION
            || e.errorCode == HealthTrackerException.PACKAGE_NOT_INSTALLED) {
            Log.e("Samsung conn", "Fail to connect")
        }

        if (e.hasResolution()) {
            Log.e("Samsung conn", "Fail to connect")
        }
    }
}