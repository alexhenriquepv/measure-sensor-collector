package br.concy.demo.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.concy.demo.model.entity.GyroscopeMeasurement

@Dao
interface GyroMeasurementDao {

    @Insert
    suspend fun insert(item: GyroscopeMeasurement)

    @Query("DELETE FROM gyro_measurements WHERE sync = true")
    suspend fun deleteSynced(): Int

    @Query("SELECT * FROM gyro_measurements WHERE sync = false")
    suspend fun getNotSynced(): List<GyroscopeMeasurement>

    @Update
    suspend fun updateAll(items: List<GyroscopeMeasurement>)
}