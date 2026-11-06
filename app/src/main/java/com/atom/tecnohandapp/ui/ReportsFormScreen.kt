package com.atom.tecnohandapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)
private val BorderGrayColor = Color(0xFFCBD5E1)

data class NeighborhoodPreset(
    val nombreEs: String,
    val nombreEn: String,
    val direccion: String,
    val lat: Double,
    val lng: Double
)

private val neighborhoodPresets = listOf(
    NeighborhoodPreset("Caseta Principal", "Main Gate", "Caseta de Vigilancia, Av. Los Pinos 100", 19.432608, -99.133209),
    NeighborhoodPreset("Parque Central", "Central Park", "Área Verde y Jardines, Calle Roble 205", 19.434100, -99.131500),
    NeighborhoodPreset("Canchas Deportivas", "Sports Courts", "Canchas de Tenis, Pasaje Encino 42", 19.431200, -99.134800),
    NeighborhoodPreset("Zona Residencial Este", "East Area", "Paseo de los Fresnos 88", 19.435500, -99.129800),
    NeighborhoodPreset("Juegos Infantiles", "Playground Area", "Plaza Central, Calle Sauces 12", 19.430100, -99.136000)
)

@Composable
fun ReportsFormScreen(
    formViewModel: FormReportsViewModel = viewModel(),
    reportsViewModel: ReportsViewModel = viewModel(),
    lang: AppLanguage = AppLanguage.SPANISH,
    onBack: () -> Unit = {}
) {
    val uiState by formViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var dropdownExpanded by remember { mutableStateOf(false) }

    val categoriesMap = listOf(
        Pair("infraestructura", if (StringsHelper.isEnglish(lang)) "Lighting Fault / Infrastructure" else "Falla en alumbrado / Infraestructura"),
        Pair("seguridad", if (StringsHelper.isEnglish(lang)) "Security & Surveillance" else "Seguridad y vigilancia"),
        Pair("bienestar", if (StringsHelper.isEnglish(lang)) "Noise / Community Welfare" else "Ruido / Convivencia"),
        Pair("infraestructura", if (StringsHelper.isEnglish(lang)) "Common Area Maintenance" else "Mantenimiento de áreas comunes")
    )

    val prioritiesList = listOf("baja", "media", "alta", "urgente")

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            formViewModel.fetchCurrentGpsLocation(context)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            formViewModel.setSelectedPhotoUri(context, uri)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar with Back Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = StringsHelper.generateReportTitle(lang),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = StringsHelper.generateReportSub(lang),
                        fontSize = 11.sp,
                        color = SubtitleGrayColor
                    )
                }
            }

            // Categoría del incidente
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = StringsHelper.categoryLabel(lang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp))
                            .clickable { dropdownExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.categoriaDisplay,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Desplegar categorías",
                                tint = SubtitleGrayColor
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        categoriesMap.forEach { (apiCat, displayCat) ->
                            DropdownMenuItem(
                                text = { Text(displayCat, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    formViewModel.setCategoria(apiCat, displayCat)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Prioridad del incidente
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = StringsHelper.priorityLabel(lang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    prioritiesList.forEach { prio ->
                        val isSelected = uiState.prioridad == prio
                        val prioText = when (prio) {
                            "baja" -> if (StringsHelper.isEnglish(lang)) "LOW" else "BAJA"
                            "media" -> if (StringsHelper.isEnglish(lang)) "MEDIUM" else "MEDIA"
                            "alta" -> if (StringsHelper.isEnglish(lang)) "HIGH" else "ALTA"
                            else -> if (StringsHelper.isEnglish(lang)) "URGENT" else "URGENTE"
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ConfirmButtonColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { formViewModel.setPrioridad(prio) }
                        ) {
                            Text(
                                text = prioText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else SubtitleGrayColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Descripción del reporte
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = StringsHelper.descriptionLabel(lang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = { formViewModel.setDescripcion(it) },
                    placeholder = {
                        Text(
                            text = StringsHelper.descriptionPlaceholder(lang),
                            fontSize = 13.sp,
                            color = SubtitleGrayColor
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ConfirmButtonColor,
                        unfocusedBorderColor = BorderGrayColor,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Evidencia fotográfica (hasta 30MB / 3000x3000px)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = StringsHelper.photoEvidenceLabel(lang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (uiState.selectedPhotoUri != null) {
                    // Show Preview Thumbnail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ConfirmButtonColor, RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = uiState.selectedPhotoUri),
                            contentDescription = "Foto adjunta",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        IconButton(
                            onClick = { formViewModel.setSelectedPhotoUri(null, null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remover", tint = Color.White)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Adjuntar fotos",
                                tint = ConfirmButtonColor,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = StringsHelper.tapToAttachPhoto(lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = SubtitleGrayColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Ubicación del incidente con GPS, Presets Simulados y Mapa OpenStreetMap
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = StringsHelper.locationLabel(lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Button to Fetch GPS Location
                    TextButton(
                        onClick = {
                            val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            if (fineGranted || coarseGranted) {
                                formViewModel.fetchCurrentGpsLocation(context)
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        if (uiState.isFetchingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = ConfirmButtonColor)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(StringsHelper.useMyGps(lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ConfirmButtonColor)
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.direccion,
                    onValueChange = { formViewModel.setDireccion(it) },
                    placeholder = {
                        Text(
                            text = if (StringsHelper.isEnglish(lang)) "Street, exterior number or area reference" else "Calle, número exterior o referencia de la zona",
                            fontSize = 13.sp,
                            color = SubtitleGrayColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Pin de ubicación",
                            tint = ConfirmButtonColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ConfirmButtonColor,
                        unfocusedBorderColor = BorderGrayColor,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Simulated Neighborhood Location Presets
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = StringsHelper.simulatedPointsLabel(lang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(neighborhoodPresets) { preset ->
                            val isSelected = uiState.latitude == preset.lat && uiState.longitude == preset.lng
                            val name = if (StringsHelper.isEnglish(lang)) preset.nombreEn else preset.nombreEs

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) ConfirmButtonColor else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) ConfirmButtonColor else BorderGrayColor),
                                modifier = Modifier.clickable {
                                    formViewModel.setLocationCoordinates(
                                        lat = preset.lat,
                                        lng = preset.lng,
                                        address = preset.direccion
                                    )
                                }
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else SubtitleGrayColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Real OpenStreetMap Cartography Map Container (100% Free & Immediate streets!)
                OsmMapViewContainer(
                    latitude = uiState.latitude,
                    longitude = uiState.longitude,
                    direccion = uiState.direccion,
                    onMapClick = { lat, lng ->
                        formViewModel.setLocationCoordinates(
                            lat,
                            lng,
                            "Lat: %.4f, Lng: %.4f".format(lat, lng)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Error Message if any
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Submit Button
            Button(
                onClick = {
                    formViewModel.submitReport(reportsViewModel) {
                        onBack()
                    }
                },
                enabled = !uiState.isSubmitting && uiState.descripcion.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ConfirmButtonColor
                )
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = StringsHelper.submitReportBtn(lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun OsmMapViewContainer(
    latitude: Double,
    longitude: Double,
    direccion: String,
    onMapClick: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp))
    ) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(16.5)
                    controller.setCenter(GeoPoint(latitude, longitude))

                    val marker = Marker(this).apply {
                        position = GeoPoint(latitude, longitude)
                        title = "Ubicación del Incidente"
                        snippet = direccion
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    overlays.add(marker)

                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            if (p != null) {
                                onMapClick(p.latitude, p.longitude)
                            }
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint?): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(mapEventsReceiver))
                }
            },
            update = { mapView ->
                mapView.controller.setCenter(GeoPoint(latitude, longitude))
                mapView.overlays.filterIsInstance<Marker>().firstOrNull()?.apply {
                    position = GeoPoint(latitude, longitude)
                    snippet = direccion
                }
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
