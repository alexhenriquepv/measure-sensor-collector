package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "accel_measurements")
data class AccelMeasurement(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("RegisteredAt")
    val registeredAt: String,

    @SerializedName("X")
    val x: Float,

    @SerializedName("Y")
    val y: Float,

    @SerializedName("Z")
    val z: Float,
    var sync: Boolean = false
)
