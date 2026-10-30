package com.atom.tecnohandapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atom.tecnohandapp.FullSettingsDialog
import com.atom.tecnohandapp.HistoryDialog
import com.atom.tecnohandapp.R
import com.atom.tecnohandapp.ScanQrScreen
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppTheme
import com.atom.tecnohandapp.ui.theme.TecnoHandAppTheme

private val AppBgColor = Color(0xFFF8FAFC)
private val DarkTextColor = Color(0xFF0A1838)
private val ReticleBlueColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)
private val IconBgColor = Color(0xFFF1F5F9)

sealed class NavTab(val index: Int, val icon: ImageVector) {
    object ScanQr : NavTab(0, Icons.Default.QrCodeScanner)
    object Reports : NavTab(1, Icons.AutoMirrored.Filled.Assignment)
    object Alerts : NavTab(2, Icons.Default.NotificationsActive)
}

@Composable
fun MainContainerScreen(
    scanQrViewModel: ScanQrViewModel = viewModel(),
    reportsViewModel: ReportsViewModel = viewModel(),
    alertsViewModel: AlertsViewModel = viewModel()
) {
    val scanUiState by scanQrViewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(NavTab.ScanQr, NavTab.Reports, NavTab.Alerts)
    val lang = scanUiState.selectedLanguage

    val isDark = when (scanUiState.selectedTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    TecnoHandAppTheme(darkTheme = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF121826) else AppBgColor)
                .statusBarsPadding()
        ) {
            // --- Minimalist Material Top Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tecnohand_logo),
                    contentDescription = "Logo TecnoHand",
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TecnoHand",
                        color = if (isDark) Color.White else DarkTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (selectedTabIndex) {
                            0 -> StringsHelper.headerSubQr(lang)
                            1 -> StringsHelper.headerSubReports(lang)
                            else -> StringsHelper.headerSubAlerts(lang)
                        },
                        color = if (isDark) Color.LightGray else SubtitleGrayColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Discreet Quick Action Buttons (Historial & Servidor/Ajustes) - Active on ALL TABS!
                IconButton(
                    onClick = { scanQrViewModel.loadAccessHistory() },
                    modifier = Modifier
                        .background(if (isDark) Color(0xFF1E293B) else IconBgColor, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Historial",
                        tint = if (isDark) Color.White else DarkTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { scanQrViewModel.setShowServerConfigDialog(true) },
                    modifier = Modifier
                        .background(if (isDark) Color(0xFF1E293B) else IconBgColor, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes y Configuración",
                        tint = if (isDark) Color.White else DarkTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // --- Screen Body Container ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = if (isDark) Color(0xFF121826) else AppBgColor
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedTabIndex) {
                        0 -> ScanQrScreen(viewModel = scanQrViewModel)
                        1 -> ReportsAdminScreen(viewModel = reportsViewModel, lang = lang)
                        2 -> AlertsNotificationsScreen(viewModel = alertsViewModel, lang = lang)
                    }
                }
            }

            // --- Minimalist Bottom Navigation Bar ---
            NavigationBar(
                containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTabIndex == tab.index
                    val tabTitle = when (tab) {
                        NavTab.ScanQr -> StringsHelper.tabQr(lang)
                        NavTab.Reports -> StringsHelper.tabReports(lang)
                        NavTab.Alerts -> StringsHelper.tabAlerts(lang)
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = tab.index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tabTitle,
                                tint = if (isSelected) ReticleBlueColor else SubtitleGrayColor
                            )
                        },
                        label = {
                            Text(
                                text = tabTitle,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ReticleBlueColor else SubtitleGrayColor
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = ReticleBlueColor.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }

        // --- Global Settings & History Dialogs (Rendered at Top Level for All Tabs!) ---
        if (scanUiState.showServerConfigDialog) {
            FullSettingsDialog(
                uiState = scanUiState,
                onDismiss = { scanQrViewModel.setShowServerConfigDialog(false) },
                onSaveUrl = { newUrl -> scanQrViewModel.updateServerUrl(newUrl) },
                onSelectTheme = { theme -> scanQrViewModel.setAppTheme(theme) },
                onSelectLanguage = { l -> scanQrViewModel.setAppLanguage(l) },
                onToggleOfflineDemo = { enabled -> scanQrViewModel.setOfflineDemoMode(enabled) },
                onCheckUpdate = { scanQrViewModel.checkVersionUpdates() },
                onClearUpdateMsg = { scanQrViewModel.clearUpdateCheckResult() }
            )
        }

        if (scanUiState.showHistoryDialog) {
            HistoryDialog(
                logs = scanUiState.historyLogs,
                isLoading = scanUiState.isHistoryLoading,
                onDismiss = { scanQrViewModel.setShowHistoryDialog(false) }
            )
        }
    }
}
