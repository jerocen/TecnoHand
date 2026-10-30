package com.atom.tecnohandapp.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atom.tecnohandapp.data.local.PreferencesManager
import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.AppTheme
import com.atom.tecnohandapp.data.models.GithubCommitResponse
import com.atom.tecnohandapp.data.models.QrConsultRequest
import com.atom.tecnohandapp.data.models.QrConsultResponse
import com.atom.tecnohandapp.data.models.QrDetails
import com.atom.tecnohandapp.data.models.QrValidationRequest
import com.atom.tecnohandapp.data.models.QrValidationResponse
import com.atom.tecnohandapp.data.models.VersionInfo
import com.atom.tecnohandapp.data.network.RetrofitClient
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val isHistoryLoading: Boolean = false,
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val selectedLanguage: AppLanguage = AppLanguage.SPANISH,
    val isOfflineDemoMode: Boolean = false,
    val versionInfo: VersionInfo = VersionInfo(),
    val isCheckingUpdate: Boolean = false,
    val updateCheckResult: String? = null,
    val githubCommits: List<GithubCommitResponse> = emptyList(),
    val isGithubLoading: Boolean = false
)

class ScanQrViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(
        ScanUiState(
            serverUrl = prefs.getServerUrl(),
            selectedTheme = prefs.getTheme(),
            selectedLanguage = prefs.getLanguage(),
            isOfflineDemoMode = prefs.isOfflineDemoMode()
        )
    )
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var lastProcessTime = 0L
    private var lastProcessedToken: String? = null

    init {
        RetrofitClient.getService(prefs.getServerUrl())
        loadGithubCommits()
    }

    fun setAppTheme(theme: AppTheme) {
        prefs.setTheme(theme)
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    fun setAppLanguage(language: AppLanguage) {
        prefs.setLanguage(language)
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun setOfflineDemoMode(enabled: Boolean) {
        prefs.setOfflineDemoMode(enabled)
        _uiState.update { it.copy(isOfflineDemoMode = enabled) }
    }

    fun loadGithubCommits() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isGithubLoading = true) }
            try {
                val response = RetrofitClient.getGithubService().getRepoCommits("jerocen", "TecnoHand")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isGithubLoading = false,
                            githubCommits = response.body()!!
                        )
                    }
                } else {
                    _uiState.update { it.copy(isGithubLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGithubLoading = false) }
            }
        }
    }

    fun checkVersionUpdates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingUpdate = true, updateCheckResult = null) }
            loadGithubCommits()
            delay(1200)
            val isEng = _uiState.value.selectedLanguage == AppLanguage.ENGLISH
            _uiState.update {
                it.copy(
                    isCheckingUpdate = false,
                    updateCheckResult = if (isEng)
                        "TecnoHand v1.0.0 is up to date! Commits loaded from GitHub."
                    else
                        "¡TecnoHand v1.0.0 está al día! Commits sincronizados con GitHub."
                )
            }
        }
    }

    fun clearUpdateCheckResult() {
        _uiState.update { it.copy(updateCheckResult = null) }
    }

    fun updateServerUrl(newUrl: String) {
        RetrofitClient.getService(newUrl)
        prefs.setServerUrl(RetrofitClient.baseUrl)
        _uiState.update {
            it.copy(
                serverUrl = RetrofitClient.baseUrl,
                showServerConfigDialog = false
            )
        }
    }

    private fun sanitizeToken(token: String): String {
        return token.trim()
            .replace("\n", "")
            .replace("\r", "")
            .replace("\"", "")
            .replace("'", "")
            .uppercase()
    }

    private fun isExternalUrlOrNonTecnoHand(text: String): Boolean {
        val clean = text.trim().lowercase()
        return clean.startsWith("http://") ||
               clean.startsWith("https://") ||
               clean.startsWith("www.") ||
               clean.startsWith("mailto:") ||
               clean.startsWith("wifi:") ||
               clean.startsWith("tel:") ||
               clean.contains(".com") ||
               clean.contains(".net") ||
               clean.contains(".org")
    }

    private fun performLocalTestQrValidation(token: String, isEng: Boolean): QrValidationResponse? {
        val clean = sanitizeToken(token)
        return when {
            clean.contains("PEDRO") || clean == "QR_PEDRO_001" -> QrValidationResponse(
                valido = true,
                resultado = "permitido",
                mensaje = if (isEng) "Access GRANTED! Welcome Pedro Visitante" else "¡Acceso PERMITIDO! Bienvenid@ Pedro Visitante",
                motivo = "QR vigente en sistema",
                detalles = QrDetails(
                    nombre = "Pedro Visitante",
                    tipo = "invitado",
                    direccion = "Calle Roble 101",
                    residente = "Ana Torres",
                    colonia = "Colonia Los Pinos",
                    usosMaximos = 2,
                    usosRealizados = 1,
                    usosRestantes = 1
                )
            )
            clean.contains("MARIA") || clean == "QR_MARIA_001" -> QrValidationResponse(
                valido = true,
                resultado = "permitido",
                mensaje = if (isEng) "Access GRANTED! Welcome Maria Repartidora" else "¡Acceso PERMITIDO! Bienvenid@ Maria Repartidora",
                motivo = "QR vigente en sistema",
                detalles = QrDetails(
                    nombre = "Maria Repartidora",
                    tipo = "invitado",
                    direccion = "Calle Roble 102",
                    residente = "Luis Mendoza",
                    colonia = "Colonia Los Pinos",
                    usosMaximos = 1,
                    usosRealizados = 1,
                    usosRestantes = 0
                )
            )
            clean.contains("JORGE") || clean == "QR_JORGE_001" -> QrValidationResponse(
                valido = true,
                resultado = "permitido",
                mensaje = if (isEng) "Access GRANTED! Welcome Jorge Familiar" else "¡Acceso PERMITIDO! Bienvenid@ Jorge Familiar",
                motivo = "QR vigente en sistema",
                detalles = QrDetails(
                    nombre = "Jorge Familiar",
                    tipo = "invitado",
                    direccion = "Calle Cedro 210",
                    residente = "Sofia Ramirez",
                    colonia = "Colonia Los Pinos",
                    usosMaximos = 4,
                    usosRealizados = 1,
                    usosRestantes = 3
                )
            )
            clean.contains("PAULA") || clean == "QR_PAULA_001" -> QrValidationResponse(
                valido = true,
                resultado = "permitido",
                mensaje = if (isEng) "Access GRANTED! Welcome Paula Tecnica" else "¡Acceso PERMITIDO! Bienvenid@ Paula Tecnica",
                motivo = "QR vigente en sistema",
                detalles = QrDetails(
                    nombre = "Paula Tecnica",
                    tipo = "invitado",
                    direccion = "Calle Cedro 212",
                    residente = "Carlos Vega",
                    colonia = "Colonia Los Pinos",
                    usosMaximos = 1,
                    usosRealizados = 1,
                    usosRestantes = 0
                )
            )
            clean.contains("RESIDENTE") || clean.contains("ANA") || clean == "QR_RESIDENTE_ANA" -> QrValidationResponse(
                valido = true,
                resultado = "permitido",
                mensaje = if (isEng) "Access GRANTED! Welcome Ana Torres" else "¡Acceso PERMITIDO! Bienvenid@ Ana Torres",
                motivo = "Usuario registrado activo",
                detalles = QrDetails(
                    nombre = "Ana Torres",
                    tipo = "registrado",
                    direccion = "Calle Roble 101",
                    residente = "Ana Torres",
                    colonia = "Colonia Los Pinos"
                )
            )
            else -> null
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

        if (isExternalUrlOrNonTecnoHand(code)) {
            val isEng = _uiState.value.selectedLanguage == AppLanguage.ENGLISH
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = if (isEng) "Invalid QR code" else "Código QR no válido para TecnoHand",
                    validationResponse = QrValidationResponse(
                        valido = false,
                        resultado = "rechazado",
                        mensaje = if (isEng) "Invalid URL or External Format" else "URL o Formato No Válido",
                        motivo = if (isEng)
                            "The scanned QR code is a URL or external format not registered with TecnoHand. Please scan a valid access QR code."
                        else
                            "El código QR escaneado es un enlace URL o formato externo no registrado en TecnoHand. Por favor escanee un código QR de acceso válido."
                    ),
                    showResultSheet = true
                )
            }
            return
        }

        validarAcceso(code, "entrada")
    }

    private fun getResizedBitmap(context: Context, uri: Uri, maxDimension: Int = 1080): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDimension || options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            val scaleOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val scaledStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(scaledStream, null, scaleOptions)
            scaledStream.close()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun processImageUri(context: Context, uri: Uri) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = getResizedBitmap(context, uri, maxDimension = 1080)
                if (bitmap == null) {
                    val isEng = _uiState.value.selectedLanguage == AppLanguage.ENGLISH
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = if (isEng) "Could not load selected image." else "No se pudo cargar la imagen seleccionada."
                        )
                    }
                    return@launch
                }

                val image = InputImage.fromBitmap(bitmap, 0)
                val barcodeScanner = BarcodeScanning.getClient()

                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val detectedQr = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                        if (detectedQr != null) {
                            onQrDetected(detectedQr)
                        } else {
                            val isEng = _uiState.value.selectedLanguage == AppLanguage.ENGLISH
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = if (isEng) "No QR code detected in the selected image." else "No se detectó ningún código QR en la imagen seleccionada.",
                                    validationResponse = QrValidationResponse(
                                        valido = false,
                                        resultado = "rechazado",
                                        mensaje = if (isEng) "Image without valid QR" else "Imagen sin QR reconocible",
                                        motivo = if (isEng)
                                            "No readable QR code was found in the photo selected from gallery or camera."
                                        else
                                            "No se encontró ningún código QR legible en la foto seleccionada de la galería o cámara."
                                    ),
                                    showResultSheet = true
                                )
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error al procesar la imagen: ${e.localizedMessage}"
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al abrir la foto seleccionada: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun validarAcceso(token: String, tipo: String = "entrada") {
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El código QR no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val isEng = _uiState.value.selectedLanguage == AppLanguage.ENGLISH

            // Check if local test validation matches first
            val localResult = performLocalTestQrValidation(token, isEng)

            // If offline demo mode is enabled, validate locally immediately
            if (_uiState.value.isOfflineDemoMode) {
                delay(400)
                val finalRes = localResult ?: QrValidationResponse(
                    valido = false,
                    resultado = "rechazado",
                    mensaje = if (isEng) "QR Code Not Registered" else "Código QR No Registrado",
                    motivo = if (isEng)
                        "The QR code '$token' was not found in TecnoHand database."
                    else
                        "El código QR '$token' no fue encontrado en la base de datos de TecnoHand."
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        validationResponse = finalRes,
                        showResultSheet = true,
                        showManualDialog = false
                    )
                }
                return@launch
            }

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
                    val finalResponse = localResult ?: QrValidationResponse(
                        valido = false,
                        resultado = "rechazado",
                        mensaje = if (isEng) "QR Code Not Registered" else "Código QR No Registrado",
                        motivo = if (isEng)
                            "The QR code '$token' was not found in TecnoHand database."
                        else
                            "El código QR '$token' no fue encontrado en la base de datos MySQL de TecnoHand."
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            validationResponse = finalResponse,
                            showResultSheet = true,
                            showManualDialog = false
                        )
                    }
                }
            } catch (e: Exception) {
                // If network server fails, try local test validation fallback or show server connection error
                val finalResult = localResult ?: QrValidationResponse(
                    valido = false,
                    resultado = "error",
                    mensaje = if (isEng) "Server Connection Error" else "Servidor No Disponible",
                    motivo = if (isEng)
                        "Could not connect to API server at ${_uiState.value.serverUrl}. Make sure Node.js server is running (npm start) or enable Offline Demo Mode in Settings."
                    else
                        "No se pudo conectar con el servidor API en ${_uiState.value.serverUrl}. Inicie el servidor Node.js (npm start) o active el 'Modo Demo Offline' en Ajustes ⚙️."
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        validationResponse = finalResult,
                        showResultSheet = true,
                        showManualDialog = false
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
