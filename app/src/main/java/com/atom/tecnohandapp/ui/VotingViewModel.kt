package com.atom.tecnohandapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.tecnohandapp.data.models.CrearEncuestaRequest
import com.atom.tecnohandapp.data.models.VotarRequest
import com.atom.tecnohandapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class VotingItem(
    val id: String,
    val titulo: String,
    val descripcion: String = "",
    val fechaInicio: LocalDateTime = LocalDateTime.now(),
    val fechaCierre: LocalDateTime,
    val votos: Int = 0,
    val totalViviendas: Int = 1,
    val presupuestoAsignado: String = "Por definir",
    val estado: String = "activa"
)

class VotingViewModel : ViewModel() {

    val totalViviendas = 10

    private val _votingList = MutableStateFlow<List<VotingItem>>(emptyList())
    val votingList: StateFlow<List<VotingItem>> = _votingList.asStateFlow()

    private val _votedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val votedItemIds: StateFlow<Set<String>> = _votedItemIds.asStateFlow()

    init {
        cargarEncuestas()
    }

    fun cargarEncuestas() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getService(RetrofitClient.baseUrl).obtenerEncuestas()
                if (response.isSuccessful && response.body() != null) {
                    val encuestasMapeadas = response.body()!!.map { dto ->
                        val fechaFinLocal = try {
                            Instant.parse(dto.fecha_fin)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                        } catch (e: Exception) {
                            LocalDateTime.now().plusHours(24)
                        }

                        VotingItem(
                            id = dto.id.toString(),
                            titulo = dto.titulo,
                            descripcion = dto.descripcion ?: "",
                            fechaCierre = fechaFinLocal,
                            votos = dto.votos,
                            totalViviendas = dto.total_viviendas.coerceAtLeast(1),
                            presupuestoAsignado = dto.presupuesto?.ifBlank { "Por definir" } ?: "Por definir",
                            estado = dto.estado
                        )
                    }
                    _votingList.value = encuestasMapeadas
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun registrarVoto(itemId: String, cuentaId: Long) {
        val voteKey = "${cuentaId}_${itemId}"
        if (_votedItemIds.value.contains(voteKey)) return

        viewModelScope.launch {
            try {
                val response = RetrofitClient.getService(RetrofitClient.baseUrl)
                    .votarEncuesta(itemId, VotarRequest(vivienda_id = 0, emitido_por = cuentaId))

                if (response.isSuccessful || response.code() == 409) {
                    _votedItemIds.update { it + voteKey }
                    cargarEncuestas()
                } else {
                    android.util.Log.e(
                        "VotingViewModel",
                        "Error al votar (${response.code()}): ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregarEncuesta(
        titulo: String,
        descripcion: String,
        presupuesto: String,
        duracionHoras: Long
    ) {
        viewModelScope.launch {
            try {
                val cleanBudget = presupuesto.trim()
                val descFinal = if (cleanBudget.isNotBlank()) {
                    "${descripcion.trim()}\n[Presupuesto: $cleanBudget]".trim()
                } else {
                    descripcion.trim()
                }

                val request = CrearEncuestaRequest(
                    titulo = titulo.trim(),
                    descripcion = descFinal,
                    presupuesto = presupuesto.trim().ifBlank { "Por definir" },
                    duracion_horas = duracionHoras.coerceAtLeast(1L)
                )
                val response = RetrofitClient.getService(RetrofitClient.baseUrl)
                    .crearEncuesta(request)

                if (response.isSuccessful) {
                    cargarEncuestas()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarEncuesta(itemId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getService(RetrofitClient.baseUrl)
                    .eliminarEncuesta(itemId)

                if (response.isSuccessful) {
                    _votedItemIds.update { it - itemId }
                    cargarEncuestas() // Refresca la lista sin la encuesta eliminada
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun extraerMontoPresupuesto(texto: String): Double
{
    val limpio = texto.replace(Regex("[^0-9.]"), "")
    return limpio.toDoubleOrNull() ?: 0.0
}