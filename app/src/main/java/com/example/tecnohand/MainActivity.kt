package com.example.tecnohand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Si es null se muestra el formulario; si tiene texto, se muestra el QR
                var contenidoQr by rememberSaveable { mutableStateOf<String?>(null) }

                val contenido = contenidoQr
                if (contenido == null) {
                    FormularioBreveScreen(
                        onGenerar = { datos -> contenidoQr = construirContenidoQr(datos) },
                        onCancelar = { finish() }
                    )
                } else {
                    BackHandler { contenidoQr = null }
                    QrScreen(
                        contenido = contenido,
                        onVolver = { contenidoQr = null }
                    )
                }
            }
        }
    }
}

// ---------- Paleta de colores TecnoHand ----------
private val AzulMuyOscuro = Color(0xFF071332)
private val AzulOscuro    = Color(0xFF172A62)
private val AzulClaro     = Color(0xFF6587ED)
private val Gris          = Color(0xFF697381)
private val Blanco        = Color(0xFFFFFFFF)
private val FondoCampo    = Color(0xFFEFF2FC)
private val BordeCampo    = Color(0xFFDCE2F5)
private val BordeCancelar = Color(0xFFE1E4EA)
private val Rojo          = Color(0xFFD32F2F)

// ================= PANTALLA 1: FORMULARIO =================
@Composable
fun FormularioBreveScreen(
    onGenerar: (DatosInvitacion) -> Unit,
    onCancelar: () -> Unit
) {
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var numero by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }
    var hora by rememberSaveable { mutableStateOf("") }
    var expiracion by rememberSaveable { mutableStateOf("6 horas") }
    var intentoEnvio by rememberSaveable { mutableStateOf(false) }

    val formularioValido = nombres.isNotBlank() &&
            apellidos.isNotBlank() &&
            numero.isNotBlank() &&
            fecha.isNotBlank() &&
            hora.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
    ) {
        Encabezado()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Formulario breve",
                color = AzulMuyOscuro,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Completa los datos para generar el codigo de acceso.",
                color = Gris,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            CampoTexto(
                etiqueta = "Nombre(s)",
                valor = nombres,
                onValorCambia = { nombres = it },
                mostrarError = intentoEnvio && nombres.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))
            CampoTexto(
                etiqueta = "Apellidos",
                valor = apellidos,
                onValorCambia = { apellidos = it },
                mostrarError = intentoEnvio && apellidos.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))
            CampoTexto(
                etiqueta = "Numero",
                valor = numero,
                onValorCambia = { numero = it },
                teclado = KeyboardType.Phone,
                mostrarError = intentoEnvio && numero.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    CampoTexto(
                        etiqueta = "Fecha",
                        valor = fecha,
                        onValorCambia = { fecha = it },
                        mostrarError = intentoEnvio && fecha.isBlank()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    CampoTexto(
                        etiqueta = "Hora",
                        valor = hora,
                        onValorCambia = { hora = it },
                        mostrarError = intentoEnvio && hora.isBlank()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CampoExpiracion(
                seleccion = expiracion,
                onSeleccion = { expiracion = it }
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "El codigo dejara de funcionar al vencer este tiempo.",
                color = Gris,
                fontSize = 12.sp
            )

            if (intentoEnvio && !formularioValido) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Completa todos los campos para generar el codigo.",
                    color = Rojo,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    intentoEnvio = true
                    if (formularioValido) {
                        onGenerar(
                            DatosInvitacion(
                                nombres = nombres.trim(),
                                apellidos = apellidos.trim(),
                                numero = numero.trim(),
                                fecha = fecha.trim(),
                                hora = hora.trim(),
                                horasExpiracion = expiracion.substringBefore(" ").toInt()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulOscuro,
                    contentColor = Blanco
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = null,
                    tint = Blanco
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generar codigo QR",
                    color = Blanco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCancelar,
                border = BorderStroke(1.dp, BordeCancelar),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "Cancelar", color = AzulOscuro)
            }
        }
    }
}

// ================= PANTALLA 2: QR GENERADO =================
@Composable
fun QrScreen(contenido: String, onVolver: () -> Unit) {
    val bitmap = remember(contenido) { generarQr(contenido) }

    // Datos para mostrar debajo del QR
    val json = remember(contenido) { JSONObject(contenido) }
    val nombreCompleto = "${json.getString("nombres")} ${json.getString("apellidos")}"
    val vence = remember(contenido) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(json.getLong("venceEn")))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
    ) {
        Encabezado()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Codigo de acceso",
                color = AzulMuyOscuro,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Muestra este codigo al guardia para ingresar.",
                color = Gris,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Fondo blanco con borde: ayuda a que la camara lo lea bien
            Box(
                modifier = Modifier
                    .background(Blanco, RoundedCornerShape(12.dp))
                    .border(1.dp, BordeCampo, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Codigo QR de acceso",
                    modifier = Modifier.size(260.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = nombreCompleto,
                color = AzulMuyOscuro,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Valido hasta: $vence",
                color = Gris,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = onVolver,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulOscuro,
                    contentColor = Blanco
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Volver al formulario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun Encabezado() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo TecnoHand",
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "TecnoHand",
            color = AzulMuyOscuro,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Divider(color = BordeCampo, thickness = 1.dp)
}

@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValorCambia: (String) -> Unit,
    teclado: KeyboardType = KeyboardType.Text,
    mostrarError: Boolean = false
) {
    Column {
        Text(
            text = etiqueta,
            color = AzulOscuro,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value = valor,
            onValueChange = onValorCambia,
            singleLine = true,
            isError = mostrarError,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = FondoCampo,
                focusedContainerColor = FondoCampo,
                errorContainerColor = FondoCampo,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (mostrarError) Rojo else BordeCampo,
                    RoundedCornerShape(8.dp)
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoExpiracion(
    seleccion: String,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val opciones = listOf("1 hora", "6 horas", "12 horas", "24 horas")

    Column {
        Text(
            text = "Expiracion",
            color = AzulOscuro,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = it }
        ) {
            TextField(
                value = seleccion,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = FondoCampo,
                    focusedContainerColor = FondoCampo,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .border(1.dp, BordeCampo, RoundedCornerShape(8.dp))
            )
            ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccion(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioBreveScreenPreview() {
    MaterialTheme {
        FormularioBreveScreen(onGenerar = {}, onCancelar = {})
    }
}