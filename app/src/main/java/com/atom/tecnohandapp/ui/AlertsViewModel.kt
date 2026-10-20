package com.atom.tecnohandapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.tecnohandapp.data.models.AlertItem
import com.atom.tecnohandapp.data.models.UpdateReportStatusRequest
import com.atom.tecnohandapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlertsUiState(
    val alerts: List<AlertItem> = emptyList(),
    val filteredAlerts: List<AlertItem> = emptyList(),
    val selectedFilter: String = "Todas", // "Todas", "Críticas", "En Proceso", "Terminadas"
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val panicTriggered: Boolean = false
)

class AlertsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    fun setFilter(filter: String) {
        _uiState.update { state ->
            val filtered = filterAlertsList(state.alerts, filter)
            state.copy(selectedFilter = filter, filteredAlerts = filtered)
        }
    }

    private fun filterAlertsList(list: List<AlertItem>, filter: String): List<AlertItem> {
        return when (filter) {
            "Críticas" -> list.filter { it.colorNivel == "rojo" || it.prioridad == "urgente" || it.prioridad == "alta" }
            "En Proceso" -> list.filter { it.estadoUi == "En Proceso" || it.colorNivel == "amarillo" }
            "Terminadas" -> list.filter { it.estadoUi == "Terminado" || it.colorNivel == "verde" }
            else -> list
        }
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService()
                val response = service.obtenerAlertas()

                if (response.isSuccessful && response.body() != null) {
                    val raw = response.body()!!
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            alerts = raw,
                            filteredAlerts = filterAlertsList(raw, state.selectedFilter)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al obtener alertas (${response.code()})"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de red: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun triggerPanicButton() {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService()
                val response = service.activarPanico()

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            panicTriggered = true,
                            successMessage = "¡ALERTA DE PÁNICO Y EMERGENCIA TRANSMITIDA CON ÉXITO!"
                        )
                    }
                    loadAlerts()
                } else {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            errorMessage = "No se pudo transmitir la alerta de pánico"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = "Error de red al activar pánico: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun markAlertAsResolved(alertId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }

            try {
                val service = RetrofitClient.getService()
                val response = service.actualizarEstadoIncidente(
                    id = alertId,
                    request = UpdateReportStatusRequest(estado = "Terminado")
                )

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            successMessage = "Alerta #$alertId marcada como Terminado / Resuelto"
                        )
                    }
                    loadAlerts()
                } else {
                    _uiState.update { it.copy(isActionLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isActionLoading = false) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, panicTriggered = false) }
    }
}
