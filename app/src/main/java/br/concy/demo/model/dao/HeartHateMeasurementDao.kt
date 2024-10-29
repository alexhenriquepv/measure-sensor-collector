package br.concy.demo.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.concy.demo.model.entity.HeartHateMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartHateMeasurementDao {

    @Query("SELECT * FROM heart_rate_measurements")
    fun getAll(): List<HeartHateMeasurement>

    @Query("SELECT * FROM heart_rate_measurements ORDER BY id DESC LIMIT 1")
    fun getLastMeasurement(): Flow<HeartHateMeasurement>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measurement: HeartHateMeasurement)
}