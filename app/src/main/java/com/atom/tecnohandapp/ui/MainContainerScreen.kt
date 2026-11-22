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
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
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
import androidx.compose.runtime.mutableStateOf
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
import com.atom.tecnohandapp.data.models.UserProfile
import com.atom.tecnohandapp.data.models.UserRole
import com.atom.tecnohandapp.ui.theme.TecnoHandAppTheme

private val AppBgColor = Color(0xFFF8FAFC)
private val DarkTextColor = Color(0xFF0A1838)
private val ReticleBlueColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)
private val IconBgColor = Color(0xFFF1F5F9)

sealed class RoleNavTab(val index: Int, val icon: ImageVector, val labelEs: String, val labelEn: String) {
    // Guard Tabs
    object ScanQr : RoleNavTab(0, Icons.Default.QrCodeScanner, "Acceso QR", "QR Access")
    object GuardReports : RoleNavTab(1, Icons.AutoMirrored.Filled.Assignment, "Reportes", "Reports")
    object GuardAlerts : RoleNavTab(2, Icons.Default.NotificationsActive, "Alertas", "Alertas")
    object GuardProfile : RoleNavTab(3, Icons.Default.Person, "Perfil", "Profile")

    // Resident Tabs
    object Voting : RoleNavTab(0, Icons.Default.HowToVote, "Votaciones", "Voting")
    object CreateQr : RoleNavTab(1, Icons.Default.QrCode, "Crear QR", "Guest QR")
    object ResidentReports : RoleNavTab(2, Icons.AutoMirrored.Filled.Assignment, "Reportes", "Reports")
    object ResidentAlerts : RoleNavTab(3, Icons.Default.NotificationsActive, "Alertas", "Alertas")
    object ResidentFamily : RoleNavTab(4, Icons.Default.FamilyRestroom, "Mi Familia", "My Family")
}

@Composable
fun MainContainerScreen(
    scanQrViewModel: ScanQrViewModel = viewModel(),
    reportsViewModel: ReportsViewModel = viewModel(),
    alertsViewModel: AlertsViewModel = viewModel(),
    votingViewModel: VotingViewModel = viewModel()
) {
    val scanUiState by scanQrViewModel.uiState.collectAsState()
    var currentUserProfile by remember { mutableStateOf<UserProfile?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val lang = scanUiState.selectedLanguage

    val isDark = when (scanUiState.selectedTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    TecnoHandAppTheme(darkTheme = isDark) {
        if (currentUserProfile == null) {
            // Show Login Screen
            LoginScreen(
                lang = lang,
                onLoginSuccess = { profile ->
                    currentUserProfile = profile
                    selectedTabIndex = 0
                }
            )
        } else {
            // Logged In Application Layout
            val profile = currentUserProfile!!
            val isGuard = profile.rol == UserRole.GUARD

            val navTabs: List<RoleNavTab> = if (isGuard) {
                listOf(RoleNavTab.ScanQr, RoleNavTab.GuardReports, RoleNavTab.GuardAlerts, RoleNavTab.GuardProfile)
            } else {
                listOf(RoleNavTab.Voting, RoleNavTab.CreateQr, RoleNavTab.ResidentReports, RoleNavTab.ResidentAlerts, RoleNavTab.ResidentFamily)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDark) Color(0xFF121826) else AppBgColor)
                    .statusBarsPadding()
            ) {
                // --- Top Header Bar ---
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
                            text = "${profile.nombre} (${profile.rol.name})",
                            color = if (isDark) Color.LightGray else SubtitleGrayColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // History Button
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

                    // Settings Button
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
                        if (isGuard) {
                            when (selectedTabIndex) {
                                0 -> ScanQrScreen(viewModel = scanQrViewModel)
                                1 -> ReportsAdminScreen(viewModel = reportsViewModel, lang = lang)
                                2 -> AlertsNotificationsScreen(viewModel = alertsViewModel, lang = lang)
                                3 -> FamilyProfileScreen(
                                    userProfile = profile,
                                    lang = lang,
                                    onUpdateProfile = { updated -> currentUserProfile = updated },
                                    onLogout = { currentUserProfile = null }
                                )
                            }
                        } else {
                            when (selectedTabIndex) {
                                0 -> VotingScreen(lang = lang, votingViewModel = votingViewModel)
                                1 -> GuestQrGeneratorScreen(userProfile = profile, lang = lang)
                                2 -> ReportsAdminScreen(viewModel = reportsViewModel, lang = lang)
                                3 -> AlertsNotificationsScreen(viewModel = alertsViewModel, lang = lang)
                                4 -> FamilyProfileScreen(
                                    userProfile = profile,
                                    lang = lang,
                                    onUpdateProfile = { updated -> currentUserProfile = updated },
                                    onLogout = { currentUserProfile = null }
                                )
                            }
                        }
                    }
                }

                // --- Bottom Role Navigation Bar ---
                NavigationBar(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    navTabs.forEach { tab ->
                        val isSelected = selectedTabIndex == tab.index
                        val tabTitle = if (StringsHelper.isEnglish(lang)) tab.labelEn else tab.labelEs

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
                                    fontSize = 10.sp,
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
        }

        // --- Global Settings & History Dialogs ---
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
