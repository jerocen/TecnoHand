package com.atom.tecnohandapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import android.content.pm.PackageManager
import androidx.compose.material3.CircularProgressIndicator
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import com.atom.tecnohandapp.data.models.ResetPasswordRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.atom.tecnohandapp.R
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.local.UserDataStore
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.FamiliaMember
import com.atom.tecnohandapp.data.models.UserProfile
import com.atom.tecnohandapp.data.models.UserRole
import com.atom.tecnohandapp.data.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.atom.tecnohandapp.data.models.LoginRequest
import com.atom.tecnohandapp.data.models.RegisterRequest
import com.google.android.gms.location.LocationServices

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)

enum class LoginScreenState
{
    LANDING,
    LOGIN_FORM,
    REGISTER_FORM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    lang: AppLanguage = AppLanguage.SPANISH,
    onLoginSuccess: (UserProfile) -> Unit = {}
)
{
    var screenState by remember { mutableStateOf(LoginScreenState.LANDING) }
    val context = LocalContext.current
    val userDataStore = remember { UserDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    // --- State for 3-Step Onboarding Registration Flow ---
    var onboardingStep by remember { mutableIntStateOf(1) }

    // Step 1: Basic Info
    var nombresInput by remember { mutableStateOf("") }
    var apellidosInput by remember { mutableStateOf("") }
    var telefonoInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var fechaNacimientoInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.RESIDENT) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Step 2: Housing Info & OSMDroid Map
    var numeroViviendaInput by remember { mutableStateOf("") }
    var latitudInput by remember { mutableDoubleStateOf(19.432608) }
    var longitudInput by remember { mutableDoubleStateOf(-99.133209) }
    var ubicacionDireccionInput by remember { mutableStateOf("Caseta Principal, Av. Los Pinos 100") }
    var cantidadResidentesCount by remember { mutableIntStateOf(3) }
    var isDetectingGps by remember { mutableStateOf(false) }

    // Step 3: Family Members
    val integrantesFamiliaList = remember { mutableStateListOf<FamiliaMember>() }
    var nuevoFamiliarNombre by remember { mutableStateOf("") }
    var nuevoFamiliarParentesco by remember { mutableStateOf("Esposo/a") }
    var parentescoExpanded by remember { mutableStateOf(false) }

    // Login Form State
    var loginUserInput by remember { mutableStateOf("") }
    var loginPasswordInput by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmittingAuth by remember { mutableStateOf(false) }

    // Estados para recuperar/cambiar contraseña
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var resetNewPassInput by remember { mutableStateOf("") }
    var resetConfirmPassInput by remember { mutableStateOf("") }
    var resetMessage by remember { mutableStateOf<String?>(null) }
    var isResetError by remember { mutableStateOf(false) }
    var isResettingPass by remember { mutableStateOf(false) }

    // Función centralizada para registrar (sirve para Guardia en Paso 1 y Residente en Paso 3)
    val submitRegistration: () -> Unit = {
        val isGuard = selectedRole == UserRole.GUARD
        val cleanNombres = nombresInput.trim()
        val cleanApellidos = apellidosInput.trim()
        val fullNombre = "$cleanNombres $cleanApellidos".trim()
        val cleanEmail = emailInput.replace(" ", "")
        val cleanPassword = passwordInput.replace(" ", "")
        val houseId = if (isGuard) "" else numeroViviendaInput.trim().ifBlank { "101" }
        val addressFormatted = if (isGuard) {
            ""
        }
        else if(ubicacionDireccionInput.isNotBlank() && !ubicacionDireccionInput.startsWith("Lat:"))
        {
            "$ubicacionDireccionInput (#$houseId)"
        }
        else
        {
            "Calle Roble #$houseId"
        }

        val createdProfile = UserProfile(
            id = if (isGuard) "GRD-001" else "VIV-$houseId",
            email = cleanEmail,
            password = cleanPassword,
            nombres = cleanNombres,
            apellidos = cleanApellidos,
            nombre = fullNombre.ifBlank { cleanEmail },
            telefono = telefonoInput.trim(),
            fechaNacimiento = fechaNacimientoInput.ifBlank { "01-01-1990" },
            rol = selectedRole,
            numeroVivienda = houseId,
            vivienda = addressFormatted,
            latitud = if (isGuard) 0.0 else latitudInput,
            longitud = if (isGuard) 0.0 else longitudInput,
            ubicacionDireccion = if (isGuard) "" else ubicacionDireccionInput,
            cantidadResidentes = if (isGuard) 0 else cantidadResidentesCount,
            colonia = "Colonia Los Pinos",
            integrantesFamilia = if (isGuard) emptyList() else integrantesFamiliaList.map { it.toFormattedString() }
        )

        coroutineScope.launch {
            isSubmittingAuth = true
            loginErrorMessage = null
            try
            {
                val response = RetrofitClient.getService().register(
                    RegisterRequest(
                        nombre = fullNombre.ifBlank { cleanEmail },
                        telefono = telefonoInput.trim(),
                        email = cleanEmail,
                        password = cleanPassword,
                        rol = selectedRole.name,
                        direccion = addressFormatted,
                        referencia = houseId,
                        colonia = "Colonia Los Pinos"
                    )
                )
                if(response.isSuccessful && response.body() != null)
                {
                    val u = response.body()!!
                    val syncedProfile = createdProfile.copy(id = u.id.toString())
                    userDataStore.registerUser(syncedProfile)
                    onLoginSuccess(syncedProfile)
                }
                else
                {
                    loginErrorMessage = "No se pudo registrar en la BD (${response.code()})"
                }
            }
            catch(e: Exception)
            {
                loginErrorMessage = "Error de conexión: ${e.localizedMessage}"
            }
            finally
            {
                isSubmittingAuth = false
            }
        }
    }

    // Secret Developer / Admin Panel State (15-second long press + PIN 3141)
    var showAdminPinDialog by remember { mutableStateOf(false) }
    var showAdminDeveloperPanel by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }
    var adminPinError by remember { mutableStateOf(false) }

    var isHoldingLoginButton by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }

    val parentescoOptions = listOf(
        "Esposo/a", "Hijo/a", "Padre/Madre", "Hermano/a", "Abuelo/a", "Tío/a", "Otro"
    )

    // Location Permission Launcher for Automatic GPS Detection in Step 2
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    )
    { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if(granted)
        {
            isDetectingGps = true
            fetchGpsDeviceLocation(context) { lat, lng, addr ->
                latitudInput = lat
                longitudInput = lng
                ubicacionDireccionInput = addr
                isDetectingGps = false
            }
        }
    }

    // Auto-detect device location upon entering Step 2
    LaunchedEffect(onboardingStep)
    {
        if(onboardingStep == 2)
        {
            val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if(fineGranted || coarseGranted)
            {
                isDetectingGps = true
                fetchGpsDeviceLocation(context)
                { lat, lng, addr ->
                    latitudInput = lat
                    longitudInput = lng
                    ubicacionDireccionInput = addr
                    isDetectingGps = false
                }
            }
            else
            {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            Spacer(modifier = Modifier.height(10.dp))

            // App Brand Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(ConfirmButtonColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.tecnohand_logo),
                    contentDescription = "TecnoHand Logo",
                    modifier = Modifier.size(56.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "TecnoHand",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (StringsHelper.isEnglish(lang)) "Residential Access & Safety Control" else "Control de Acceso y Seguridad Residencial",
                fontSize = 12.sp,
                color = SubtitleGrayColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            when(screenState)
            {
                LoginScreenState.LANDING -> {
                    // Central Landing Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        // "Iniciar Sesión" Button with 15-Second Hold Gesture for Secret Admin Panel
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ConfirmButtonColor)
                                .pointerInput(Unit)
                                {
                                    detectTapGestures(
                                        onTap = {
                                            screenState = LoginScreenState.LOGIN_FORM
                                        },
                                        onPress = {
                                            val startTime = System.currentTimeMillis()
                                            isHoldingLoginButton = true
                                            val job = coroutineScope.launch{
                                                while(isHoldingLoginButton)
                                                {
                                                    val elapsed = System.currentTimeMillis() - startTime
                                                    holdProgress = (elapsed / 15000f).coerceIn(0f, 1f)
                                                    if(elapsed >= 15000L)
                                                    {
                                                        showAdminPinDialog = true
                                                        isHoldingLoginButton = false
                                                        holdProgress = 0f
                                                        break
                                                    }
                                                    delay(100)
                                                }
                                            }
                                            try
                                            {
                                                awaitRelease()
                                            }
                                            finally
                                            {
                                                isHoldingLoginButton = false
                                                job.cancel()
                                                holdProgress = 0f
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        )
                        {
                            if(isHoldingLoginButton && holdProgress > 0.05f)
                            {
                                LinearProgressIndicator(
                                    progress = { holdProgress },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp)),
                                    color = Color.White.copy(alpha = 0.35f),
                                    trackColor = Color.Transparent
                                )
                            }

                            Text(
                                text = if (isHoldingLoginButton && holdProgress > 0.05f)
                                {
                                    val secondsLeft = (15 - (holdProgress * 15)).toInt().coerceAtLeast(1)
                                    "Mantén presionado... (${secondsLeft}s)"
                                }
                                else
                                {
                                    if(StringsHelper.isEnglish(lang)) "Log In" else "Iniciar Sesión"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Clean "Registrar Usuario" Button
                        OutlinedButton(
                            onClick = {
                                onboardingStep = 1
                                screenState = LoginScreenState.REGISTER_FORM
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, ConfirmButtonColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ConfirmButtonColor)
                        )
                        {
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
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        )
                        {
                            Text(
                                text = if (StringsHelper.isEnglish(lang)) "Exit App" else "Salir",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                LoginScreenState.LOGIN_FORM -> {
                    // Login Card Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    )
                    {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .statusBarsPadding()
                                .navigationBarsPadding()
                                .imePadding(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        )
                        {
                            Row(verticalAlignment = Alignment.CenterVertically)
                            {
                                IconButton(onClick = { screenState = LoginScreenState.LANDING })
                                {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                                }
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Log In" else "Iniciar Sesión",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            OutlinedTextField(
                                value = loginUserInput,
                                onValueChange = { loginUserInput = it.filter { c -> !c.isWhitespace() } },
                                label = { Text(if (StringsHelper.isEnglish(lang)) "Email or User ID" else "Correo o ID de Usuario") },
                                placeholder = { Text("ej. residente@tecnohand.com o RES-1001") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ConfirmButtonColor) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = loginPasswordInput,
                                onValueChange = { loginPasswordInput = it.filter { c -> !c.isWhitespace() } },
                                label = { Text(if (StringsHelper.isEnglish(lang)) "Password" else "Contraseña") },
                                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ConfirmButtonColor) },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Mostrar contraseña",
                                            tint = SubtitleGrayColor
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if(loginErrorMessage != null)
                            {
                                Text(
                                    text = loginErrorMessage!!,
                                    color = Color(0xFFEF4444),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isSubmittingAuth = true
                                        loginErrorMessage = null
                                        try
                                        {
                                            val response = RetrofitClient.getService().login(
                                                LoginRequest(
                                                    email = loginUserInput.trim(),
                                                    password = loginPasswordInput
                                                )
                                            )
                                            if(response.isSuccessful && response.body() != null)
                                            {
                                                val u = response.body()!!
                                                val roleEnum = when (u.rol.uppercase())
                                                {
                                                    "GUARD" -> UserRole.GUARD
                                                    "ADMIN" -> UserRole.ADMIN
                                                    else -> UserRole.RESIDENT
                                                }
                                                val profile = UserProfile(
                                                    id = u.id.toString(),
                                                    email = u.email,
                                                    password = loginPasswordInput,
                                                    nombre = u.nombre,
                                                    telefono = u.telefono ?: "",
                                                    rol = roleEnum,
                                                    numeroVivienda = u.referencia ?: "",
                                                    vivienda = u.direccion ?: "",
                                                    ubicacionDireccion = u.direccion ?: "",
                                                    colonia = u.colonia ?: "Colonia Los Pinos"
                                                )
                                                userDataStore.registerUser(profile) // guarda sesión local
                                                onLoginSuccess(profile)
                                            }
                                            else
                                            {
                                                loginErrorMessage = "Credenciales incorrectas (${response.code()})"
                                            }
                                        }
                                        catch(e: Exception)
                                        {
                                            loginErrorMessage = "Error de conexión con la API: ${e.localizedMessage}"
                                        }
                                    }
                                },
                                enabled = loginUserInput.isNotBlank() && loginPasswordInput.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                            )
                            {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Enter App" else "Ingresar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            TextButton(
                                onClick = {
                                    resetEmailInput = loginUserInput
                                    resetNewPassInput = ""
                                    resetConfirmPassInput = ""
                                    resetMessage = null
                                    showForgotPasswordDialog = true
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Forgot your password?" else "¿Olvidaste tu contraseña?",
                                    color = ConfirmButtonColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                LoginScreenState.REGISTER_FORM -> {
                    // --- 3-STEP ONBOARDING REGISTRATION CONTAINER ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    )
                    {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        )
                        {
                            // Header with Back Button & Stepper Progress
                            val totalSteps = if (selectedRole == UserRole.GUARD) 1 else 3
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            )
                            {
                                IconButton(
                                    onClick = {
                                        if(onboardingStep > 1)
                                        {
                                            onboardingStep--
                                        }
                                        else
                                        {
                                            screenState = LoginScreenState.LANDING
                                        }
                                    }
                                )
                                {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                                }

                                Column(modifier = Modifier.weight(1f))
                                {
                                    Text(
                                        text = "OnBoarding Registro (${onboardingStep}/$totalSteps)",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = when(onboardingStep)
                                        {
                                            1 -> "Paso 1: Datos Personales y Cuenta"
                                            2 -> "Paso 2: Vivienda y Mapa OSMdroid"
                                            else -> "Paso 3: Núcleo Familiar"
                                        },
                                        fontSize = 12.sp,
                                        color = SubtitleGrayColor
                                    )
                                }
                            }

                            // Stepper Visual Progress Bar
                            LinearProgressIndicator(
                                progress = { onboardingStep / totalSteps.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ConfirmButtonColor,
                                trackColor = Color.LightGray.copy(alpha = 0.3f)
                            )

                            // Stepper Step Dots
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            )
                            {
                                StepChip(stepNumber = 1, title = "Datos", active = onboardingStep >= 1)
                                if(selectedRole != UserRole.GUARD)
                                {
                                    StepChip(stepNumber = 2, title = "Vivienda", active = onboardingStep >= 2)
                                    StepChip(stepNumber = 3, title = "Familia", active = onboardingStep >= 3)
                                }
                            }

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                            // --- STEP 1: DATOS SENCILLOS ---
                            if(onboardingStep == 1)
                            {
                                Text(
                                    text = "Ingresa tus datos principales:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Role selection
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                )
                                {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (selectedRole == UserRole.RESIDENT) ConfirmButtonColor else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedRole = UserRole.RESIDENT }
                                    )
                                    {
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
                                    )
                                    {
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
                                    value = nombresInput,
                                    onValueChange = { nombresInput = it.filter { c -> c.isLetter() || c == ' ' }.trimStart() },
                                    label = { Text("Nombres") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ConfirmButtonColor) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = apellidosInput,
                                    onValueChange = { apellidosInput = it.filter { c -> c.isLetter() || c == ' ' }.trimStart() },
                                    label = { Text("Apellidos") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ConfirmButtonColor) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                val isPhoneValid = telefonoInput.length == 10

                                OutlinedTextField(
                                    value = telefonoInput,
                                    onValueChange = { telefonoInput = it.filter { c -> c.isDigit() }.take(10) },
                                    label = { Text("Número de Teléfono (10 dígitos)") },
                                    placeholder = { Text("8711234567") },
                                    isError = telefonoInput.isNotEmpty() && !isPhoneValid,
                                    supportingText = {
                                        if(telefonoInput.isNotEmpty() && !isPhoneValid)
                                        {
                                            Text("Debe tener exactamente 10 dígitos (${telefonoInput.length}/10)")
                                        }
                                    },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = ConfirmButtonColor) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()

                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it.filter { c -> !c.isWhitespace() } },
                                    label = { Text("E-mail (Correo Electrónico)") },
                                    isError = emailInput.isNotEmpty() && !isEmailValid,
                                    supportingText = {
                                        if(emailInput.isNotEmpty() && !isEmailValid)
                                        {
                                            Text("Ingresa un correo válido (ej. usuario@correo.com)")
                                        }
                                    },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ConfirmButtonColor) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it.filter { c -> !c.isWhitespace() } },
                                    label = { Text("Contraseña") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ConfirmButtonColor) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible })
                                        {
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

                                // Fecha de Nacimiento con formato DD-MM-AAAA y Calendario Seleccionable (DatePicker)
                                OutlinedTextField(
                                    value = fechaNacimientoInput,
                                    onValueChange = { fechaNacimientoInput = it },
                                    label = { Text("Fecha de Nacimiento (DD-MM-AAAA)") },
                                    placeholder = { Text("DD-MM-AAAA") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = ConfirmButtonColor) },
                                    trailingIcon = {
                                        IconButton(onClick = { showDatePickerDialog = true }) {
                                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Seleccionar Fecha", tint = ConfirmButtonColor)
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if(loginErrorMessage != null)
                                {
                                    Text(text = loginErrorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                val step1Valid = nombresInput.isNotBlank() &&
                                        apellidosInput.isNotBlank() &&
                                        isPhoneValid &&
                                        isEmailValid &&
                                        emailInput.isNotBlank() &&
                                        passwordInput.isNotBlank() &&
                                        passwordInput.length >= 4

                                Button(
                                    onClick = {
                                        if(selectedRole == UserRole.GUARD)
                                        {
                                            submitRegistration()
                                        }
                                        else
                                        {
                                            onboardingStep = 2
                                        }
                                    },
                                    enabled = step1Valid && !isSubmittingAuth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if(selectedRole == UserRole.GUARD) Color(0xFF10B981) else ConfirmButtonColor
                                    )
                                )
                                {
                                    if(isSubmittingAuth && selectedRole == UserRole.GUARD)
                                    {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    }
                                    else
                                    {
                                        Text(
                                            text = if (selectedRole == UserRole.GUARD) "Completar Registro de Guardia" else "Siguiente: Vivienda y Ubicación ➔",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // --- STEP 2: APARTADOS DE LA VIVIENDA ---
                            if(onboardingStep == 2)
                            {
                                Text(
                                    text = "Configura los datos de tu vivienda:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // 1. Número de vivienda (ID de identificación en DB)
                                OutlinedTextField(
                                    value = numeroViviendaInput,
                                    onValueChange = { numeroViviendaInput = it },
                                    label = { Text("Número / ID de Vivienda (ID en BD)") },
                                    placeholder = { Text("ej. 101, A-204 o Calle Roble #101") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ConfirmButtonColor) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Text(
                                    text = "💡 Este número o identificador se registrará en la BD para rápida identificación del inmueble.",
                                    fontSize = 11.sp,
                                    color = SubtitleGrayColor
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // 2. Ubicación con Mapa e OSMdroid y Detección GPS Automática
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Text(
                                        text = "Ubicación Vivienda (OSMDroid):",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    TextButton(
                                        onClick = {
                                            val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                            val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                                            if(fineGranted || coarseGranted)
                                            {
                                                isDetectingGps = true
                                                fetchGpsDeviceLocation(context) { lat, lng, addr ->
                                                    latitudInput = lat
                                                    longitudInput = lng
                                                    ubicacionDireccionInput = addr
                                                    isDetectingGps = false
                                                }
                                            }
                                            else
                                            {
                                                locationPermissionLauncher.launch(
                                                    arrayOf(
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    )
                                                )
                                            }
                                        }
                                    )
                                    {
                                        Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = ConfirmButtonColor, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isDetectingGps) "Obteniendo GPS..." else "GPS Actual 📍",
                                            fontSize = 11.sp,
                                            color = ConfirmButtonColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                OsmRegistrationMap(
                                    latitude = latitudInput,
                                    longitude = longitudInput,
                                    onLocationChanged = { lat, lng ->
                                        latitudInput = lat
                                        longitudInput = lng
                                        ubicacionDireccionInput = "Lat: ${"%.4f".format(lat)}, Lng: ${"%.4f".format(lng)}"
                                    }
                                )

                                // Presets chips for quick location selection
                                Text(text = "Presets o toque en mapa:", fontSize = 11.sp, color = SubtitleGrayColor)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                )
                                {
                                    PresetChip("Caseta Main", 19.432608, -99.133209) { lat, lng ->
                                        latitudInput = lat; longitudInput = lng; ubicacionDireccionInput = "Caseta Principal, Av. Los Pinos 100"
                                    }
                                    PresetChip("Roble 101", 19.434100, -99.131500) { lat, lng ->
                                        latitudInput = lat; longitudInput = lng; ubicacionDireccionInput = "Calle Roble #101"
                                    }
                                    PresetChip("Fresnos 88", 19.435500, -99.129800) { lat, lng ->
                                        latitudInput = lat; longitudInput = lng; ubicacionDireccionInput = "Paseo Fresnos 88"
                                    }
                                }

                                Text(
                                    text = "📍 Ubicación seleccionada: $ubicacionDireccionInput",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ConfirmButtonColor
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // 3. Cantidad de Residentes
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Row(verticalAlignment = Alignment.CenterVertically)
                                    {
                                        Icon(imageVector = Icons.Default.People, contentDescription = null, tint = ConfirmButtonColor)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Cantidad de Residentes:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically)
                                    {
                                        Button(
                                            onClick = { if (cantidadResidentesCount > 1) cantidadResidentesCount-- },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        {
                                            Text("-", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "$cantidadResidentesCount",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        Button(
                                            onClick = { cantidadResidentesCount++ },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        {
                                            Text("+", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                )
                                {
                                    OutlinedButton(
                                        onClick = { onboardingStep = 1 },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    {
                                        Text("⬅ Anterior")
                                    }

                                    Button(
                                        onClick = { onboardingStep = 3 },
                                        enabled = numeroViviendaInput.isNotBlank(),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                                    )
                                    {
                                        Text("Siguiente: Familia ➔", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            // --- STEP 3: APARTADO DE FAMILIA ---
                            if(onboardingStep == 3)
                            {
                                Text(
                                    text = "Apartado de Familia (Integrantes):",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Agrega a los miembros del hogar indicando su respectivo parentesco:",
                                    fontSize = 12.sp,
                                    color = SubtitleGrayColor
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    OutlinedTextField(
                                        value = nuevoFamiliarNombre,
                                        onValueChange = { nuevoFamiliarNombre = it },
                                        placeholder = { Text("Nombre del familiar") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = parentescoExpanded,
                                        onExpandedChange = { parentescoExpanded = !parentescoExpanded },
                                        modifier = Modifier.weight(1f)
                                    )
                                    {
                                        OutlinedTextField(
                                            value = nuevoFamiliarParentesco,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentescoExpanded) },
                                            modifier = Modifier.menuAnchor(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = parentescoExpanded,
                                            onDismissRequest = { parentescoExpanded = false }
                                        )
                                        {
                                            parentescoOptions.forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option) },
                                                    onClick = {
                                                        nuevoFamiliarParentesco = option
                                                        parentescoExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if(nuevoFamiliarNombre.isNotBlank())
                                            {
                                                integrantesFamiliaList.add(
                                                    FamiliaMember(
                                                        nombre = nuevoFamiliarNombre.trim(),
                                                        parentesco = nuevoFamiliarParentesco
                                                    )
                                                )
                                                nuevoFamiliarNombre = ""
                                            }
                                        },
                                        enabled = nuevoFamiliarNombre.isNotBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                                    )
                                    {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
                                    }
                                }

                                // List of Family Members
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface
                                )
                                {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    )
                                    {
                                        if(integrantesFamiliaList.isEmpty())
                                        {
                                            Text(
                                                text = "No has agregado familiares aún. Puedes omitir o agregar después.",
                                                fontSize = 12.sp,
                                                color = SubtitleGrayColor,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        else
                                        {
                                            integrantesFamiliaList.forEachIndexed { index, member ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                )
                                                {
                                                    Row(verticalAlignment = Alignment.CenterVertically)
                                                    {
                                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ConfirmButtonColor, modifier = Modifier.size(18.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "${member.nombre} (${member.parentesco})",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }

                                                    IconButton(onClick = { integrantesFamiliaList.removeAt(index) }) {
                                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                )
                                {
                                    OutlinedButton(
                                        onClick = { onboardingStep = 2 },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    {
                                        Text("⬅ Anterior")
                                    }

                                    // Complete Registration Button -> Saves to DataStore!
                                    Button(
                                        onClick = {
                                            submitRegistration()
                                        },
                                        enabled = !isSubmittingAuth,
                                        modifier = Modifier
                                            .weight(1.4f)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                    )
                                    {
                                        if(isSubmittingAuth)
                                        {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                        }
                                        else
                                        {
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Completar Registro", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO DE CAMBIO DE CONTRASEÑA ---
    if(showForgotPasswordDialog)
    {
        AlertDialog(
            onDismissRequest = { if (!isResettingPass) showForgotPasswordDialog = false },
            title = {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Reset Password" else "Cambiar Contraseña",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp))
                {
                    Text(
                        text = "Ingresa tu correo registrado y tu nueva contraseña (sin espacios):",
                        fontSize = 12.sp,
                        color = SubtitleGrayColor
                    )

                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it.filter { c -> !c.isWhitespace() } },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = resetNewPassInput,
                        onValueChange = { resetNewPassInput = it.filter { c -> !c.isWhitespace() } },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = resetConfirmPassInput,
                        onValueChange = { resetConfirmPassInput = it.filter { c -> !c.isWhitespace() } },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if(resetMessage != null)
                    {
                        Text(
                            text = resetMessage!!,
                            color = if (isResetError) Color(0xFFEF4444) else Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(resetNewPassInput != resetConfirmPassInput)
                        {
                            isResetError = true
                            resetMessage = "Las contraseñas no coinciden."
                            return@Button
                        }
                        if(resetNewPassInput.length < 4)
                        {
                            isResetError = true
                            resetMessage = "La contraseña debe tener al menos 4 caracteres."
                            return@Button
                        }
                        coroutineScope.launch{
                            isResettingPass = true
                            resetMessage = null
                            try
                            {
                                val res = RetrofitClient.getService().resetPassword(
                                    ResetPasswordRequest(
                                        email = resetEmailInput.trim(),
                                        newPassword = resetNewPassInput
                                    )
                                )
                                if(res.isSuccessful)
                                {
                                    isResetError = false
                                    resetMessage = "¡Contraseña actualizada! Ya puedes iniciar sesión."
                                    delay(1200)
                                    showForgotPasswordDialog = false
                                }
                                else
                                {
                                    isResetError = true
                                    resetMessage = if (res.code() == 404) {
                                        "No se encontró ninguna cuenta con ese correo."
                                    }
                                    else
                                    {
                                        "Error al actualizar (${res.code()})."
                                    }
                                }
                            }
                            catch(e: Exception)
                            {
                                isResetError = true
                                resetMessage = "Error de conexión: ${e.localizedMessage}"
                            }
                            finally
                            {
                                isResettingPass = false
                            }
                        }
                    },
                    enabled = !isResettingPass && resetEmailInput.isNotBlank() && resetNewPassInput.isNotBlank() && resetConfirmPassInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                )
                {
                    if(isResettingPass)
                    {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                    else
                    {
                        Text("Guardar", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showForgotPasswordDialog = false },
                    enabled = !isResettingPass
                )
                {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DATE PICKER DIALOG ---
    if(showDatePickerDialog)
    {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            fechaNacimientoInput = formatter.format(Date(millis))
                        }
                        showDatePickerDialog = false
                    }
                )
                {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false })
                {
                    Text("Cancelar")
                }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    // --- SECRET ADMIN PIN DIALOG (Triggered by 15-second long press on "Iniciar Sesión") ---
    if(showAdminPinDialog)
    {
        AlertDialog(
            onDismissRequest = {
                showAdminPinDialog = false
                adminPinInput = ""
                adminPinError = false
            },
            title = {
                Text(
                    text = "🔐 Panel de Desarrolladores",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp))
                {
                    Text(
                        text = "Ingrese el PIN de 4 dígitos para acceder al panel de administración y pruebas:",
                        fontSize = 13.sp,
                        color = SubtitleGrayColor
                    )

                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = {
                            if(it.length <= 4 && it.all { char -> char.isDigit() })
                            {
                                adminPinInput = it
                                adminPinError = false
                            }
                        },
                        label = { Text("PIN de 4 dígitos (3141)") },
                        placeholder = { Text("3141") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if(adminPinError)
                    {
                        Text(
                            text = "❌ PIN incorrecto. Intente nuevamente.",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(adminPinInput == "3141")
                        {
                            showAdminPinDialog = false
                            adminPinInput = ""
                            adminPinError = false
                            showAdminDeveloperPanel = true
                        }
                        else
                        {
                            adminPinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
                )
                {
                    Text("Ingresar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAdminPinDialog = false
                        adminPinInput = ""
                        adminPinError = false
                    }
                )
                {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- SECRET ADMIN DEVELOPER PANEL DIALOG (Admin 1: Residente, Admin 2: Guardia) ---
    if(showAdminDeveloperPanel)
    {
        AlertDialog(
            onDismissRequest = { showAdminDeveloperPanel = false },
            title = {
                Text(
                    text = "🛠️ Panel Admin y Desarrolladores",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                )
                {
                    Text(
                        text = "Selecciona un acceso directo por rol para pruebas:",
                        fontSize = 13.sp,
                        color = SubtitleGrayColor
                    )

                    // Admin 1 (Resident Preset)
                    Button(
                        onClick = {
                            coroutineScope.launch{
                                val user = userDataStore.authenticateUser("residente@tecnohand.com", "123456")
                                if(user != null)
                                {
                                    showAdminDeveloperPanel = false
                                    onLoginSuccess(user)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    )
                    {
                        Text("Admin 1: Iniciar como Residente 🏠", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Admin 2 (Guard Preset)
                    Button(
                        onClick = {
                            coroutineScope.launch{
                                val user = userDataStore.authenticateUser("guardia@tecnohand.com", "123456")
                                if(user != null)
                                {
                                    showAdminDeveloperPanel = false
                                    onLoginSuccess(user)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    )
                    {
                        Text("Admin 2: Iniciar como Guardia 🛡️", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Admin 3 (Administrador Preset)
                    Button(
                        onClick = {
                            coroutineScope.launch{
                                val adminUser = UserProfile(
                                    id = "ADM-001",
                                    email = "admin@tecnohand.com",
                                    password = "123456",
                                    nombre = "Administrador TecnoHand",
                                    rol = UserRole.ADMIN,
                                    colonia = "Colonia Los Pinos"
                                )
                                userDataStore.registerUser(adminUser)
                                showAdminDeveloperPanel = false
                                onLoginSuccess(adminUser)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060060))
                    )
                    {
                        Text("Admin 3: Iniciar como Administrador 👑", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAdminDeveloperPanel = false })
                {
                    Text("Cerrar")
                }
            }
        )
    }
}

private fun fetchGpsDeviceLocation(
    context: Context,
    onLocationRetrieved: (Double, Double, String) -> Unit
)
{
    if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    )
    {
        return
    }

    try
    {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null)
            {
                val lat = location.latitude
                val lng = location.longitude
                var addressText = "Lat: %.4f, Lng: %.4f".format(lat, lng)

                try
                {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    if(!addresses.isNullOrEmpty())
                    {
                        val addr = addresses[0]
                        val thoroughfare = addr.thoroughfare ?: ""
                        val locality = addr.subLocality ?: addr.locality ?: ""
                        val formatted = "$thoroughfare, $locality".trim(',', ' ')
                        if(formatted.isNotBlank())
                        {
                            addressText = formatted
                        }
                    }
                }
                catch(e: Exception)
                {
                    // Fallback to coordinates format
                }

                onLocationRetrieved(lat, lng, addressText)
            }
        }
    }
    catch(e: Exception)
    {
        // Exception fallback
    }
}

@Composable
private fun StepChip(stepNumber: Int, title: String, active: Boolean)
{
    Row(verticalAlignment = Alignment.CenterVertically)
    {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (active) ConfirmButtonColor else Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = "$stepNumber",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) ConfirmButtonColor else SubtitleGrayColor
        )
    }
}

@Composable
private fun PresetChip(label: String, lat: Double, lng: Double, onClick: (Double, Double) -> Unit)
{
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ConfirmButtonColor.copy(alpha = 0.12f),
        modifier = Modifier.clickable { onClick(lat, lng) }
    )
    {
        Text(
            text = label,
            fontSize = 10.sp,
            color = ConfirmButtonColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun OsmRegistrationMap(
    latitude: Double,
    longitude: Double,
    onLocationChanged: (Double, Double) -> Unit
)
{
    val context = LocalContext.current

    LaunchedEffect(Unit)
    {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid_reg", Context.MODE_PRIVATE))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(14.dp))
    )
    {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply{
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(16.0)
                    controller.setCenter(GeoPoint(latitude, longitude))

                    val marker = Marker(this).apply{
                        position = GeoPoint(latitude, longitude)
                        title = "Ubicación Vivienda"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    overlays.add(marker)

                    val mapEventsReceiver = object : MapEventsReceiver
                    {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean
                        {
                            if(p != null)
                            {
                                onLocationChanged(p.latitude, p.longitude)
                            }
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint?): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(mapEventsReceiver))
                }
            },
            update = { mapView ->
                mapView.controller.setCenter(GeoPoint(latitude, longitude))
                mapView.overlays.filterIsInstance<Marker>().firstOrNull()?.apply{
                    position = GeoPoint(latitude, longitude)
                }
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
