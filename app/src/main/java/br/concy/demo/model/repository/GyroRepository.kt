package br.concy.demo.model.repository

import br.concy.demo.model.dao.GyroMeasurementDao
import br.concy.demo.model.entity.GyroscopeMeasurement
import javax.inject.Inject

class GyroRepository @Inject constructor(
    private val dao: GyroMeasurementDao
) {
    suspend fun insert(item: GyroscopeMeasurement) = dao.insert(item)
    suspend fun deleteSynced() = dao.deleteSynced()
    suspend fun getNotSynced() = dao.getNotSynced()
    suspend fun updateAll(items: List<GyroscopeMeasurement>) = dao.updateAll(items)
}