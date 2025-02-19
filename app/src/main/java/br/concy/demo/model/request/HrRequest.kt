package br.concy.demo.model.request

import br.concy.demo.model.entity.HrMeasurement
import com.google.gson.annotations.SerializedName

data class HrRequest(
    @SerializedName("id_patient")
    val patientId: Int,
    val sensor: String = "hr",
    val measurements: List<HrMeasurement>
)
