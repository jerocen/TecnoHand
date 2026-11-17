package com.atom.tecnohandapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.UserProfile

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyProfileScreen(
    userProfile: UserProfile,
    lang: AppLanguage = AppLanguage.SPANISH,
    onUpdateProfile: (UserProfile) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var viviendaText by remember { mutableStateOf(userProfile.vivienda.ifBlank { userProfile.ubicacionDireccion }) }
    var numeroViviendaText by remember { mutableStateOf(userProfile.numeroVivienda) }
    var telefonoText by remember { mutableStateOf(userProfile.telefono) }
    var cantidadResidentes by remember { mutableIntStateOf(userProfile.cantidadResidentes) }

    var nuevoNombreFamiliar by remember { mutableStateOf("") }
    var nuevoParentesco by remember { mutableStateOf("Hijo/a") }
    var parentescoDropdownExpanded by remember { mutableStateOf(false) }

    val integrantesList = remember { mutableStateListOf(*userProfile.integrantesFamilia.toTypedArray()) }
    var saveSuccessMessage by remember { mutableStateOf(false) }

    val parentescoOptions = listOf(
        "Esposo/a", "Hijo/a", "Padre/Madre", "Hermano/a", "Abuelo/a", "Tío/a", "Otro"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(ConfirmButtonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Email: ${userProfile.email}",
                        fontSize = 12.sp,
                        color = SubtitleGrayColor
                    )
                    Text(
                        text = "Nacimiento: ${userProfile.fechaNacimiento}",
                        fontSize = 11.sp,
                        color = SubtitleGrayColor
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ConfirmButtonColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ROL: ${userProfile.rol.name}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ConfirmButtonColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ID VIVIENDA: ${userProfile.numeroVivienda}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Editable Residence Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Housing & Location Details" else "Datos de la Vivienda y Ubicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = numeroViviendaText,
                    onValueChange = { numeroViviendaText = it },
                    label = { Text("Número o ID de Vivienda (DB)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ConfirmButtonColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = viviendaText,
                    onValueChange = { viviendaText = it },
                    label = { Text(if (StringsHelper.isEnglish(lang)) "Residence Address" else "Dirección / Referencia Vivienda") },
                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ConfirmButtonColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (userProfile.ubicacionDireccion.isNotBlank()) {
                    Text(
                        text = "📍 Ubicación registrada en mapa: ${userProfile.ubicacionDireccion}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ConfirmButtonColor
                    )
                }

                OutlinedTextField(
                    value = telefonoText,
                    onValueChange = { telefonoText = it },
                    label = { Text(if (StringsHelper.isEnglish(lang)) "Contact Phone" else "Teléfono de Contacto") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = ConfirmButtonColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Resident count controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.People, contentDescription = null, tint = ConfirmButtonColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Residentes:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { if (cantidadResidentes > 1) cantidadResidentes-- },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("-", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "$cantidadResidentes",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Button(
                            onClick = { cantidadResidentes++ },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Family Members List Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Family Members" else "Integrantes de la Familia y Parentesco",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Add Member Inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nuevoNombreFamiliar,
                        onValueChange = { nuevoNombreFamiliar = it },
                        placeholder = { Text("Nombre del familiar") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = parentescoDropdownExpanded,
                        onExpandedChange = { parentescoDropdownExpanded = !parentescoDropdownExpanded },
                        modifier = Modifier.weight(0.9f)
                    ) {
                        OutlinedTextField(
                            value = nuevoParentesco,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentescoDropdownExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = parentescoDropdownExpanded,
                            onDismissRequest = { parentescoDropdownExpanded = false }
                        ) {
                            parentescoOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        nuevoParentesco = option
                                        parentescoDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (nuevoNombreFamiliar.isNotBlank()) {
                                integrantesList.add("${nuevoNombreFamiliar.trim()} ($nuevoParentesco)")
                                nuevoNombreFamiliar = ""
                            }
                        },
                        enabled = nuevoNombreFamiliar.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                if (integrantesList.isEmpty()) {
                    Text(
                        text = "Sin integrantes familiares registrados aún.",
                        fontSize = 12.sp,
                        color = SubtitleGrayColor
                    )
                } else {
                    integrantesList.forEachIndexed { index, miembro ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ConfirmButtonColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = miembro,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(onClick = { integrantesList.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (saveSuccessMessage) {
            Text(
                text = "✓ ¡Datos de vivienda y familia guardados y sincronizados en DataStore!",
                color = Color(0xFF10B981),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Save Button
        Button(
            onClick = {
                onUpdateProfile(
                    userProfile.copy(
                        numeroVivienda = numeroViviendaText,
                        vivienda = viviendaText,
                        ubicacionDireccion = viviendaText,
                        telefono = telefonoText,
                        cantidadResidentes = cantidadResidentes,
                        integrantesFamilia = integrantesList.toList()
                    )
                )
                saveSuccessMessage = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
        ) {
            Text(
                text = if (StringsHelper.isEnglish(lang)) "Save Profile Changes" else "Guardar Cambios de Perfil",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
            Text(
                text = if (StringsHelper.isEnglish(lang)) "Log Out" else "Cerrar Sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
