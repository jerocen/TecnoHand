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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.ReportItem

private val ConfirmButtonColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)

private val IniciadoColor = Color(0xFFF59E0B)  // Naranja / Ámbar
private val EnProcesoColor = Color(0xFF3B82F6) // Azul
private val TerminadoColor = Color(0xFF10B981) // Verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAdminScreen(
    viewModel: ReportsViewModel = viewModel(),
    lang: AppLanguage = AppLanguage.SPANISH
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf(
        StringsHelper.tabAll(lang),
        StringsHelper.tabInitiated(lang),
        StringsHelper.tabInProgress(lang),
        StringsHelper.tabCompleted(lang)
    )
    var showFormScreen by remember { mutableStateOf(false) }

    if (showFormScreen) {
        ReportsFormScreen(
            reportsViewModel = viewModel,
            lang = lang,
            onBack = { showFormScreen = false }
        )
        return
    }

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
                        text = StringsHelper.reportsAdminTitle(lang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = StringsHelper.reportsAdminSub(lang),
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
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = ConfirmButtonColor,
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
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
                        text = StringsHelper.noReportsMsg(lang, uiState.selectedTab),
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
                            lang = lang,
                            onStatusChangeClick = { viewModel.setSelectedReportDetail(report) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Open Full Form
        FloatingActionButton(
            onClick = { showFormScreen = true },
            containerColor = ConfirmButtonColor,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = StringsHelper.generateReportTitle(lang))
        }
    }

    if (uiState.selectedReportForDetail != null) {
        UpdateStatusDialog(
            report = uiState.selectedReportForDetail!!,
            lang = lang,
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
    lang: AppLanguage,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                color = MaterialTheme.colorScheme.onSurface,
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
                    text = "${StringsHelper.reportedBy(lang)} ${report.reportadoPorNombre} • ${report.creadoEn.take(16)}",
                    fontSize = 11.sp,
                    color = SubtitleGrayColor
                )

                TextButton(onClick = onStatusChangeClick) {
                    Text(
                        text = StringsHelper.changeStatus(lang),
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
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onUpdateStatus: (id: Long, status: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = StringsHelper.manageReport(lang, report.id),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = report.descripcion,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = StringsHelper.currentStatus(lang, report.estadoUi),
                    fontSize = 13.sp,
                    color = SubtitleGrayColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = StringsHelper.selectNewStatus(lang),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onUpdateStatus(report.id, "Iniciado") },
                    colors = ButtonDefaults.buttonColors(containerColor = IniciadoColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(StringsHelper.tabInitiated(lang).uppercase(), color = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { onUpdateStatus(report.id, "En Proceso") },
                    colors = ButtonDefaults.buttonColors(containerColor = EnProcesoColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(StringsHelper.tabInProgress(lang).uppercase(), color = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { onUpdateStatus(report.id, "Terminado") },
                    colors = ButtonDefaults.buttonColors(containerColor = TerminadoColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(StringsHelper.tabCompleted(lang).uppercase(), color = Color.White)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(StringsHelper.closeBtn(lang))
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
