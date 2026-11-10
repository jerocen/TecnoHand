package com.atom.tecnohandapp.data.models

enum class UserRole {
    GUARD,    // Guardia: Escaneo QR, Reportes, Alertas
    RESIDENT  // Residente/Habitante: Votaciones, Crear QR Invitados, Reportes, Alertas, Mi Familia/Vivienda
}

data class UserProfile(
    val id: String = "RES-1001",
    val email: String = "residente@tecnohand.com",
    val nombre: String = "Pedro Ramírez",
    val rol: UserRole = UserRole.RESIDENT,
    val vivienda: String = "Calle Roble #101",
    val colonia: String = "Colonia Los Pinos",
    val telefono: String = "+52 55 1234 5678",
    val integrantesFamilia: List<String> = listOf("María Ramírez (Esposa)", "Lucas Ramírez (Hijo)", "Sofía Ramírez (Hija)")
)
