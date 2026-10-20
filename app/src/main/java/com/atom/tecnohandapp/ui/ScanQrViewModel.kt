package com.atom.tecnohandapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.QrConsultRequest
import com.atom.tecnohandapp.data.models.QrConsultResponse
import com.atom.tecnohandapp.data.models.QrValidationRequest
import com.atom.tecnohandapp.data.models.QrValidationResponse
import com.atom.tecnohandapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScanUiState(
    val lastDetectedToken: String? = null,
    val isLoading: Boolean = false,
    val validationResponse: QrValidationResponse? = null,
    val consultResponse: QrConsultResponse? = null,
    val errorMessage: String? = null,
    val showResultSheet: Boolean = false,
    val showManualDialog: Boolean = false,
    val showServerConfigDialog: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val serverUrl: String = RetrofitClient.baseUrl,
    val coloniaId: Long = 1,
    val guardiaCuentaId: Long = 5,
    val historyLogs: List<AccessLogItem> = emptyList(),
    val isHistoryLoading: Boolean = false
)

class ScanQrViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var lastProcessTime = 0L
    private var lastProcessedToken: String? = null

    fun updateServerUrl(newUrl: String) {
        RetrofitClient.getService(newUrl)
        _uiState.update {
            it.copy(
                serverUrl = RetrofitClient.baseUrl,
                showServerConfigDialog = false
            )
        }
    }

    fun onQrDetected(code: String) {
        val now = System.currentTimeMillis()
        if (_uiState.value.showResultSheet || _uiState.value.isLoading) return
        if (code == lastProcessedToken && now - lastProcessTime < 3000) return

        lastProcessTime = now
        lastProcessedToken = code

        _uiState.update {
            it.copy(
                lastDetectedToken = code,
                errorMessage = null
            )
        }

        validarAcceso(code, "entrada")
    }

    fun validarAcceso(token: String, tipo: String = "entrada") {
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El código QR no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService(_uiState.value.serverUrl)
                val request = QrValidationRequest(
                    token = token.trim(),
                    coloniaId = _uiState.value.coloniaId,
                    guardiaCuentaId = _uiState.value.guardiaCuentaId,
                    tipo = tipo
                )

                val response = service.validarAcceso(request)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            validationResponse = result,
                            showResultSheet = true,
                            showManualDialog = false
                        )
                    }
                } else {
                    val errorStr = response.errorBody()?.string() ?: "Error de comunicación con el servidor (${response.code()})"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorStr,
                            validationResponse = QrValidationResponse(
                                valido = false,
                                resultado = "rechazado",
                                mensaje = "Error al procesar el escaneo",
                                motivo = errorStr
                            ),
                            showResultSheet = true
                        )
                    }
                }
            } catch (e: Exception) {
                val msg = e.localizedMessage ?: "No se pudo conectar con el servidor en ${_uiState.value.serverUrl}"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = msg,
                        validationResponse = QrValidationResponse(
                            valido = false,
                            resultado = "error",
                            mensaje = "Error de conexión",
                            motivo = "$msg. Verifique que la API esté corriendo y la IP sea correcta."
                        ),
                        showResultSheet = true
                    )
                }
            }
        }
    }

    fun consultarQr(token: String) {
        if (token.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val service = RetrofitClient.getService(_uiState.value.serverUrl)
                val response = service.consultarQr(QrConsultRequest(token.trim()))

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            consultResponse = result,
                            showResultSheet = true
                        )
                    }
                } else {
                    val errorStr = response.errorBody()?.string() ?: "Código QR no encontrado"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorStr
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun loadAccessHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHistoryLoading = true) }

            try {
                val service = RetrofitClient.getService(_uiState.value.serverUrl)
                val response = service.obtenerAccesos()

                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isHistoryLoading = false,
                            historyLogs = response.body()!!,
                            showHistoryDialog = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isHistoryLoading = false,
                            showHistoryDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isHistoryLoading = false,
                        showHistoryDialog = true
                    )
                }
            }
        }
    }

    fun setShowManualDialog(show: Boolean) {
        _uiState.update { it.copy(showManualDialog = show) }
    }

    fun setShowServerConfigDialog(show: Boolean) {
        _uiState.update { it.copy(showServerConfigDialog = show) }
    }

    fun setShowHistoryDialog(show: Boolean) {
        _uiState.update { it.copy(showHistoryDialog = show) }
    }

    fun dismissResultSheet() {
        _uiState.update {
            it.copy(
                showResultSheet = false,
                validationResponse = null,
                consultResponse = null,
                errorMessage = null
            )
        }
        lastProcessedToken = null
    }
}
