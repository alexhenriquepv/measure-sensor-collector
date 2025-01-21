package br.concy.demo.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.concy.demo.model.entity.EcgMeasurement

@Dao
interface EcgMeasurementDao {

    @Insert
    suspend fun insertAll(items: List<EcgMeasurement>)

    @Query("DELETE FROM ecg_measurements")
    suspend fun deleteAll()

    @Query("SELECT * FROM ecg_measurements")
    suspend fun getAll(): List<EcgMeasurement>
}