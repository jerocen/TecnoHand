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
                            presupuestoAsignado = "Por definir",
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

    fun registrarVoto(itemId: String) {
        if (_votedItemIds.value.contains(itemId)) return

        viewModelScope.launch {
            try {
                val response = RetrofitClient.getService(RetrofitClient.baseUrl)
                    .votarEncuesta(itemId, VotarRequest(vivienda_id = 1, emitido_por = 1))

                if (response.isSuccessful) {
                    _votedItemIds.update { it + itemId }
                    cargarEncuestas() // Refresca los votos desde MySQL
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
                val request = CrearEncuestaRequest(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    duracion_horas = duracionHoras.coerceAtLeast(1L)
                )
                val response = RetrofitClient.getService(RetrofitClient.baseUrl)
                    .crearEncuesta(request)

                if (response.isSuccessful) {
                    cargarEncuestas() // Trae la nueva encuesta recién guardada en MySQL
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