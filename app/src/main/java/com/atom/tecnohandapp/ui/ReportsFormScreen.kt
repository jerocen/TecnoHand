package com.atom.tecnohandapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)
private val BorderGrayColor = Color(0xFFCBD5E1)

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
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ConfirmButtonColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { formViewModel.setPrioridad(prio) }
                        ) {
                            Text(
                                text = prio.uppercase(),
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

            // Ubicación del incidente con GPS y Google Maps
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
                            text = "Calle, número exterior o referencia de la zona",
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

                Spacer(modifier = Modifier.height(4.dp))

                // Interactive Google Map View Container
                SafeGoogleMapContainer(
                    latitude = uiState.latitude,
                    longitude = uiState.longitude,
                    direccion = uiState.direccion,
                    onMapClick = { latLng ->
                        formViewModel.setLocationCoordinates(
                            latLng.latitude,
                            latLng.longitude,
                            "Lat: %.4f, Lng: %.4f".format(latLng.latitude, latLng.longitude)
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
private fun SafeGoogleMapContainer(
    latitude: Double,
    longitude: Double,
    direccion: String,
    onMapClick: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 15f)
    }

    LaunchedEffect(latitude, longitude) {
        try {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f)
            )
        } catch (e: Throwable) {
            // Ignore camera animation error
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = onMapClick
        ) {
            Marker(
                state = MarkerState(position = LatLng(latitude, longitude)),
                title = "Ubicación del Incidente",
                snippet = direccion
            )
        }
    }
}
