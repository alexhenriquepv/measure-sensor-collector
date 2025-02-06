package br.concy.demo.model.repository

import br.concy.demo.model.dao.IbiMeasurementDao
import br.concy.demo.model.entity.IbiMeasurement
import javax.inject.Inject

class IbiRepository @Inject constructor(
    private val dao: IbiMeasurementDao
) {

    suspend fun insert(item: IbiMeasurement) = dao.insert(item)
    suspend fun deleteSynced() = dao.deleteSynced()
    suspend fun getNotSynced() = dao.getNotSynced()
    suspend fun updateAll(items: List<IbiMeasurement>) = dao.updateAll(items)
}