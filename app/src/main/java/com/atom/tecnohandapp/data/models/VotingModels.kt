package com.atom.tecnohandapp.data.models

data class EncuestaDto(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val fecha_inicio: String?,
    val fecha_fin: String,
    val estado: String,
    val votos: Int = 0,
    val total_viviendas: Int = 1,
    val presupuesto: String? = "Por definir"
)

data class CrearEncuestaRequest(
    val titulo: String,
    val descripcion: String,
    val duracion_horas: Long,
    val presupuesto: String? = "Por definir"
)

data class VotarRequest(
    val vivienda_id: Long = 1,
    val emitido_por: Long = 1
)
