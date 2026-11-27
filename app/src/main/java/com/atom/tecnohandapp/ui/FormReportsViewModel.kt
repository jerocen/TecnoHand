package com.atom.tecnohandapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import kotlinx.coroutines.withContext
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class FormReportsUiState(
    val categoria: String = "infraestructura",
    val categoriaDisplay: String = "Falla en alumbrado / Infraestructura",
    val descripcion: String = "",
    val direccion: String = "Calle Roble 101, Colonia Los Pinos",
    val prioridad: String = "media",
    val latitude: Double = 19.432608,
    val longitude: Double = -99.133209,
    val isFetchingLocation: Boolean = false,
    val selectedPhotoUri: Uri? = null,
    val selectedPhotoBase64: String? = null, // <--- Agrega esta línea
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

class FormReportsViewModel : ViewModel()
{
    private val _uiState = MutableStateFlow(FormReportsUiState())
    val selectedPhotoBase64: String? = null
    val uiState: StateFlow<FormReportsUiState> = _uiState.asStateFlow()

    fun setCategoria(apiCat: String, displayCat: String)
    {
        _uiState.update { it.copy(categoria = apiCat, categoriaDisplay = displayCat) }
    }

    fun setDescripcion(text: String)
    {
        _uiState.update { it.copy(descripcion = text) }
    }

    fun setDireccion(text: String)
    {
        _uiState.update { it.copy(direccion = text) }
    }

    fun setPrioridad(prio: String)
    {
        _uiState.update { it.copy(prioridad = prio) }
    }

    fun setLocationCoordinates(lat: Double, lng: Double, address: String? = null)
    {
        _uiState.update{
            it.copy(
                latitude = lat,
                longitude = lng,
                direccion = address ?: it.direccion
            )
        }
    }

    fun fetchCurrentGpsLocation(context: Context)
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            _uiState.update { it.copy(errorMessage = "Se requieren permisos de ubicación GPS.") }
            return
        }

        _uiState.update { it.copy(isFetchingLocation = true, errorMessage = null) }

        try
        {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(location != null)
                {
                    val lat = location.latitude
                    val lng = location.longitude

                    var addressText = "Lat: %.4f, Lng: %.4f".format(lat, lng)
                    try
                    {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(lat, lng, 1)
                        if(!addresses.isNullOrEmpty())
                        {
                            val addr = addresses[0]
                            val thoroughfare = addr.thoroughfare ?: ""
                            val locality = addr.subLocality ?: addr.locality ?: ""
                            val formatted = "$thoroughfare, $locality".trim(',', ' ')
                            if(formatted.isNotBlank())
                            {
                                addressText = formatted
                            }
                            else
                            {
                                addressText = addr.getAddressLine(0) ?: addressText
                            }
                        }
                    }
                    catch(e: Exception)
                    {
                        // Fallback
                    }

                    _uiState.update{
                        it.copy(
                            isFetchingLocation = false,
                            latitude = lat,
                            longitude = lng,
                            direccion = addressText
                        )
                    }
                }
                else
                {
                    _uiState.update{
                        it.copy(
                            isFetchingLocation = false,
                            errorMessage = "Obteniendo coordenadas GPS... Asegúrese de tener el GPS activado."
                        )
                    }
                }
            }.addOnFailureListener { e ->
                _uiState.update{
                    it.copy(
                        isFetchingLocation = false,
                        errorMessage = "Error GPS: ${e.localizedMessage}"
                    )
                }
            }
        }
        catch(e: Exception)
        {
            _uiState.update {
                it.copy(
                    isFetchingLocation = false,
                    errorMessage = "Error de ubicación: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun getResizedReportBitmap(context: Context, uri: Uri, maxDimension: Int = 1920): Bitmap?
    {
        return try
        {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDimension || options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            val scaleOptions = BitmapFactory.Options().apply{
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val scaledStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(scaledStream, null, scaleOptions)
            scaledStream.close()
            bitmap
        }
        catch(e: Exception)
        {
            null
        }
    }

    fun setSelectedPhotoUri(context: Context?, uri: Uri?) {
        if (uri == null || context == null) {
            _uiState.update { it.copy(selectedPhotoUri = null, selectedPhotoBase64 = null) }
            return
        }

        _uiState.update { it.copy(selectedPhotoUri = uri, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val (base64Str, err) = uriToBase64(context, uri)
            _uiState.update {
                it.copy(
                    selectedPhotoBase64 = base64Str,
                    errorMessage = err
                )
            }
        }
    }

    fun submitReport(
        context: Context,
        reportsViewModel: ReportsViewModel,
        reportadoPorId: Long,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        if (state.descripcion.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingrese la descripción del incidente.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            // Convierte la foto en este momento si aún no está lista en Base64
            var base64Final = state.selectedPhotoBase64
            if (base64Final == null && state.selectedPhotoUri != null) {
                val (converted, err) = withContext(Dispatchers.IO) {
                    uriToBase64(context, state.selectedPhotoUri)
                }
                if (converted == null) {
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = err ?: "No se pudo procesar la foto")
                    }
                    return@launch
                }
                base64Final = converted
            }
            android.util.Log.d("FOTO_DEBUG", "Enviando reporte. Base64 final: ${base64Final?.length ?: "NULL"}")

            val fullDescription = if (state.direccion.isNotBlank()) {
                "${state.descripcion} [GPS: ${state.latitude}, ${state.longitude} | Ubicación: ${state.direccion}]"
            } else {
                state.descripcion
            }

            reportsViewModel.createReport(
                categoria = state.categoria,
                descripcion = fullDescription,
                prioridad = state.prioridad,
                reportadoPorId = reportadoPorId,
                evidenciaBase64 = base64Final
            )

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    submitSuccess = true,
                    descripcion = "",
                    selectedPhotoUri = null,
                    selectedPhotoBase64 = null
                )
            }

            onSuccess()
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): Pair<String?, String?> {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
                ?: return Pair(null, "No se pudo abrir la foto (stream null)")

            val options = BitmapFactory.Options().apply { inSampleSize = 2 }
            val originalBitmap = stream.use { BitmapFactory.decodeStream(it, null, options) }
                ?: return Pair(null, "Android no pudo decodificar la imagen")

            val maxDim = 800
            val ratio = minOf(
                maxDim.toFloat() / originalBitmap.width,
                maxDim.toFloat() / originalBitmap.height,
                1f
            )
            val scaledBitmap = if (ratio < 1f) {
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    (originalBitmap.width * ratio).toInt().coerceAtLeast(1),
                    (originalBitmap.height * ratio).toInt().coerceAtLeast(1),
                    true
                )
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 65, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            Pair(base64, null)
        } catch (e: Throwable) {
            Pair(null, "Error foto: ${e.javaClass.simpleName} - ${e.message}")
        }
    }

    fun clearMessages()
    {
        _uiState.update { it.copy(errorMessage = null, submitSuccess = false) }
    }
}
