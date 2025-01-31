package br.concy.demo.health

import br.concy.demo.model.entity.Patient
import br.concy.demo.model.request.EcgRequest
import br.concy.demo.model.response.EcgResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EcgAPIService {

    @POST("ecg/multiple")
    suspend fun sendRegister(
        @Body data: EcgRequest
    ) : EcgResponse

    @GET("patients")
    suspend fun getPatients(): List<Patient>
}