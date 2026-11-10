package com.atom.tecnohandapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.UserProfile
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)
private val WhatsAppGreen = Color(0xFF25D366)

@Composable
fun GuestQrGeneratorScreen(
    userProfile: UserProfile,
    lang: AppLanguage = AppLanguage.SPANISH
) {
    val context = LocalContext.current
    var nombreInvitado by remember { mutableStateOf("") }
    var limiteUsos by remember { mutableIntStateOf(1) }
    var generatedQrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var generatedTokenCode by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (StringsHelper.isEnglish(lang)) "Generate Guest QR Access" else "Crear Código QR para Invitados",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (StringsHelper.isEnglish(lang)) "Generate a limited-use QR code and share via WhatsApp" else "Genera un acceso QR con usos limitados y compártelo por WhatsApp",
            fontSize = 12.sp,
            color = SubtitleGrayColor,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Guest Information" else "Datos del Invitado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = nombreInvitado,
                    onValueChange = { nombreInvitado = it },
                    label = { Text(if (StringsHelper.isEnglish(lang)) "Guest Full Name" else "Nombre del Invitado") },
                    placeholder = { Text("ej. Juan Pérez") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Allowed Access Limit:" else "Límite de Usos Permitidos:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(1, 3, 5).forEach { uses ->
                        val isSelected = limiteUsos == uses
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ConfirmButtonColor else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, ConfirmButtonColor),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { limiteUsos = uses }
                        ) {
                            Text(
                                text = if (uses == 1) "1 Uso" else "$uses Usos",
                                color = if (isSelected) Color.White else ConfirmButtonColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val token = "INV-${(1000..9999).random()}-${nombreInvitado.take(4).uppercase()}"
                        val payload = "TECNOHAND_QR|$token|${userProfile.vivienda}|$limiteUsos"
                        generatedTokenCode = token
                        generatedQrBitmap = generateQrBitmap(payload, 500)
                    },
                    enabled = nombreInvitado.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                ) {
                    Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (StringsHelper.isEnglish(lang)) "Generate QR Code" else "Generar Código QR",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Generated QR Display Card & Share Button
        if (generatedQrBitmap != null && generatedTokenCode != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, ConfirmButtonColor)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PASE DE ACCESO TECNOHAND",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ConfirmButtonColor
                    )
                    Text(
                        text = "Invitado: $nombreInvitado ($limiteUsos uso/s)",
                        fontSize = 12.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Image(
                        bitmap = generatedQrBitmap!!.asImageBitmap(),
                        contentDescription = "Código QR Invitado",
                        modifier = Modifier
                            .size(200.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Código: $generatedTokenCode",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // WhatsApp Share Button
                    Button(
                        onClick = {
                            shareQrViaWhatsApp(
                                context = context,
                                bitmap = generatedQrBitmap!!,
                                invitado = nombreInvitado,
                                token = generatedTokenCode!!,
                                vivienda = userProfile.vivienda
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (StringsHelper.isEnglish(lang)) "Share via WhatsApp 💬" else "Compartir por WhatsApp 💬",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun generateQrBitmap(content: String, size: Int): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}

private fun shareQrViaWhatsApp(
    context: Context,
    bitmap: Bitmap,
    invitado: String,
    token: String,
    vivienda: String
) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/guest_qr.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val imagePath = File(context.cacheDir, "images")
        val newFile = File(imagePath, "guest_qr.png")
        val contentUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", newFile)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(
                Intent.EXTRA_TEXT,
                "Hola $invitado, este es tu pase de acceso QR para ingresar a $vivienda (Clave: $token)."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            // WhatsApp chooser fallback
            val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "Pase QR TecnoHand para $invitado ($vivienda)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Compartir Pase QR")
            context.startActivity(chooser)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
