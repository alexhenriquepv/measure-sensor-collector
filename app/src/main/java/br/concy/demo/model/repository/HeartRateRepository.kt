package br.concy.demo.model.repository

import br.concy.demo.model.dao.HeartHateMeasurementDao
import br.concy.demo.model.entity.HeartHateMeasurement
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HeartRateRepository @Inject constructor(
    private val heartRateDao: HeartHateMeasurementDao
) {
    val lastMeasurement: Flow<HeartHateMeasurement?> = heartRateDao.getLastMeasurement()

    suspend fun insert(measurement: HeartHateMeasurement) {
        heartRateDao.insert(measurement)
    }
}