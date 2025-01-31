package br.concy.demo.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.concy.demo.model.entity.AccelMeasurement

@Dao
interface AccelMeasurementDao {

    @Insert
    suspend fun insert(item: AccelMeasurement)

    @Query("DELETE FROM accel_measurements")
    suspend fun deleteAll()

    @Query("SELECT * FROM accel_measurements")
    suspend fun getAll(): List<AccelMeasurement>
}