package br.concy.demo.model.request

import br.concy.demo.model.entity.AccelMeasurement
import com.google.gson.annotations.SerializedName

data class AccelRequest(
    @SerializedName("patient_id")
    val patientId: Int,
    val measurements: List<AccelMeasurement>
)
