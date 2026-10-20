package com.atom.tecnohandapp.data.models

import com.google.gson.annotations.SerializedName

data class ReportItem(
    @SerializedName("id") val id: Long,
    @SerializedName("colonia_nombre") val coloniaNombre: String?,
    @SerializedName("vivienda_direccion") val viviendaDireccion: String?,
    @SerializedName("reportado_por_nombre") val reportadoPorNombre: String?,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("prioridad") val prioridad: String, // "baja", "media", "alta", "urgente"
    @SerializedName("db_estado") val dbEstado: String, // "abierto", "en_proceso", "resuelto"
    @SerializedName("estado_ui") val estadoUi: String, // "Iniciado", "En Proceso", "Terminado"
    @SerializedName("creado_en") val creadoEn: String,
    @SerializedName("resuelto_en") val resueltoEn: String?
)

data class CreateReportRequest(
    @SerializedName("colonia_id") val coloniaId: Long = 1,
    @SerializedName("vivienda_id") val viviendaId: Long? = null,
    @SerializedName("reportado_por") val reportadoPor: Long = 1,
    @SerializedName("categoria") val categoria: String = "seguridad", // "seguridad", "bienestar", "infraestructura"
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("prioridad") val prioridad: String = "media"
)

data class UpdateReportStatusRequest(
    @SerializedName("estado") val estado: String // "Iniciado", "En Proceso", "Terminado"
)

data class AlertItem(
    @SerializedName("id") val id: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("prioridad") val prioridad: String,
    @SerializedName("estado_ui") val estadoUi: String,
    @SerializedName("db_estado") val dbEstado: String,
    @SerializedName("tipo_alerta") val tipoAlerta: String,
    @SerializedName("color_nivel") val colorNivel: String, // "rojo", "amarillo", "verde"
    @SerializedName("reportado_por") val reportadoPor: String,
    @SerializedName("fecha") val fecha: String
)

data class GenericApiResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("id") val id: Long?
)
