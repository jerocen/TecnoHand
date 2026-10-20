package com.atom.tecnohandapp.data.network

import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.AlertItem
import com.atom.tecnohandapp.data.models.CreateReportRequest
import com.atom.tecnohandapp.data.models.GenericApiResponse
import com.atom.tecnohandapp.data.models.QrConsultRequest
import com.atom.tecnohandapp.data.models.QrConsultResponse
import com.atom.tecnohandapp.data.models.QrValidationRequest
import com.atom.tecnohandapp.data.models.QrValidationResponse
import com.atom.tecnohandapp.data.models.ReportItem
import com.atom.tecnohandapp.data.models.UpdateReportStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TecnoHandApiService {

    // --- QR Endpoints ---
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

    // --- Reportes / Incidentes Endpoints ---
    @GET("incidentes")
    suspend fun obtenerIncidentes(
        @Query("estado") estado: String? = null,
        @Query("colonia_id") coloniaId: Long = 1
    ): Response<List<ReportItem>>

    @POST("incidentes")
    suspend fun crearIncidente(
        @Body request: CreateReportRequest
    ): Response<GenericApiResponse>

    @PUT("incidentes/{id}/estado")
    suspend fun actualizarEstadoIncidente(
        @Path("id") id: Long,
        @Body request: UpdateReportStatusRequest
    ): Response<GenericApiResponse>

    // --- Alertas y Notificaciones Endpoints ---
    @GET("incidentes/alertas")
    suspend fun obtenerAlertas(): Response<List<AlertItem>>

    @POST("incidentes/alertas/panico")
    suspend fun activarPanico(
        @Body payload: Map<String, String> = mapOf("descripcion" to "¡BOTÓN DE PÁNICO ACTIVADO EN APP!")
    ): Response<GenericApiResponse>
}
