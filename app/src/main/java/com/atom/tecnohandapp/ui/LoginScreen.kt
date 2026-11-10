package com.atom.tecnohandapp.ui

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atom.tecnohandapp.R
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.UserProfile
import com.atom.tecnohandapp.data.models.UserRole

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)

enum class LoginScreenState {
    LANDING,
    LOGIN_FORM,
    REGISTER_FORM
}

@Composable
fun LoginScreen(
    lang: AppLanguage = AppLanguage.SPANISH,
    onLoginSuccess: (UserProfile) -> Unit = {}
) {
    var screenState by remember { mutableStateOf(LoginScreenState.LANDING) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Center Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ConfirmButtonColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tecnohand_logo),
                    contentDescription = "TecnoHand Logo",
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "TecnoHand",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (StringsHelper.isEnglish(lang)) "Residential Access & Safety Control" else "Control de Acceso y Seguridad Residencial",
                fontSize = 12.sp,
                color = SubtitleGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            when (screenState) {
                LoginScreenState.LANDING -> {
                    // Central 3 Buttons: Iniciar Sesion, Registrar Usuario, Salir
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { screenState = LoginScreenState.LOGIN_FORM },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                        ) {
                            Text(
                                text = if (StringsHelper.isEnglish(lang)) "Log In" else "Iniciar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = { screenState = LoginScreenState.REGISTER_FORM },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, ConfirmButtonColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ConfirmButtonColor)
                        ) {
                            Text(
                                text = if (StringsHelper.isEnglish(lang)) "Register User" else "Registrar Usuario",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ConfirmButtonColor
                            )
                        }

                        OutlinedButton(
                            onClick = { (context as? Activity)?.finish() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Text(
                                text = if (StringsHelper.isEnglish(lang)) "Exit App" else "Salir",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Role Login Buttons for Testing
                        Text(
                            text = if (StringsHelper.isEnglish(lang)) "Quick Login Presets:" else "Acceso Rápido por Rol:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SubtitleGrayColor
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    onLoginSuccess(
                                        UserProfile(
                                            id = "RES-2001",
                                            email = "residente@tecnohand.com",
                                            nombre = "Pedro Ramírez",
                                            rol = UserRole.RESIDENT,
                                            vivienda = "Calle Roble #101",
                                            colonia = "Colonia Los Pinos"
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                            ) {
                                Text("Residente 🏠", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onLoginSuccess(
                                        UserProfile(
                                            id = "GRD-9001",
                                            email = "guardia@tecnohand.com",
                                            nombre = "Oficial Gómez",
                                            rol = UserRole.GUARD,
                                            vivienda = "Caseta de Vigilancia",
                                            colonia = "Colonia Los Pinos"
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Guardia 🛡️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                LoginScreenState.LOGIN_FORM -> {
                    // Login Form View
                    var userInput by remember { mutableStateOf("") }
                    var passwordInput by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { screenState = LoginScreenState.LANDING }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Log In" else "Iniciar Sesión",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Email or ID TextBox
                            OutlinedTextField(
                                value = userInput,
                                onValueChange = { userInput = it },
                                label = { Text(if (StringsHelper.isEnglish(lang)) "Email or User ID" else "Correo o ID de Usuario") },
                                placeholder = { Text("ej. usuario@tecnohand.com o RES-1001") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ConfirmButtonColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Password TextBox with Eye Toggle Visibility Icon
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text(if (StringsHelper.isEnglish(lang)) "Password" else "Contraseña") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ConfirmButtonColor) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Mostrar contraseña",
                                            tint = SubtitleGrayColor
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    val isGuard = userInput.contains("guardia", ignoreCase = true) || userInput.contains("GRD", ignoreCase = true)
                                    val role = if (isGuard) UserRole.GUARD else UserRole.RESIDENT
                                    onLoginSuccess(
                                        UserProfile(
                                            id = if (userInput.isNotBlank()) userInput else "RES-1001",
                                            email = if (userInput.contains("@")) userInput else "$userInput@tecnohand.com",
                                            nombre = if (isGuard) "Oficial Caseta" else "Pedro Ramírez",
                                            rol = role
                                        )
                                    )
                                },
                                enabled = userInput.isNotBlank() && passwordInput.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                            ) {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Enter App" else "Ingresar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                LoginScreenState.REGISTER_FORM -> {
                    // Registration Form View
                    var nombreInput by remember { mutableStateOf("") }
                    var emailInput by remember { mutableStateOf("") }
                    var passwordInput by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }
                    var viviendaInput by remember { mutableStateOf("") }
                    var selectedRole by remember { mutableStateOf(UserRole.RESIDENT) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { screenState = LoginScreenState.LANDING }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "User Registration" else "Registro de Usuario",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Role Selection (Resident vs Guard)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRole == UserRole.RESIDENT) ConfirmButtonColor else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRole = UserRole.RESIDENT }
                                ) {
                                    Text(
                                        text = "Residente 🏠",
                                        color = if (selectedRole == UserRole.RESIDENT) Color.White else SubtitleGrayColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedRole == UserRole.GUARD) ConfirmButtonColor else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedRole = UserRole.GUARD }
                                ) {
                                    Text(
                                        text = "Guardia 🛡️",
                                        color = if (selectedRole == UserRole.GUARD) Color.White else SubtitleGrayColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = nombreInput,
                                onValueChange = { nombreInput = it },
                                label = { Text("Nombre Completo") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ConfirmButtonColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Correo Electrónico") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ConfirmButtonColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Contraseña") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ConfirmButtonColor) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Mostrar contraseña",
                                            tint = SubtitleGrayColor
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (selectedRole == UserRole.RESIDENT) {
                                OutlinedTextField(
                                    value = viviendaInput,
                                    onValueChange = { viviendaInput = it },
                                    label = { Text("Vivienda / Calle y Número") },
                                    placeholder = { Text("ej. Calle Roble #101") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ConfirmButtonColor) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    val newId = "RES-${(1000..9999).random()}"
                                    onLoginSuccess(
                                        UserProfile(
                                            id = newId,
                                            email = emailInput,
                                            nombre = nombreInput,
                                            rol = selectedRole,
                                            vivienda = if (viviendaInput.isNotBlank()) viviendaInput else "Calle Roble #101"
                                        )
                                    )
                                },
                                enabled = nombreInput.isNotBlank() && emailInput.isNotBlank() && passwordInput.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                            ) {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Complete Registration" else "Completar Registro",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
