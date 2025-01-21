package br.concy.demo.model.request

import br.concy.demo.model.entity.EcgMeasurement
import java.util.Date

data class EcgRequest(
    val patient_id: Int,
    val datetime: Date = Date(),
    val measurements: List<EcgMeasurement>
)
