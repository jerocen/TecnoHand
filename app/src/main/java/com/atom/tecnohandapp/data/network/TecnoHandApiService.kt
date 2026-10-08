package com.atom.tecnohandapp.data.network

import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.QrConsultRequest
import com.atom.tecnohandapp.data.models.QrConsultResponse
import com.atom.tecnohandapp.data.models.QrValidationRequest
import com.atom.tecnohandapp.data.models.QrValidationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TecnoHandApiService {

    @POST("qr/consultar")
    suspend fun consultarQr(
        @Body request: QrConsultRequest
    ): Response<QrConsultResponse>

    @POST("qr/validar")
    suspend fun validarAcceso(
        @Body request: QrValidationRequest
    ): Response<QrValidationResponse>

    @GET("qr/accesos")
    suspend fun obtenerAccesos(): Response<List<AccessLogItem>>
}
