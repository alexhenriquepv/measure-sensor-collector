package br.concy.demo.health

import br.concy.demo.model.entity.HeartHateMeasurement
import br.concy.demo.model.response.EcgResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface HeartRateAPIService {
    @POST("register")
    suspend fun sendRegister(
        @Body data: HeartHateMeasurement
    ): EcgResponse
}