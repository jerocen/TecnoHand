package com.atom.tecnohandapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atom.tecnohandapp.data.models.ReportItem

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val DarkTextColor = Color(0xFF0A1838)
private val SubtitleGrayColor = Color(0xFF64748B)

private val IniciadoColor = Color(0xFFF59E0B)  // Naranja / Ámbar
private val EnProcesoColor = Color(0xFF3B82F6) // Azul
private val TerminadoColor = Color(0xFF10B981) // Verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAdminScreen(
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Todos", "Iniciado", "En Proceso", "Terminado")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Title & Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Panel de Reportes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextColor
                    )
                    Text(
                        text = "Administración de estado e incidentes",
                        fontSize = 13.sp,
                        color = SubtitleGrayColor
                    )
                }

                IconButton(onClick = { viewModel.loadReports() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        tint = ConfirmButtonColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // State Filter Tabs
            PrimaryTabRow(
                selectedTabIndex = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0),
                containerColor = Color(0xFFF1F5F9),
                contentColor = ConfirmButtonColor,
                modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            ) {
                tabs.forEach { tabName ->
                    val isSelected = uiState.selectedTab == tabName
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setTabFilter(tabName) },
                        text = {
                            Text(
                                text = tabName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ConfirmButtonColor else SubtitleGrayColor
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Report List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ConfirmButtonColor)
                }
            } else if (uiState.reports.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron reportes en el estado '${uiState.selectedTab}'",
                        color = SubtitleGrayColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(uiState.reports) { report ->
                        ReportCardItem(
                            report = report,
                            onStatusChangeClick = { viewModel.setSelectedReportDetail(report) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Create Report
        FloatingActionButton(
            onClick = { viewModel.setShowCreateDialog(true) },
            containerColor = ConfirmButtonColor,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Reporte")
        }
    }

    // Dialogs
    if (uiState.showCreateDialog) {
        CreateReportDialog(
            onDismiss = { viewModel.setShowCreateDialog(false) },
            onCreate = { cat, desc, prio ->
                viewModel.createReport(cat, desc, prio)
            }
        )
    }

    if (uiState.selectedReportForDetail != null) {
        UpdateStatusDialog(
            report = uiState.selectedReportForDetail!!,
            onDismiss = { viewModel.setSelectedReportDetail(null) },
            onUpdateStatus = { reportId, newStatus ->
                viewModel.updateReportStatus(reportId, newStatus)
            }
        )
    }
}

@Composable
private fun ReportCardItem(
    report: ReportItem,
    onStatusChangeClick: () -> Unit
) {
    val (statusColor, statusIcon) = when (report.estadoUi) {
        "Iniciado" -> Pair(IniciadoColor, Icons.Default.Info)
        "En Proceso" -> Pair(EnProcesoColor, Icons.Default.HourglassTop)
        "Terminado" -> Pair(TerminadoColor, Icons.Default.CheckCircle)
        else -> Pair(SubtitleGrayColor, Icons.Default.Info)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStatusChangeClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category & Housing
                Text(
                    text = "${report.categoria.uppercase()} • ${report.viviendaDireccion}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtitleGrayColor
                )

                // Status Badge (Iniciado, En Proceso, Terminado)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = report.estadoUi,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = report.descripcion,
                fontSize = 14.sp,
                color = DarkTextColor,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer (Reported By, Priority, Action)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reportó: ${report.reportadoPorNombre} • ${report.creadoEn.take(16)}",
                    fontSize = 11.sp,
                    color = SubtitleGrayColor
                )

                TextButton(onClick = onStatusChangeClick) {
                    Text(
                        text = "Cambiar Estado >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConfirmButtonColor
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateStatusDialog(
    report: ReportItem,
    onDismiss: () -> Unit,
    onUpdateStatus: (id: Long, status: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Administrar Reporte #${report.id}",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = DarkTextColor
            )
        },
        text = {
            Column {
                Text(
                    text = report.descripcion,
                    fontSize = 14.sp,
                    color = DarkTextColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Estado actual: ${report.estadoUi}",
                    fontSize = 13.sp,
                    color = SubtitleGrayColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Seleccione el nuevo estado:",
                    fontSize = 13.sp,
                    color = DarkTextColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons for changing status
                Button(
                    onClick = { onUpdateStatus(report.id, "Iniciado") },
                    colors = ButtonDefaults.buttonColors(containerColor = IniciadoColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Marcar como INICIADO", color = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { onUpdateStatus(report.id, "En Proceso") },
                    colors = ButtonDefaults.buttonColors(containerColor = EnProcesoColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Marcar como EN PROCESO", color = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { onUpdateStatus(report.id, "Terminado") },
                    colors = ButtonDefaults.buttonColors(containerColor = TerminadoColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Marcar como TERMINADO", color = Color.White)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateReportDialog(
    onDismiss: () -> Unit,
    onCreate: (categoria: String, descripcion: String, prioridad: String) -> Unit
) {
    var categoria by remember { mutableStateOf("seguridad") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("media") }

    var catExpanded by remember { mutableStateOf(false) }
    var prioExpanded by remember { mutableStateOf(false) }

    val categoriasList = listOf("seguridad", "bienestar", "infraestructura")
    val prioridadesList = listOf("baja", "media", "alta", "urgente")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Crear Nuevo Reporte", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = categoria.uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categoriasList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.uppercase()) },
                                onClick = {
                                    categoria = item
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = prioExpanded,
                    onExpandedChange = { prioExpanded = !prioExpanded }
                ) {
                    OutlinedTextField(
                        value = prioridad.uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = prioExpanded,
                        onDismissRequest = { prioExpanded = false }
                    ) {
                        prioridadesList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.uppercase()) },
                                onClick = {
                                    prioridad = item
                                    prioExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description Input
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del incidente") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(categoria, descripcion, prioridad) },
                enabled = descripcion.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmButtonColor)
            ) {
                Text("Guardar y Registrar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
