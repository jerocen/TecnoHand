package com.atom.tecnohandapp.data.models

import com.google.gson.annotations.SerializedName

data class QrConsultRequest(
    @SerializedName("token") val token: String
)

data class QrDetails(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("residente") val residente: String? = null,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("colonia") val colonia: String? = null,
    @SerializedName("colonia_id") val coloniaId: Long? = null,
    @SerializedName("vigente_desde") val vigenteDesde: String? = null,
    @SerializedName("vigente_hasta") val vigenteHasta: String? = null,
    @SerializedName("usos_maximos") val usosMaximos: Int? = null,
    @SerializedName("usos_realizados") val usosRealizados: Int? = null,
    @SerializedName("usos_restantes") val usosRestantes: Int? = null,
    @SerializedName("tipo") val tipo: String? = null,
    @SerializedName("estado") val estado: String? = null
)

data class QrConsultResponse(
    @SerializedName("valido") val valido: Boolean,
    @SerializedName("motivo") val motivo: String? = null,
    @SerializedName("credencial_id") val credencialId: Long? = null,
    @SerializedName("tipo") val tipo: String? = null,
    @SerializedName("detalles") val detalles: QrDetails? = null,
    @SerializedName("error") val error: String? = null
)

data class QrValidationRequest(
    @SerializedName("token") val token: String,
    @SerializedName("colonia_id") val coloniaId: Long = 1,
    @SerializedName("guardia_cuenta_id") val guardiaCuentaId: Long = 5,
    @SerializedName("tipo") val tipo: String = "entrada"
)

data class QrValidationResponse(
    @SerializedName("valido") val valido: Boolean,
    @SerializedName("resultado") val resultado: String? = null, // "permitido" or "rechazado"
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("motivo") val motivo: String? = null,
    @SerializedName("acceso_id") val accesoId: Long? = null,
    @SerializedName("visitante") val visitante: String? = null,
    @SerializedName("usos_restantes") val usosRestantes: Int? = null,
    @SerializedName("detalles") val detalles: QrDetails? = null,
    @SerializedName("error") val error: String? = null
)

data class AccessLogItem(
    @SerializedName("id") val id: Long,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("resultado") val resultado: String,
    @SerializedName("motivo") val motivo: String?,
    @SerializedName("fecha_hora") val fechaHora: String,
    @SerializedName("colonia") val colonia: String,
    @SerializedName("guardia") val guardia: String,
    @SerializedName("persona_nombre") val personaNombre: String?,
    @SerializedName("vivienda_direccion") val viviendaDireccion: String?
)
