package com.atom.tecnohandapp.data.local

import com.atom.tecnohandapp.data.models.AppLanguage

object StringsHelper {

    fun isEnglish(language: AppLanguage): Boolean = language == AppLanguage.ENGLISH

    // --- Navigation Tabs ---
    fun tabQr(language: AppLanguage): String = if (isEnglish(language)) "QR Access" else "Acceso QR"
    fun tabReports(language: AppLanguage): String = if (isEnglish(language)) "Reports" else "Reportes"
    fun tabAlerts(language: AppLanguage): String = if (isEnglish(language)) "Alerts" else "Alertas"

    // --- Header Subtitles ---
    fun headerSubQr(language: AppLanguage): String = if (isEnglish(language)) "QR Access Module" else "Módulo de Acceso QR"
    fun headerSubReports(language: AppLanguage): String = if (isEnglish(language)) "Reports Admin Panel" else "Panel de Reportes"
    fun headerSubAlerts(language: AppLanguage): String = if (isEnglish(language)) "Alerts & Notifications" else "Alertas y Notificaciones"

    // --- QR Scanner Screen ---
    fun pointCamera(language: AppLanguage): String = if (isEnglish(language)) "Point camera at QR code..." else "Apunte al código QR..."
    fun verifyingServer(language: AppLanguage): String = if (isEnglish(language)) "Verifying QR with server..." else "Verificando QR con el servidor..."
    fun detected(language: AppLanguage): String = if (isEnglish(language)) "Detected:" else "Detectado:"
    fun confirmScan(language: AppLanguage): String = if (isEnglish(language)) "Confirm Scan" else "Confirmar Escaneo"
    fun manualInput(language: AppLanguage): String = if (isEnglish(language)) "Manual Entry" else "Ingreso Manual"
    fun scanFromGallery(language: AppLanguage): String = if (isEnglish(language)) "Scan from Gallery / Photo" else "Escanear desde Galería / Foto"
    fun footerHint(language: AppLanguage): String = if (isEnglish(language)) "Use manual entry if QR is unreadable." else "Use ingreso manual si el QR no es legible."

    // --- QR Validation Messages ---
    fun accessGranted(language: AppLanguage): String = if (isEnglish(language)) "Access GRANTED!" else "¡Acceso PERMITIDO!"
    fun accessDenied(language: AppLanguage): String = if (isEnglish(language)) "Access DENIED" else "Acceso RECHAZADO"
    fun invalidQrUrl(language: AppLanguage): String = if (isEnglish(language)) "Invalid QR Code / URL Detected" else "Código QR No Válido / URL Detectada"
    fun invalidUrlMessage(language: AppLanguage): String = if (isEnglish(language))
        "The scanned QR code contains an external URL or non-TecnoHand format. Please scan a valid access QR code."
    else
        "El código QR escaneado contiene un enlace URL o formato externo no registrado en TecnoHand. Por favor escanee un QR de acceso válido."

    fun understandBtn(language: AppLanguage): String = if (isEnglish(language)) "Got it / Scan Another" else "Entendido / Escanear Otro"
    fun markExit(language: AppLanguage): String = if (isEnglish(language)) "Mark Exit" else "Marcar Salida"

    // --- Reports Admin Screen ---
    fun reportsAdminTitle(language: AppLanguage): String = if (isEnglish(language)) "Reports Panel" else "Panel de Reportes"
    fun reportsAdminSub(language: AppLanguage): String = if (isEnglish(language)) "Incident & status management" else "Administración de estado e incidentes"
    fun tabAll(language: AppLanguage): String = if (isEnglish(language)) "All" else "Todos"
    fun tabInitiated(language: AppLanguage): String = if (isEnglish(language)) "Initiated" else "Iniciado"
    fun tabInProgress(language: AppLanguage): String = if (isEnglish(language)) "In Progress" else "En Proceso"
    fun tabCompleted(language: AppLanguage): String = if (isEnglish(language)) "Completed" else "Terminado"
    fun noReportsMsg(language: AppLanguage, tab: String): String = if (isEnglish(language)) "No reports found for status '$tab'" else "No se encontraron reportes en el estado '$tab'"
    fun reportedBy(language: AppLanguage): String = if (isEnglish(language)) "Reported by:" else "Reportó:"
    fun changeStatus(language: AppLanguage): String = if (isEnglish(language)) "Change Status >" else "Cambiar Estado >"
    fun manageReport(language: AppLanguage, id: Long): String = if (isEnglish(language)) "Manage Report #$id" else "Administrar Reporte #$id"
    fun currentStatus(language: AppLanguage, status: String): String = if (isEnglish(language)) "Current status: $status" else "Estado actual: $status"
    fun selectNewStatus(language: AppLanguage): String = if (isEnglish(language)) "Select new status:" else "Seleccione el nuevo estado:"

