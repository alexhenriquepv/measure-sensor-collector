package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "hr_measurements")
data class HrMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("RegisteredAt")
    val registeredAt: String,

    @SerializedName("Value")
    val measurement: Int,
    var sync: Boolean = false
)
