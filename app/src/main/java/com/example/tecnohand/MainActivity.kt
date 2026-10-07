package com.example.tecnohand

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FormularioBreveScreen()
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

@Composable
fun FormularioBreveScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
    ) {
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

        // Contenido con scroll para que los botones siempre sean alcanzables
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
            CampoTexto(etiqueta = "Nombre(s)")

            Spacer(modifier = Modifier.height(16.dp))
            CampoTexto(etiqueta = "Apellidos")

            Spacer(modifier = Modifier.height(16.dp))
            CampoTexto(etiqueta = "Numero", teclado = KeyboardType.Phone)

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    CampoTexto(etiqueta = "Fecha")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    CampoTexto(etiqueta = "Hora")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CampoExpiracion()

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "El codigo dejara de funcionar al vencer este tiempo.",
                color = Gris,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón principal: solo visual, sin acción por ahora
            Button(
                onClick = { /* sin funcionalidad */ },
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
                    imageVector = Icons.Filled.List, // ícono básico; luego puedes cambiarlo por uno de QR
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

            // Botón cancelar: solo visual, sin acción por ahora
            OutlinedButton(
                onClick = { /* sin funcionalidad */ },
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

// Campo de solo lectura: se ve como un campo normal pero no permite escribir
@Composable
private fun CampoTexto(etiqueta: String, teclado: KeyboardType = KeyboardType.Text) {
    Column {
        Text(
            text = etiqueta,
            color = AzulOscuro,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = FondoCampo,
                focusedContainerColor = FondoCampo,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BordeCampo, RoundedCornerShape(8.dp))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoExpiracion() {
    var expandido by remember { mutableStateOf(false) }
    val opciones = listOf("1 hora", "6 horas", "12 horas", "24 horas")
    var seleccion by remember { mutableStateOf("6 horas") }

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
                            seleccion = opcion
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
        FormularioBreveScreen()
    }
}