    // --- Reports Form Screen ---
    fun generateReportTitle(language: AppLanguage): String = if (isEnglish(language)) "GENERATE REPORT" else "GENERAR REPORTE"
    fun generateReportSub(language: AppLanguage): String = if (isEnglish(language)) "Complete form to notify an incident." else "Complete el formulario para notificar un incidente."
    fun categoryLabel(language: AppLanguage): String = if (isEnglish(language)) "Incident Category" else "Categoría del incidente"
    fun priorityLabel(language: AppLanguage): String = if (isEnglish(language)) "Report Priority" else "Prioridad del reporte"
    fun descriptionLabel(language: AppLanguage): String = if (isEnglish(language)) "Detailed Description" else "Descripción detallada"
    fun descriptionPlaceholder(language: AppLanguage): String = if (isEnglish(language)) "Explain what happened, reference points or relevant details..." else "Explica qué sucedió, puntos de referencia o detalles relevantes..."
    fun photoEvidenceLabel(language: AppLanguage): String = if (isEnglish(language)) "Photo Evidence (Up to 30MB / 3000x3000px)" else "Evidencia fotográfica (Hasta 30MB / 3000x3000px)"
    fun tapToAttachPhoto(language: AppLanguage): String = if (isEnglish(language)) "Tap to attach high-res photo (Up to 30MB / 3000x3000px)" else "Toca para adjuntar fotos en alta resolución (Hasta 30MB / 3000x3000px)"
    fun locationLabel(language: AppLanguage): String = if (isEnglish(language)) "Incident Location" else "Ubicación del incidente"
    fun useMyGps(language: AppLanguage): String = if (isEnglish(language)) "Use my GPS 📍" else "Usar mi GPS 📍"
    fun submitReportBtn(language: AppLanguage): String = if (isEnglish(language)) "Submit Report" else "Enviar Reporte"

    // --- Alerts Screen ---
    fun alertsTitle(language: AppLanguage): String = if (isEnglish(language)) "Alerts & Notifications" else "Alertas y Notificaciones"
    fun alertsSub(language: AppLanguage): String = if (isEnglish(language)) "Notices sorted by status and risk level" else "Avisos clasificados según el estado y nivel de riesgo"
    fun panicCardTitle(language: AppLanguage): String = if (isEnglish(language)) "Emergency / Panic Button" else "Botón de Emergencia / Pánico"
    fun panicCardSub(language: AppLanguage): String = if (isEnglish(language)) "Send immediate alert signal to security booth" else "Enviar señal inmediata de alerta a la caseta"
    fun panicBtnText(language: AppLanguage): String = if (isEnglish(language)) "PANIC" else "PÁNICO"
    fun tabCritical(language: AppLanguage): String = if (isEnglish(language)) "Critical" else "Críticas"
    fun resolveBtn(language: AppLanguage): String = if (isEnglish(language)) "Resolve" else "Resolver"
    fun noAlertsMsg(language: AppLanguage, filter: String): String = if (isEnglish(language)) "No notifications found for '$filter'" else "No hay notificaciones para '$filter'"

    // --- Settings ---
    fun settingsTitle(language: AppLanguage): String = if (isEnglish(language)) "Settings & Preferences" else "Ajustes y Configuración"
    fun tabServer(language: AppLanguage): String = if (isEnglish(language)) "🌐 Server" else "🌐 Servidor"
    fun tabTheme(language: AppLanguage): String = if (isEnglish(language)) "🎨 Theme" else "🎨 Tema"
    fun tabLanguage(language: AppLanguage): String = if (isEnglish(language)) "🗣️ Language" else "🗣️ Idioma"
    fun saveApply(language: AppLanguage): String = if (isEnglish(language)) "Save & Apply" else "Guardar y Aplicar"
    fun closeBtn(language: AppLanguage): String = if (isEnglish(language)) "Close" else "Cerrar"

    // --- History ---
    fun historyTitle(language: AppLanguage): String = if (isEnglish(language)) "Registered Access History" else "Historial de Accesos Registrados"
    fun noHistoryLogs(language: AppLanguage): String = if (isEnglish(language)) "No access logs found in database" else "No hay registros de acceso en la base de datos"
}
