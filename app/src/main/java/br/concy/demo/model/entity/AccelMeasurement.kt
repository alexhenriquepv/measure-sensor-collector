package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accel_measurements")
data class AccelMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registeredAt: String,
    val x: Int,
    val y: Int,
    val z: Int
)
