package br.concy.demo.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.concy.demo.model.entity.HrMeasurement

@Dao
interface HrMeasurementDao {

    @Insert
    suspend fun insert(item: HrMeasurement)

    @Query("DELETE FROM hr_measurements WHERE sync = true")
    suspend fun deleteSynced(): Int

    @Query("SELECT * FROM hr_measurements WHERE sync = false")
    suspend fun getNotSynced(): List<HrMeasurement>

    @Update
    suspend fun updateAll(items: List<HrMeasurement>)
}