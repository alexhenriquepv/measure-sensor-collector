package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ecg_measurements")
data class EcgMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registered_at: String,
    val measurement: Float,
)
