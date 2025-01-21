package br.concy.demo.model.entity

import java.util.Date

data class EcgRequest(
    val patient_id: Int,
    val datetime: Date = Date(),
    val measurements: List<EcgMeasurement>
)
