package com.atom.tecnohandapp.data.models

enum class UserRole
{
    GUARD,    // Guardia: Escaneo QR, Reportes, Alertas
    RESIDENT,  // Residente/Habitante: Votaciones, Crear QR Invitados, Reportes, Alertas, Mi Familia/Vivienda
    ADMIN
}

data class FamiliaMember(
    val nombre: String = "",
    val parentesco: String = "Familiar"
)
{
    fun toFormattedString(): String = if (parentesco.isNotBlank()) "$nombre ($parentesco)" else nombre
}

data class UserProfile(
    val id: String = "RES-1001",
    val email: String = "residente@tecnohand.com",
    val password: String = "123456",
    val nombres: String = "Pedro",
    val apellidos: String = "Ramírez",
    val nombre: String = "Pedro Ramírez",
    val telefono: String = "+52 55 1234 5678",
    val fechaNacimiento: String = "15-05-1990",
    val rol: UserRole = UserRole.RESIDENT,
    val numeroVivienda: String = "101",
    val vivienda: String = "Calle Roble #101",
    val latitud: Double = 19.432608,
    val longitud: Double = -99.133209,
    val ubicacionDireccion: String = "Caseta Principal, Av. Los Pinos 100",
    val cantidadResidentes: Int = 3,
    val colonia: String = "Colonia Los Pinos",
    val integrantesFamilia: List<String> = listOf("María Ramírez (Esposa)", "Lucas Ramírez (Hijo)", "Sofía Ramírez (Hija)")
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateProfileRequest(
    val nombre: String,
    val telefono: String
)

data class RegisterRequest(
    val nombre: String,
    val telefono: String,
    val email: String,
    val password: String,
    val rol: String,
    val direccion: String,
    val referencia: String,
    val colonia: String = "Colonia Los Pinos"
)

data class AdminUserItem(
    val id: Long,
    val persona_id: Long,
    val nombre: String,
    val telefono: String?,
    val email: String,
    val rol: String,
    val estado: String
)

data class AuthUserResponse(
    val id: Long,
    val persona_id: Long,
    val vivienda_id: Long? = null,
    val colonia_id: Long? = null,
    val nombre: String,
    val telefono: String?,
    val email: String,
    val rol: String,
    val direccion: String? = null,
    val referencia: String? = null,
    val colonia: String? = null
)

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String
)
