package com.example.tecnohand

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun QrCodeScreen(
    addressText: String = "[Cerrada, número]",
    qrContent: String = "https://colonia.app/join/12345",
    onShareWhatsAppClick: () -> Unit = {},
    onCopyLinkClick: () -> Unit = {}
)
{
    val headerDarkBlue = Color(0xFF0D1B3E)
    val headerSubtitleBlue = Color(0xFF4A72B2)
    val qrBorderBlue = Color(0xFF6B8AFD)
    val cardBorderGray = Color(0xFFCBD5E1)
    val labelGray = Color(0xFF64748B)
    val buttonBlue = Color(0xFF5E81F4)

    val qrBitmap = remember(qrContent) {
        generateQrBitmap(qrContent, 512, 512)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerDarkBlue)
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mi código QR",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Escanéalo para conectar",
                fontSize = 14.sp,
                color = headerSubtitleBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Válido por tiempo limitado",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(
                        width = 2.dp,
                        color = qrBorderBlue,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(24.dp))
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            )
            {
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Código QR",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: CircularProgressIndicator(color = buttonBlue)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = cardBorderGray,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            )
            {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Dirección",
                        fontSize = 12.sp,
                        color = labelGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = addressText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Escanea o comparte este código para unirte",
                fontSize = 13.sp,
                color = labelGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onShareWhatsAppClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            )
            {
                Text(
                    text = "Compartir por WhatsApp",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onCopyLinkClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(cardBorderGray)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White
                )
            )
            {
                Text(
                    text = "Copiar enlace",
                    color = labelGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


private fun generateQrBitmap(content: String, width: Int, height: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewQrCodeScreen() {
    QrCodeScreen()
}