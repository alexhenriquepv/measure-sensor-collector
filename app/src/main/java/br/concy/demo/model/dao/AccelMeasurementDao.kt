package br.concy.demo.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.concy.demo.model.entity.AccelMeasurement

@Dao
interface AccelMeasurementDao {

    @Insert
    suspend fun insert(item: AccelMeasurement)

    @Query("DELETE FROM accel_measurements WHERE sync = true")
    suspend fun deleteSynced(): Int

    @Query("SELECT * FROM accel_measurements WHERE sync = false")
    suspend fun getNotSynced(): List<AccelMeasurement>

    @Update
    suspend fun updateAll(items: List<AccelMeasurement>)
}