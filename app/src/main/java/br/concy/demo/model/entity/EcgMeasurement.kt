package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ecg_measurements")
data class EcgMeasurement(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("registered_at")
    val registeredAt: String,
    val measurement: Float,
)
