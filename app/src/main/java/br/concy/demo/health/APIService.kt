package br.concy.demo.health

import br.concy.demo.model.entity.EcgMeasurement
import br.concy.demo.model.entity.HrMeasurement
import br.concy.demo.model.entity.Patient
import br.concy.demo.model.request.AccelRequest
import br.concy.demo.model.request.EcgRequest
import br.concy.demo.model.request.GyroRequest
import br.concy.demo.model.request.HrRequest
import br.concy.demo.model.request.IbiRequest
import br.concy.demo.model.response.AccelResponse
import br.concy.demo.model.response.EcgResponse
import br.concy.demo.model.response.GyroResponse
import br.concy.demo.model.response.HrResponse
import br.concy.demo.model.response.IbiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("patients")
    suspend fun getPatients(): List<Patient>

    @POST("patients/{patientId}/ecg")
    suspend fun sendEcgData(
        @Path("patientId") patientId: Int,
        @Body measurements: List<EcgMeasurement>
    ) : EcgResponse

    @POST("accelerometer/multiple")
    suspend fun sendAccelData(
        @Body data: AccelRequest
    ) : AccelResponse

    @POST("gyroscope/multiple")
    suspend fun sendGyroData(
        @Body data: GyroRequest
    ) : GyroResponse

    @POST("patients/{patientId}/hr")
    suspend fun sendHrData(
        @Path("patientId") patientId: Int,
        @Body measurements: List<HrMeasurement>
    ) : HrResponse

    @POST("ibi/multiple")
    suspend fun sendIbiData(
        @Body data: IbiRequest
    ) : IbiResponse
}