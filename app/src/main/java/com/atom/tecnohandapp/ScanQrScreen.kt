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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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

@Composable
fun ScanQrScreen(
    onConfirmScan: () -> Unit = {},
    onManualInput: () -> Unit = {},
    onQrCodeDetected: (String) -> Unit = {}
) {
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
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tecnohand_logo),
                contentDescription = "Logo TecnoHand",
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Escanear QR de Acceso",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- White Body Card Container ---
        Surface(
            modifier = Modifier
                .fillMaxSize(),
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
                                onQrCodeDetected = onQrCodeDetected
                            )
                        }
                        // Overlaid QR Framing Reticle
                        QrScannerOverlay(
                            modifier = Modifier.fillMaxSize(),
                            bracketColor = ReticleBlueColor
                        )
                        // Text at the bottom inside camera preview
                        Text(
                            text = "Apunte al código QR...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp)
                        )
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

                Spacer(modifier = Modifier.height(20.dp))

                // --- Confirm Button ---
                Button(
                    onClick = onConfirmScan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ConfirmButtonColor
                    )
                ) {
                    Text(
                        text = "Confirmar Escaneo",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- Manual Input Button ---
                OutlinedButton(
                    onClick = onManualInput,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
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
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- Footer Hint Text ---
                Text(
                    text = "Use ingreso manual si el QR no es legible.",
                    color = SubtitleGrayColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
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
