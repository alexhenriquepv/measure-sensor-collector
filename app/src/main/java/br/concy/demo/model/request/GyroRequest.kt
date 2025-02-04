package br.concy.demo.model.request

import br.concy.demo.model.entity.GyroscopeMeasurement
import com.google.gson.annotations.SerializedName

data class GyroRequest(
    @SerializedName("patient_id")
    val patientId: Int,
    val measurements: List<GyroscopeMeasurement>
)