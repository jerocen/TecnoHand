package com.atom.tecnohandapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.AppTheme
import com.atom.tecnohandapp.data.models.QrValidationResponse
import com.atom.tecnohandapp.ui.ScanQrViewModel
import com.atom.tecnohandapp.ui.ScanUiState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)
private val SuccessGreenColor = Color(0xFF10B981)
private val ErrorRedColor = Color(0xFFEF4444)

@Composable
fun ScanQrScreen(
    viewModel: ScanQrViewModel = viewModel(),
    onConfirmScan: () -> Unit = {},
    onManualInput: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val lang = uiState.selectedLanguage

    var hasCameraPermission by remember {
        mutableStateOf(
            if (isPreview) true else
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.processImageUri(context, uri)
        }
    }

    LaunchedEffect(Unit) {
        if (!isPreview && !hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Camera Preview Box ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = ConfirmButtonColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                if (LocalInspectionMode.current) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF222B3D))
                    )
                } else {
                    CameraPreviewView(
                        modifier = Modifier.fillMaxSize(),
                        onQrCodeDetected = { code ->
                            viewModel.onQrDetected(code)
                        }
                    )
                }
                // Overlaid QR Framing Reticle
                QrScannerOverlay(
                    modifier = Modifier.fillMaxSize(),
                    bracketColor = ConfirmButtonColor
                )

                // Loading overlay or Detected status bar
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = StringsHelper.verifyingServer(lang),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    // Text at the bottom inside camera preview
                    val statusText = if (!uiState.lastDetectedToken.isNullOrBlank()) {
                        "${StringsHelper.detected(lang)} ${uiState.lastDetectedToken}"
                    } else {
                        StringsHelper.pointCamera(lang)
                    }

                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            } else {
                // Fallback UI when permission is not granted
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Permiso de cámara necesario para escanear el código QR.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Otorgar Permiso",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Confirm Scan Button ---
        Button(
            onClick = {
                val token = uiState.lastDetectedToken
                if (!token.isNullOrBlank()) {
                    viewModel.validarAcceso(token, "entrada")
                } else {
                    viewModel.setShowManualDialog(true)
                }
                onConfirmScan()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ConfirmButtonColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = StringsHelper.confirmScan(lang),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Manual Input Button ---
        OutlinedButton(
            onClick = {
                viewModel.setShowManualDialog(true)
                onManualInput()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = StringsHelper.manualInput(lang),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Gallery / Photo QR Picker Button ---
        OutlinedButton(
            onClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.2.dp, ConfirmButtonColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ConfirmButtonColor.copy(alpha = 0.08f),
                contentColor = ConfirmButtonColor
            )
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = ConfirmButtonColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = StringsHelper.scanFromGallery(lang),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ConfirmButtonColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Footer Hint Text ---
        Text(
            text = StringsHelper.footerHint(lang),
            color = SubtitleGrayColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }

    // --- Dialogs & Result Sheets ---
    if (uiState.showResultSheet && uiState.validationResponse != null) {
        ScanResultDialog(
            response = uiState.validationResponse!!,
            lang = lang,
            onDismiss = { viewModel.dismissResultSheet() }
        )
    }

    if (uiState.showManualDialog) {
        ManualInputDialog(
            lang = lang,
            onDismiss = { viewModel.setShowManualDialog(false) },
            onValidar = { token ->
                viewModel.validarAcceso(token)
            }
        )
    }
}

@Composable
private fun ScanResultDialog(
    response: QrValidationResponse,
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    val isPermitido = response.valido || response.resultado == "permitido"
    val headerBgColor = if (isPermitido) SuccessGreenColor else ErrorRedColor
    val headerTitle = if (isPermitido) StringsHelper.accessGranted(lang) else StringsHelper.accessDenied(lang)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(StringsHelper.understandBtn(lang), color = Color.White)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isPermitido) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = headerBgColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = headerTitle,
                    color = headerBgColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = response.mensaje ?: response.motivo ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                response.detalles?.let { det ->
                    DetailRow(label = "Persona:", value = det.nombre ?: "N/A")
                    DetailRow(
                        label = "Tipo:",
                        value = if (det.tipo == "invitado") "Invitado/Visitante" else "Usuario Registrado"
                    )
                    if (!det.residente.isNullOrBlank()) {
                        DetailRow(label = "Residente:", value = det.residente)
                    }
                    if (!det.direccion.isNullOrBlank()) {
                        DetailRow(label = "Vivienda:", value = det.direccion)
                    }
                    if (!det.colonia.isNullOrBlank()) {
                        DetailRow(label = "Colonia:", value = det.colonia)
                    }
                    if (det.usosRestantes != null) {
                        DetailRow(label = "Usos Restantes:", value = "${det.usosRestantes}")
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = SubtitleGrayColor,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ManualInputDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onValidar: (token: String) -> Unit
) {
    var tokenText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = StringsHelper.manualInput(lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Enter the QR access code:" else "Ingrese la clave del código QR de acceso:",
                    fontSize = 13.sp,
                    color = SubtitleGrayColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tokenText,
                    onValueChange = { tokenText = it },
                    label = { Text("Código QR (ej: QR_PEDRO_001)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tokenText.isNotBlank()) {
                        onValidar(tokenText.trim())
                    }
                },
                enabled = tokenText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (StringsHelper.isEnglish(lang)) "Validate Access" else "Validar Acceso", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (StringsHelper.isEnglish(lang)) "Cancel" else "Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun FullSettingsDialog(
    uiState: ScanUiState,
    onDismiss: () -> Unit,
    onSaveUrl: (String) -> Unit,
    onSelectTheme: (AppTheme) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleOfflineDemo: (Boolean) -> Unit,
    onCheckUpdate: () -> Unit,
    onClearUpdateMsg: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val lang = uiState.selectedLanguage
    val settingTabs = listOf(
        StringsHelper.tabServer(lang),
        StringsHelper.tabTheme(lang),
        StringsHelper.tabLanguage(lang)
    )
    var urlText by remember { mutableStateOf(uiState.serverUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = ConfirmButtonColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = StringsHelper.settingsTitle(lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = ConfirmButtonColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    settingTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            // API Server Settings Tab
                            Column {
                                Text(
                                    text = "URL Base del Servidor Node.js TecnoHand:",
                                    fontSize = 13.sp,
                                    color = SubtitleGrayColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = urlText,
                                    onValueChange = { urlText = it },
                                    label = { Text("URL Base de la API") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Modo Demo Offline",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Validar sin servidor con tokens de prueba",
                                            fontSize = 10.sp,
                                            color = SubtitleGrayColor
                                        )
                                    }
                                    Switch(
                                        checked = uiState.isOfflineDemoMode,
                                        onCheckedChange = { onToggleOfflineDemo(it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ConfirmButtonColor)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Presets Rápidos de Conexión:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { urlText = "http://10.0.2.2:3000/api/" },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Emulador (10.0.2.2)", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { urlText = "http://localhost:3000/api/" },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Localhost", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Theme Settings Tab (Claro, Oscuro, Por Defecto)
                            Column {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Select Application Theme:" else "Seleccione el Tema de la Aplicación:",
                                    fontSize = 13.sp,
                                    color = SubtitleGrayColor,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                ThemeOptionRow(
                                    title = if (StringsHelper.isEnglish(lang)) "☀️ Light Theme" else "☀️ Tema Claro (Light)",
                                    selected = uiState.selectedTheme == AppTheme.LIGHT,
                                    onClick = { onSelectTheme(AppTheme.LIGHT) }
                                )

                                ThemeOptionRow(
                                    title = if (StringsHelper.isEnglish(lang)) "🌙 Dark Theme" else "🌙 Tema Oscuro (Dark)",
                                    selected = uiState.selectedTheme == AppTheme.DARK,
                                    onClick = { onSelectTheme(AppTheme.DARK) }
                                )

                                ThemeOptionRow(
                                    title = if (StringsHelper.isEnglish(lang)) "📱 System Default" else "📱 Por Defecto del Sistema",
                                    selected = uiState.selectedTheme == AppTheme.SYSTEM,
                                    onClick = { onSelectTheme(AppTheme.SYSTEM) }
                                )
                            }
                        }

                        2 -> {
                            // Language Settings Tab (Español, English)
                            Column {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Select Preferred Language:" else "Seleccione el Idioma de Preferencia:",
                                    fontSize = 13.sp,
                                    color = SubtitleGrayColor,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                ThemeOptionRow(
                                    title = "🇲🇽 Español (América Latina)",
                                    selected = uiState.selectedLanguage == AppLanguage.SPANISH,
                                    onClick = { onSelectLanguage(AppLanguage.SPANISH) }
                                )

                                ThemeOptionRow(
                                    title = "🇺🇸 English (United States)",
                                    selected = uiState.selectedLanguage == AppLanguage.ENGLISH,
                                    onClick = { onSelectLanguage(AppLanguage.ENGLISH) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Clean Version Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TecnoHand ${uiState.versionInfo.versionName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SubtitleGrayColor
                    )

                    TextButton(onClick = onCheckUpdate) {
                        Text(
                            text = if (StringsHelper.isEnglish(lang)) "Check Version" else "Comprobar Versión",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConfirmButtonColor
                        )
                    }
                }

                if (uiState.updateCheckResult != null) {
                    Text(
                        text = uiState.updateCheckResult,
                        color = SuccessGreenColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveUrl(urlText)
                    onClearUpdateMsg()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(StringsHelper.saveApply(lang), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(StringsHelper.closeBtn(lang))
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun ThemeOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = ConfirmButtonColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HistoryDialog(
    logs: List<AccessLogItem>,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Historial de Accesos Registrados", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = ConfirmButtonColor)
                } else if (logs.isEmpty()) {
                    Text("No hay registros de acceso en la base de datos", color = SubtitleGrayColor, fontSize = 13.sp)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(logs) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (log.resultado == "permitido") SuccessGreenColor else ErrorRedColor,
                                        modifier = Modifier.size(10.dp)
                                    ) {}

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = log.personaNombre ?: "Acceso sin nombre",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${log.tipo.uppercase()} • ${log.viviendaDireccion ?: log.colonia}",
                                            fontSize = 11.sp,
                                            color = SubtitleGrayColor
                                        )
                                        Text(
                                            text = "Guardia: ${log.guardia} | ${log.fechaHora}",
                                            fontSize = 10.sp,
                                            color = SubtitleGrayColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
            ) {
                Text("Cerrar", color = Color.White)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun CameraPreviewView(
    modifier: Modifier = Modifier,
    onQrCodeDetected: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val mainExecutor = ContextCompat.getMainExecutor(ctx)

            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val barcodeScanner = BarcodeScanning.getClient()
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(mainExecutor) { imageProxy ->
                            @OptIn(ExperimentalGetImage::class)
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                barcodeScanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { qrValue ->
                                                onQrCodeDetected(qrValue)
                                            }
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            null
                        }

                        if (cameraSelector != null) {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, mainExecutor)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            previewView
        },
        modifier = modifier
    )
}

@Composable
private fun QrScannerOverlay(
    modifier: Modifier = Modifier,
    bracketColor: Color = Color(0xFF5A82ED),
    boxSizeDp: Dp = 230.dp,
    strokeWidthDp: Dp = 4.5.dp,
    bracketLengthDp: Dp = 42.dp,
    cornerRadiusDp: Dp = 16.dp
) {
    Canvas(modifier = modifier) {
        val boxSizePx = boxSizeDp.toPx()
        val strokeWidthPx = strokeWidthDp.toPx()
        val bracketLengthPx = bracketLengthDp.toPx()
        val cornerRadiusPx = cornerRadiusDp.toPx()

        val left = (size.width - boxSizePx) / 2
        val top = (size.height - boxSizePx) / 2
        val right = left + boxSizePx
        val bottom = top + boxSizePx
        val centerX = size.width / 2
        val centerY = size.height / 2

        val stroke = Stroke(
            width = strokeWidthPx,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Top-Left corner
        val pathTL = Path().apply {
            moveTo(left, top + bracketLengthPx)
            lineTo(left, top + cornerRadiusPx)
            quadraticTo(left, top, left + cornerRadiusPx, top)
            lineTo(left + bracketLengthPx, top)
        }
        drawPath(pathTL, color = bracketColor, style = stroke)

        // Top-Right corner
        val pathTR = Path().apply {
            moveTo(right, top + bracketLengthPx)
            lineTo(right, top + cornerRadiusPx)
            quadraticTo(right, top, right - cornerRadiusPx, top)
            lineTo(right - bracketLengthPx, top)
        }
        drawPath(pathTR, color = bracketColor, style = stroke)

        // Bottom-Left corner
        val pathBL = Path().apply {
            moveTo(left, bottom - bracketLengthPx)
            lineTo(left, bottom - cornerRadiusPx)
            quadraticTo(left, bottom, left + cornerRadiusPx, bottom)
            lineTo(left + bracketLengthPx, bottom)
        }
        drawPath(pathBL, color = bracketColor, style = stroke)

        // Bottom-Right corner
        val pathBR = Path().apply {
            moveTo(right, bottom - bracketLengthPx)
            lineTo(right, bottom - cornerRadiusPx)
            quadraticTo(right, bottom, right - cornerRadiusPx, bottom)
            lineTo(right - bracketLengthPx, bottom)
        }
        drawPath(pathBR, color = bracketColor, style = stroke)

        // Center Crosshair (+)
        val crosshairLengthPx = 36.dp.toPx()
        val crosshairStrokePx = 3.5.dp.toPx()

        // Horizontal line
        drawLine(
            color = bracketColor,
            start = Offset(centerX - crosshairLengthPx / 2, centerY),
            end = Offset(centerX + crosshairLengthPx / 2, centerY),
            strokeWidth = crosshairStrokePx,
            cap = StrokeCap.Round
        )

        // Vertical line
        drawLine(
            color = bracketColor,
            start = Offset(centerX, centerY - crosshairLengthPx / 2),
            end = Offset(centerX, centerY + crosshairLengthPx / 2),
            strokeWidth = crosshairStrokePx,
            cap = StrokeCap.Round
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun ScanQrScreenPreview() {
    ScanQrScreen()
}
