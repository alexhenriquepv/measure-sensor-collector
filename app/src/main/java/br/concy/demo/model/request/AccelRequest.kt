package br.concy.demo.model.request

import com.google.gson.annotations.SerializedName

data class AccelRequest(
    @SerializedName("patient_id")
    val patientId: Int,
    val x: Int,
    val y: Int,
    val z: Int,
    @SerializedName("registered_at")
    val registeredAt: String
)
