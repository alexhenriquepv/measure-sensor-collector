package br.concy.demo.model.request

import br.concy.demo.model.entity.HrMeasurement
import com.google.gson.annotations.SerializedName

data class HrRequest(
    @SerializedName("patient_id")
    val patientId: Int,
    val measurements: List<HrMeasurement>
)
