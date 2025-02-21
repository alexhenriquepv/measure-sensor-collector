package br.concy.demo.model.request

import br.concy.demo.model.entity.EcgMeasurement
import com.google.gson.annotations.SerializedName

data class EcgRequest(
    val measurements: List<EcgMeasurement>
)
