package br.concy.demo.model.entity

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AudioRecord(
    @SerializedName("id_patient")
    val patientId: Int,

    @SerializedName("registro_date")
    val registeredAt: Date
)
