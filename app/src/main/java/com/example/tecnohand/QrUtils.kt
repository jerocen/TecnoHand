package com.example.tecnohand

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject
import java.util.UUID

/** Datos que captura el formulario. */
data class DatosInvitacion(
    val nombres: String,
    val apellidos: String,
    val numero: String,
    val fecha: String,
    val hora: String,
    val minutosExpiracion: Int
)

/**
 * Funcion 1: arma el texto que ira dentro del QR.
 */
fun construirContenidoQr(datos: DatosInvitacion): String {
    val ahora = System.currentTimeMillis()
    val vence = ahora + datos.minutosExpiracion * 60L * 1000L

    return JSONObject().apply {
        put("id", UUID.randomUUID().toString())
        put("nombres", datos.nombres)
        put("apellidos", datos.apellidos)
        put("numero", datos.numero)
        put("fecha", datos.fecha)
        put("hora", datos.hora)
        put("creadoEn", ahora)
        put("venceEn", vence)
    }.toString()
}

/**
 * Funcion 2: convierte un texto en un Bitmap con el codigo QR
 * usando zxing-android-embedded.
 */
fun generarQr(contenido: String, tamano: Int = 800): Bitmap {
    return BarcodeEncoder().encodeBitmap(
        contenido,
        BarcodeFormat.QR_CODE,
        tamano,
        tamano
    )
}