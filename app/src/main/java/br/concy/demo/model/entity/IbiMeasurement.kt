package br.concy.demo.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ibi_measurements")
data class IbiMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("datetime")
    val registeredAt: String,
    @SerializedName("ibi")
    val measurement: Int,
    var sync: Boolean = false
)
