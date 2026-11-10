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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun FamilyProfileScreen(
    userProfile: UserProfile,
    lang: AppLanguage = AppLanguage.SPANISH,
    onUpdateProfile: (UserProfile) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var viviendaText by remember { mutableStateOf(userProfile.vivienda) }
    var telefonoText by remember { mutableStateOf(userProfile.telefono) }
    var nuevoMiembroText by remember { mutableStateOf("") }
    val integrantesList = remember { mutableStateListOf(*userProfile.integrantesFamilia.toTypedArray()) }
    var saveSuccessMessage by remember { mutableStateOf(false) }

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
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ConfirmButtonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
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
                        text = "ID: ${userProfile.id} • ${userProfile.colonia}",
                        fontSize = 12.sp,
                        color = SubtitleGrayColor
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ConfirmButtonColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "ROL: ${userProfile.rol.name}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConfirmButtonColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
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
                    text = if (StringsHelper.isEnglish(lang)) "Housing & Contact Details" else "Datos de la Vivienda y Contacto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = viviendaText,
                    onValueChange = { viviendaText = it },
                    label = { Text(if (StringsHelper.isEnglish(lang)) "Residence Address" else "Dirección de la Vivienda") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ConfirmButtonColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = telefonoText,
                    onValueChange = { telefonoText = it },
                    label = { Text(if (StringsHelper.isEnglish(lang)) "Contact Phone" else "Teléfono de Contacto") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = ConfirmButtonColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
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
                    text = if (StringsHelper.isEnglish(lang)) "Family Members" else "Integrantes de la Familia",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Add Member Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nuevoMiembroText,
                        onValueChange = { nuevoMiembroText = it },
                        placeholder = { Text("ej. Ana Ramírez (Hija)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (nuevoMiembroText.isNotBlank()) {
                                integrantesList.add(nuevoMiembroText.trim())
                                nuevoMiembroText = ""
                            }
                        },
                        enabled = nuevoMiembroText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                integrantesList.forEachIndexed { index, miembro ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ConfirmButtonColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = miembro,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = { integrantesList.removeAt(index) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        if (saveSuccessMessage) {
            Text(
                text = "✓ ¡Datos de vivienda y familia actualizados con éxito!",
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
                        vivienda = viviendaText,
                        telefono = telefonoText,
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
