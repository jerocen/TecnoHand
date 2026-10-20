package com.atom.tecnohandapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.atom.tecnohandapp.R
import com.atom.tecnohandapp.ScanQrScreen

private val NavyHeaderColor = Color(0xFF08142C)
private val ContainerBgColor = Color(0xFFFFFFFF)
private val ReticleBlueColor = Color(0xFF5A82ED)
private val SubtitleGrayColor = Color(0xFF64748B)

sealed class NavTab(val index: Int, val title: String, val icon: ImageVector) {
    object ScanQr : NavTab(0, "Acceso QR", Icons.Default.QrCodeScanner)
    object Reports : NavTab(1, "Reportes", Icons.AutoMirrored.Filled.Assignment)
    object Alerts : NavTab(2, "Alertas", Icons.Default.NotificationsActive)
}

@Composable
fun MainContainerScreen(
    scanQrViewModel: ScanQrViewModel = viewModel(),
    reportsViewModel: ReportsViewModel = viewModel(),
    alertsViewModel: AlertsViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(NavTab.ScanQr, NavTab.Reports, NavTab.Alerts)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyHeaderColor)
            .statusBarsPadding()
    ) {
        // --- Shared Unified Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tecnohand_logo),
                contentDescription = "Logo TecnoHand",
                modifier = Modifier.size(46.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TecnoHand",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (selectedTabIndex) {
                        0 -> "Módulo de Acceso y QR"
                        1 -> "Administración de Reportes"
                        else -> "Alertas y Notificaciones"
                    },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            // Quick Actions in Header
            if (selectedTabIndex == 0) {
                IconButton(
                    onClick = { scanQrViewModel.loadAccessHistory() },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Historial",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { scanQrViewModel.setShowServerConfigDialog(true) },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configurar Servidor",
                        tint = Color.White
                    )
                }
            }
        }

        // --- Main White Rounded Card Body Container ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = ContainerBgColor
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> ScanQrScreen(viewModel = scanQrViewModel)
                    1 -> ReportsAdminScreen(viewModel = reportsViewModel)
                    2 -> AlertsNotificationsScreen(viewModel = alertsViewModel)
                }
            }
        }

        // --- Bottom Navigation Bar ---
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTabIndex == tab.index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { selectedTabIndex = tab.index },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) ReticleBlueColor else SubtitleGrayColor
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
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
}
