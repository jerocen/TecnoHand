package com.atom.tecnohandapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.tecnohandapp.data.models.CreateReportRequest
import com.atom.tecnohandapp.data.models.ReportItem
import com.atom.tecnohandapp.data.models.UpdateReportStatusRequest
import com.atom.tecnohandapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsUiState(
    val reports: List<ReportItem> = emptyList(),
    val selectedTab: String = "Todos", // "Todos", "Iniciado", "En Proceso", "Terminado"
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val selectedReportForDetail: ReportItem? = null
)

class ReportsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun setTabFilter(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
        val apiFilter = when (tab) {
            "Iniciado" -> "Iniciado"
            "En Proceso" -> "En Proceso"
            "Terminado" -> "Terminado"
            else -> null
        }
        loadReports(apiFilter)
    }

    fun loadReports(estadoFilter: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService()
                val filter = estadoFilter ?: when (_uiState.value.selectedTab) {
                    "Iniciado" -> "Iniciado"
                    "En Proceso" -> "En Proceso"
                    "Terminado" -> "Terminado"
                    else -> null
                }

                val response = service.obtenerIncidentes(estado = filter)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reports = response.body()!!
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al obtener reportes: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de conexión: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun createReport(categoria: String, descripcion: String, prioridad: String) {
        if (descripcion.isBlank()) {
            _uiState.update { it.copy(errorMessage = "La descripción no puede estar vacía") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService()
                val request = CreateReportRequest(
                    categoria = categoria,
                    descripcion = descripcion,
                    prioridad = prioridad
                )

                val response = service.crearIncidente(request)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            showCreateDialog = false,
                            successMessage = "Reporte creado exitosamente en estado 'Iniciado'"
                        )
                    }
                    loadReports()
                } else {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            errorMessage = "No se pudo crear el reporte: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = "Error de red: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun updateReportStatus(reportId: Long, newStatus: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService()
                val response = service.actualizarEstadoIncidente(
                    id = reportId,
                    request = UpdateReportStatusRequest(estado = newStatus)
                )

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            selectedReportForDetail = null,
                            successMessage = "Estatus actualizado a '$newStatus'"
                        )
                    }
                    loadReports()
                } else {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            errorMessage = "Error al actualizar estatus"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = "Error de red: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun setShowCreateDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateDialog = show) }
    }

    fun setSelectedReportDetail(report: ReportItem?) {
        _uiState.update { it.copy(selectedReportForDetail = report) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
