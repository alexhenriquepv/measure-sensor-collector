package br.concy.demo.model.repository

import br.concy.demo.model.dao.EcgMeasurementDao
import br.concy.demo.model.entity.EcgMeasurement
import javax.inject.Inject

class EcgRepository @Inject constructor(
    private val ecgMeasurementDao: EcgMeasurementDao
) {
    suspend fun insertAll(items: List<EcgMeasurement>) {
        ecgMeasurementDao.insertAll(items)
    }

    suspend fun getAll(): List<EcgMeasurement> {
        return ecgMeasurementDao.getAll()
    }

    suspend fun deleteAll() {
        ecgMeasurementDao.deleteAll()
    }
}