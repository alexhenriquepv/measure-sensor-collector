package br.concy.demo.model.request

import br.concy.demo.model.entity.IbiMeasurement
import com.google.gson.annotations.SerializedName

data class IbiRequest(
    @SerializedName("patient_id")
    val patientId: Int,
    val measurements: List<IbiMeasurement>
)
