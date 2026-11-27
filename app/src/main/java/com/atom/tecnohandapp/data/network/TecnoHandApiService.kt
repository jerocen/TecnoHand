package com.atom.tecnohandapp.data.network

import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.AdminUserItem
import com.atom.tecnohandapp.data.models.AlertItem
import com.atom.tecnohandapp.data.models.AuthUserResponse
import com.atom.tecnohandapp.data.models.CrearEncuestaRequest
import com.atom.tecnohandapp.data.models.CreateQrRequest
import com.atom.tecnohandapp.data.models.CreateQrResponse
import com.atom.tecnohandapp.data.models.CreateReportRequest
import com.atom.tecnohandapp.data.models.EncuestaDto
import com.atom.tecnohandapp.data.models.GenericApiResponse
import com.atom.tecnohandapp.data.models.LoginRequest
import com.atom.tecnohandapp.data.models.QrConsultRequest
import com.atom.tecnohandapp.data.models.QrConsultResponse
import com.atom.tecnohandapp.data.models.QrValidationRequest
import com.atom.tecnohandapp.data.models.QrValidationResponse
import com.atom.tecnohandapp.data.models.RegisterRequest
import com.atom.tecnohandapp.data.models.ReportItem
import com.atom.tecnohandapp.data.models.ResetPasswordRequest
import com.atom.tecnohandapp.data.models.UpdateProfileRequest
import com.atom.tecnohandapp.data.models.UpdateReportStatusRequest
import com.atom.tecnohandapp.data.models.VotarRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TecnoHandApiService
{
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

    @GET("encuestas")
    suspend fun obtenerEncuestas(): Response<List<EncuestaDto>>

    @POST("encuestas")
    suspend fun crearEncuesta(@Body body: CrearEncuestaRequest): Response<Unit>

    @POST("encuestas/{id}/votar")
    suspend fun votarEncuesta(@Path("id") id: String, @Body body: VotarRequest): Response<Unit>

    //@DELETE("encuestas/{id}")
    //suspend fun eliminarEncuesta(@Path("id") id: String): Response<Unit>

    // --- Auth & Perfil Endpoints ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthUserResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthUserResponse>

    @GET("auth/usuarios")
    suspend fun obtenerUsuarios(): Response<List<AdminUserItem>>

    @PUT("auth/profile/{id}")
    suspend fun actualizarPerfil(
        @Path("id") cuentaId: String,
        @Body request: UpdateProfileRequest
    ): Response<GenericApiResponse>

    @POST("qr/crear")
    suspend fun crearQrInvitado(@Body request: CreateQrRequest): Response<CreateQrResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<GenericApiResponse>

    @DELETE("encuestas/{id}")
    suspend fun eliminarEncuesta(@Path("id") id: String): Response<GenericApiResponse>
}
