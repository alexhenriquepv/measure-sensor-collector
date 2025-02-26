package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "skin_temp_measurements")
data class SkinTempMeasurement(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("RegisteredAt")
    val registeredAt: String,

    @SerializedName("Value")
    val measurement: Float,
)
