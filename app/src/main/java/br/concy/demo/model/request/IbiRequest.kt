package br.concy.demo.model.request

import br.concy.demo.model.entity.IbiMeasurement
import com.google.gson.annotations.SerializedName

data class IbiRequest(
    @SerializedName("id_patient")
    val patientId: Int,
    val sensor: String = "ibi",
    val measurements: List<IbiMeasurement>
)
