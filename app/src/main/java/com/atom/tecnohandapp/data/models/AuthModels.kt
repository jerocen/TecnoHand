package com.atom.tecnohandapp.data.models

enum class UserRole {
    GUARD,    // Guardia: Escaneo QR, Reportes, Alertas
    RESIDENT  // Residente/Habitante: Votaciones, Crear QR Invitados, Reportes, Alertas, Mi Familia/Vivienda
}

data class FamiliaMember(
    val nombre: String = "",
    val parentesco: String = "Familiar"
) {
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
