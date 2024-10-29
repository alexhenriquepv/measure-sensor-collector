package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "heart_rate_measurements")
data class HeartHateMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: Date = Date(),
    val bpm: Float
)