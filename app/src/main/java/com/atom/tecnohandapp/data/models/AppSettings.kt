package com.atom.tecnohandapp.data.models

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

enum class AppLanguage {
    SPANISH, ENGLISH
}

data class VersionInfo(
    val versionName: String = "v1.0.0",
    val versionCode: Int = 1,
    val releaseDate: String = "Febrero 2026",
    val isLatestVersion: Boolean = true,
    val changelog: List<DevLogEntry> = listOf(
        DevLogEntry(
            version = "v1.0.0",
            fecha = "11 Feb 2026",
            titulo = "Módulo QR Completo",
            detalles = listOf(
                "Escaneo automático de códigos QR con cámara MLKit.",
                "Soporte para escaneo de fotos y capturas desde la Galería.",
                "Ingreso manual con acceso rápido a tokens de prueba.",
                "Procesamiento e incremento automático de usos en MySQL."
            )
        ),
        DevLogEntry(
            version = "v0.9.5",
            fecha = "10 Feb 2026",
            titulo = "Panel de Reportes de Administración",
            detalles = listOf(
                "Control de estados: Iniciado, En Proceso, Terminado.",
                "Creación y cambio dinámico de estatus en tiempo real.",
                "Filtros por pestañas de estado e integración con API Node.js."
            )
        ),
        DevLogEntry(
            version = "v0.9.0",
            fecha = "09 Feb 2026",
            titulo = "Alertas y Notificaciones",
            detalles = listOf(
                "Avisos clasificados por nivel de riesgo (Crítica, En Proceso, Resuelta).",
                "Botón de Pánico y Emergencia con transmisión a caseta de seguridad."
            )
        )
    )
)

data class DevLogEntry(
    val version: String,
    val fecha: String,
    val titulo: String,
    val detalles: List<String>
)
