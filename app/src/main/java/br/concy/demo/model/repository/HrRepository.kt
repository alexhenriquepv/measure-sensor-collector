package br.concy.demo.model.repository

import br.concy.demo.model.dao.HrMeasurementDao
import br.concy.demo.model.entity.HrMeasurement
import javax.inject.Inject

class HrRepository @Inject constructor(
    private val dao: HrMeasurementDao
) {
    suspend fun insert(item: HrMeasurement) = dao.insert(item)
    suspend fun deleteSynced() = dao.deleteSynced()
    suspend fun getNotSynced() = dao.getNotSynced()
    suspend fun updateAll(items: List<HrMeasurement>) = dao.updateAll(items)
}