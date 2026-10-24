package com.atom.tecnohandapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val DarkTextColor = Color(0xFF0A1838)
private val SubtitleGrayColor = Color(0xFF64748B)
private val ContainerBgColor = Color(0xFFF8FAFC)
private val BorderGrayColor = Color(0xFFCBD5E1)

@Composable
fun ReportsFormScreen(
    viewModel: FormReportsViewModel = viewModel()
)
{
    var categoriaSeleccionada by remember { mutableStateOf("Falla en alumbrado público") }
    var descripcion by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxWidth())
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        )
        {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp))
            {
                Text(
                    text = "GENERAR REPORTE:",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextColor
                )
                Text(
                    text = "La información enviada por este medio deberá estar relacionada con un incidente o problema serio.",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtitleGrayColor
                )
            }

            //Categoria del reporte
            Column(verticalArrangement = Arrangement.spacedBy(6.dp))
            {
                Text(
                    text = "Categoría del incidente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextColor
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ContainerBgColor)
                        .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp))
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = categoriaSeleccionada,
                            fontSize = 14.sp,
                            color = DarkTextColor
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Desplegar categorías",
                            tint = SubtitleGrayColor
                        )
                    }
                }
            }

            //Descripción del reporte
            Column(verticalArrangement = Arrangement.spacedBy(6.dp))
            {
                Text(
                    text = "Descripción detallada",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextColor
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = {
                        Text(
                            text = "Explica qué sucedió, puntos de referencia o detalles relevantes...",
                            fontSize = 14.sp,
                            color = SubtitleGrayColor
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ConfirmButtonColor,
                        unfocusedBorderColor = BorderGrayColor,
                        focusedContainerColor = ContainerBgColor,
                        unfocusedContainerColor = ContainerBgColor
                    )
                )
            }

            //Fotos
            Column(verticalArrangement = Arrangement.spacedBy(6.dp))
            {
                Text(
                    text = "Evidencia fotográfica",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextColor
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ContainerBgColor)
                        .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Adjuntar fotos",
                            tint = ConfirmButtonColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Toca para adjuntar fotos o capturas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtitleGrayColor
                        )
                    }
                }
            }

            //Mapa
            Column(verticalArrangement = Arrangement.spacedBy(6.dp))
            {
                Text(
                    text = "Ubicación del incidente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextColor
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    placeholder = {
                        Text(
                            text = "Calle, número exterior o referencia de la zona",
                            fontSize = 14.sp,
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
                        focusedContainerColor = ContainerBgColor,
                        unfocusedContainerColor = ContainerBgColor
                    )
                )

                // Placeholder visual del mapa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE2E8F0))
                        .border(1.dp, BorderGrayColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Mapa referencial",
                            tint = SubtitleGrayColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Vista previa del mapa de la colonia",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtitleGrayColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ConfirmButtonColor
                )
            )
            {
                Text(
                    text = "Enviar Reporte",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}