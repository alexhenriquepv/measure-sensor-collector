package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "gyro_measurements")
data class GyroscopeMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("registered_at")
    val registeredAt: String,

    val x: Float,
    val y: Float,
    val z: Float,
    var sync: Boolean = false
)
