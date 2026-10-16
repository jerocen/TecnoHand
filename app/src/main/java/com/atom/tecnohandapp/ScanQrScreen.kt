package com.atom.tecnohandapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
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
import com.atom.tecnohandapp.data.models.AccessLogItem
import com.atom.tecnohandapp.data.models.QrValidationResponse
import com.atom.tecnohandapp.ui.ScanQrViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// Exact colors matched from the target design
private val NavyHeaderColor = Color(0xFF08142C)
private val ContainerBgColor = Color(0xFFFFFFFF)
private val CameraBorderColor = Color(0xFF7A9BFD)
private val ReticleBlueColor = Color(0xFF5A82ED)
private val ConfirmButtonColor = Color(0xFF5A82ED)
private val DarkTextColor = Color(0xFF0A1838)
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

    LaunchedEffect(Unit) {
        if (!isPreview && !hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyHeaderColor)
            .statusBarsPadding()
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tecnohand_logo),
                contentDescription = "Logo TecnoHand",
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Escanear QR de Acceso",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // History Button
            IconButton(
                onClick = { viewModel.loadAccessHistory() },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Historial",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Server Config Button
            IconButton(
                onClick = { viewModel.setShowServerConfigDialog(true) },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurar Servidor API",
                    tint = Color.White
                )
            }
        }

        // --- White Body Card Container ---
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = ContainerBgColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
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
                            color = CameraBorderColor,
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
                            bracketColor = ReticleBlueColor
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
                                        text = "Verificando QR con el servidor...",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            // Text at the bottom inside camera preview
                            val statusText = if (!uiState.lastDetectedToken.isNullOrBlank()) {
                                "Detectado: ${uiState.lastDetectedToken}"
                            } else {
                                "Apunte al código QR..."
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

                Spacer(modifier = Modifier.height(18.dp))

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
                        text = "Confirmar Escaneo",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- Manual Input Button ---
                OutlinedButton(
                    onClick = {
                        viewModel.setShowManualDialog(true)
                        onManualInput()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = DarkTextColor
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = DarkTextColor
                    )
                ) {
                    Text(
                        text = "Ingreso Manual",
                        color = DarkTextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- Footer Hint Text ---
                Text(
                    text = "Use ingreso manual si el QR no es legible.",
                    color = SubtitleGrayColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }

    // --- Dialogs & Result Sheets ---
    if (uiState.showResultSheet && uiState.validationResponse != null) {
        ScanResultDialog(
            response = uiState.validationResponse!!,
            onDismiss = { viewModel.dismissResultSheet() }
        )
    }

    if (uiState.showManualDialog) {
        ManualInputDialog(
            onDismiss = { viewModel.setShowManualDialog(false) },
            onValidar = { token ->
                viewModel.validarAcceso(token)
            }
        )
    }

    if (uiState.showServerConfigDialog) {
        ServerConfigDialog(
            currentUrl = uiState.serverUrl,
            currentGuardJwt = uiState.guardJwt,
            onDismiss = { viewModel.setShowServerConfigDialog(false) },
            onSave = { newUrl, jwt ->
                viewModel.updateServerConfig(newUrl, jwt)
            }
        )
    }

    if (uiState.showHistoryDialog) {
        HistoryDialog(
            logs = uiState.historyLogs,
            isLoading = uiState.isHistoryLoading,
            onDismiss = { viewModel.setShowHistoryDialog(false) }
        )
    }
}

@Composable
private fun ScanResultDialog(
    response: QrValidationResponse,
    onDismiss: () -> Unit
) {
    val isPermitido = response.valido || response.resultado == "permitido"
    val headerBgColor = if (isPermitido) SuccessGreenColor else ErrorRedColor
    val headerTitle = if (isPermitido) "¡Acceso PERMITIDO!" else "Acceso RECHAZADO"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Entendido / Escanear Otro", color = Color.White)
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
                    color = DarkTextColor
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

                if (!response.visitante.isNullOrBlank()) {
                    DetailRow(label = "Visitante:", value = response.visitante)
                }
                if (response.usosRestantes != null) {
                    DetailRow(label = "Usos Restantes:", value = "${response.usosRestantes}")
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
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
            color = DarkTextColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ManualInputDialog(
    onDismiss: () -> Unit,
    onValidar: (token: String) -> Unit
) {
    var tokenText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ingreso Manual de QR",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkTextColor
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ingrese o seleccione la clave del código QR:",
                    fontSize = 13.sp,
                    color = SubtitleGrayColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tokenText,
                    onValueChange = { tokenText = it },
                    label = { Text("Código QR (THQR1....)") },
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
                Text("Validar Acceso", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
private fun ServerConfigDialog(
    currentUrl: String,
    currentGuardJwt: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var urlText by remember { mutableStateOf(currentUrl) }
    var jwtText by remember { mutableStateOf(currentGuardJwt) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Configuración de Servidor API", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column {
                Text(
                    "Especifique la URL base del backend Node.js TecnoHand:",
                    fontSize = 13.sp,
                    color = SubtitleGrayColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("URL Base de la API") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = jwtText,
                    onValueChange = { jwtText = it },
                    label = { Text("JWT del guardia") },
                    supportingText = { Text("Obténlo con /api/auth/login en Postman") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Presets Rápidos:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        },
        confirmButton = {
            Button(
                onClick = { onSave(urlText, jwtText) },
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun HistoryDialog(
    logs: List<AccessLogItem>,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Historial de Accesos Registrados", fontWeight = FontWeight.Bold, fontSize = 17.sp)
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
                                    containerColor = Color(0xFFF8FAFC)
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
                                            color = DarkTextColor
                                        )
                                        Text(
                                            text = "${log.tipo.uppercase()} • ${log.viviendaDireccion ?: log.colonia}",
                                            fontSize = 11.sp,
                                            color = SubtitleGrayColor
                                        )
                                        Text(
                                            text = "Guardia: ${log.guardia} | ${log.fechaHora}",
                                            fontSize = 10.sp,
                                            color = Color.Gray
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
        shape = RoundedCornerShape(20.dp)
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
