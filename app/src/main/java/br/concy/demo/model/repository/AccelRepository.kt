package br.concy.demo.model.repository

import br.concy.demo.model.dao.AccelMeasurementDao
import br.concy.demo.model.entity.AccelMeasurement
import javax.inject.Inject

class AccelRepository @Inject constructor(
    private val dao: AccelMeasurementDao
) {
    suspend fun insert(item: AccelMeasurement) = dao.insert(item)
    suspend fun deleteSynced() = dao.deleteSynced()
    suspend fun getNotSynced() = dao.getNotSynced()
    suspend fun updateAll(items: List<AccelMeasurement>) = dao.updateAll(items)
}