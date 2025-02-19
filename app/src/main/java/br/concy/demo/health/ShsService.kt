package br.concy.demo.health

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import br.concy.demo.TAG
import br.concy.demo.model.entity.HrMeasurement
import br.concy.demo.model.entity.IbiMeasurement
import br.concy.demo.model.repository.HrRepository
import br.concy.demo.model.repository.IbiRepository
import br.concy.demo.util.NotificationHelper
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.ValueKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ShsService: Service() {

    private lateinit var shc: SamsungHealthConnection
    private val hrBuffer = mutableListOf<HrMeasurement>()
    private val ibiBuffer = mutableListOf<IbiMeasurement>()
    private val bufferJob = CoroutineScope(Dispatchers.IO)
    private val samplingFrequency = 1000L
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

    @Inject
    lateinit var hrRepository: HrRepository

    @Inject
    lateinit var ibiRepository: IbiRepository

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
        startBufferJob()
    }

    private val hrListener = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            if (list.isNotEmpty()) {
                for (dp in list) {
                    val hrStatus = dp.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS)
                    val hrValue = dp.getValue(ValueKey.HeartRateSet.HEART_RATE)

                    Log.d(TAG, "status $hrStatus")

                    if (hrStatus == 1) {
                        hrBuffer.add(HrMeasurement(
                            registeredAt = sdf.format(dp.timestamp),
                            measurement = hrValue
                        ))
                    }

                    val validIbiValues = getValidIbi(dp)
                    Log.d(TAG, "ibi value: $validIbiValues")

                    validIbiValues.forEach {
                        Log.d(TAG, "ibi value: $it")
                        ibiBuffer.add(
                            IbiMeasurement(
                                registeredAt = sdf.format(dp.timestamp),
                                measurement = it
                            )
                        )
                    }
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

    private fun getValidIbi(dp: DataPoint) : List<Int> {
        val ibiStatus = dp.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST)
        val ibiValues = dp.getValue(ValueKey.HeartRateSet.IBI_LIST)
        val validIbi = mutableListOf<Int>()

        for ((i, status) in ibiStatus.withIndex()) {
            if (status == 0 && ibiValues[i] != 0) {
                validIbi.add(ibiValues[i])
            } else {
                Log.d(TAG, "Discarding: status: $status, values: ${ibiValues[i]}")
            }
        }

        return validIbi.toList()
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

    private fun processBuffer() {
        if (hrBuffer.isNotEmpty()) {
            val hrMeasurement = hrBuffer.last()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    hrRepository.insert(hrMeasurement)
                    Log.d(TAG, "Inserted new hr measurement")
                    hrBuffer.clear()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }

        if (ibiBuffer.isNotEmpty()) {
            val ibiMeasurement = ibiBuffer.last()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    ibiRepository.insert(ibiMeasurement)
                    Log.d(TAG, "Inserted new ibi measurement")
                    ibiBuffer.clear()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun startBufferJob() {
        bufferJob.launch {
            while (true) {
                processBuffer()
                delay(samplingFrequency)
            }
        }
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