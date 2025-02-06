package br.concy.demo.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.concy.demo.model.entity.IbiMeasurement

@Dao
interface IbiMeasurementDao {

    @Insert
    suspend fun insert(item: IbiMeasurement)

    @Query("DELETE FROM ibi_measurements WHERE sync = true")
    suspend fun deleteSynced(): Int

    @Query("SELECT * FROM ibi_measurements WHERE sync = false")
    suspend fun getNotSynced(): List<IbiMeasurement>

    @Update
    suspend fun updateAll(items: List<IbiMeasurement>)
}