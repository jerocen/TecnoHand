package com.atom.tecnohandapp.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AlertItem
import com.atom.tecnohandapp.data.models.AppLanguage

private val SubtitleGrayColor = Color(0xFF64748B)

private val AlertRed = Color(0xFFEF4444)
private val AlertYellow = Color(0xFFF59E0B)
private val AlertGreen = Color(0xFF10B981)

@Composable
fun AlertsNotificationsScreen(
    viewModel: AlertsViewModel = viewModel(),
    lang: AppLanguage = AppLanguage.SPANISH
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterOptions = listOf(
        StringsHelper.tabAll(lang),
        StringsHelper.tabCritical(lang),
        StringsHelper.tabInProgress(lang),
        StringsHelper.tabCompleted(lang)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = StringsHelper.alertsTitle(lang),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = StringsHelper.alertsSub(lang),
                    fontSize = 12.sp,
                    color = SubtitleGrayColor
                )
            }

            IconButton(onClick = { viewModel.loadAlerts() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refrescar",
                    tint = AlertRed
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Emergency Panic Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.08f)),
            border = BorderStroke(1.5.dp, AlertRed)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AlertRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = StringsHelper.panicCardTitle(lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AlertRed
                        )
                        Text(
                            text = StringsHelper.panicCardSub(lang),
                            fontSize = 11.sp,
                            color = SubtitleGrayColor
                        )
                    }
                }

                Button(
                    onClick = { viewModel.triggerPanicButton() },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(StringsHelper.panicBtnText(lang), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { filter ->
                val isSelected = uiState.selectedFilter == filter
                val chipColor = when {
                    filter == StringsHelper.tabCritical(lang) || filter == "Críticas" -> AlertRed
                    filter == StringsHelper.tabInProgress(lang) || filter == "En Proceso" -> AlertYellow
                    filter == StringsHelper.tabCompleted(lang) || filter == "Terminadas" -> AlertGreen
                    else -> Color(0xFF5A82ED)
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) chipColor else chipColor.copy(alpha = 0.12f),
                    modifier = Modifier.clickable { viewModel.setFilter(filter) }
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else chipColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notifications List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AlertRed)
            }
        } else if (uiState.filteredAlerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = StringsHelper.noAlertsMsg(lang, uiState.selectedFilter),
                    color = SubtitleGrayColor,
                    fontSize = 13.sp,
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
                items(uiState.filteredAlerts) { alert ->
                    AlertNotificationCard(
                        alert = alert,
                        lang = lang,
                        onResolve = { viewModel.markAlertAsResolved(alert.id) }
                    )
                }
            }
        }
    }

    // Success or Error Feedback Dialog
    if (uiState.panicTriggered || uiState.successMessage != null || uiState.errorMessage != null) {
        val isError = uiState.errorMessage != null
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = {
                Text(
                    text = if (isError) "Error Notification" else "Panic Alert Sent",
                    fontWeight = FontWeight.Bold,
                    color = if (isError) AlertRed else AlertGreen
                )
            },
            text = {
                Text(
                    text = uiState.errorMessage ?: uiState.successMessage ?: "Operación realizada",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearMessages() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isError) AlertRed else AlertGreen)
                ) {
                    Text(StringsHelper.understandBtn(lang), color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun AlertNotificationCard(
    alert: AlertItem,
    lang: AppLanguage,
    onResolve: () -> Unit
) {
    val (statusColor, icon) = when (alert.colorNivel) {
        "rojo" -> Pair(AlertRed, Icons.Default.Warning)
        "amarillo" -> Pair(AlertYellow, Icons.Default.NotificationsActive)
        else -> Pair(AlertGreen, Icons.Default.CheckCircle)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alert.tipoAlerta,
                        color = statusColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Estado: ${alert.estadoUi}",
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alert.mensaje,
                fontSize = 13.sp,
                color = SubtitleGrayColor
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${StringsHelper.reportedBy(lang)} ${alert.reportadoPor} | ${alert.fecha.take(16)}",
                    fontSize = 10.sp,
                    color = SubtitleGrayColor
                )

                if (alert.estadoUi != "Terminado") {
                    OutlinedButton(
                        onClick = onResolve,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AlertGreen)
                    ) {
                        Text(StringsHelper.resolveBtn(lang), fontSize = 11.sp, color = AlertGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
