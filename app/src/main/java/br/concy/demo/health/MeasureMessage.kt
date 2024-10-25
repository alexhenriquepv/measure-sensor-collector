package br.concy.demo.health

import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.SampleDataPoint

sealed class MeasureMessage {
    class MeasureAvailability(val availability: DataTypeAvailability) : MeasureMessage()
    class MeasureData(val data: List<SampleDataPoint<Double>>) : MeasureMessage()
}