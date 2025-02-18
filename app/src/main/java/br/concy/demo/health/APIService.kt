package br.concy.demo.health

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
import br.concy.demo.model.response.RecordResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {

    @GET("patients")
    suspend fun getPatients(): List<Patient>

    @POST("ecg/multiple")
    suspend fun sendEcgData(
        @Body data: EcgRequest
    ) : EcgResponse

    @POST("accelerometer/multiple")
    suspend fun sendAccelData(
        @Body data: AccelRequest
    ) : AccelResponse

    @POST("gyroscope/multiple")
    suspend fun sendGyroData(
        @Body data: GyroRequest
    ) : GyroResponse

    @POST("heart-rate/multiple")
    suspend fun sendHrData(
        @Body data: HrRequest
    ) : HrResponse

    @POST("ibi/multiple")
    suspend fun sendIbiData(
        @Body data: IbiRequest
    ) : IbiResponse

    @Multipart
    @POST("upload_audio.php")
    suspend fun uploadAudio(
        @Part audioFilePart: MultipartBody.Part,
        @Part("id_patient") patientIdPart: RequestBody,
        @Part("time_begin") timeBeginPart: RequestBody
    ): RecordResponse
}