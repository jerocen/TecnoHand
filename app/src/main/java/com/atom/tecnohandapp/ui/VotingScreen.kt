package com.atom.tecnohandapp.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atom.tecnohandapp.data.local.StringsHelper
import com.atom.tecnohandapp.data.models.AppLanguage
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

private val PrimaryDarkBlue = Color(0xFF060060)
private val ProgressBlue = Color(0xFF5C5BD6)
private val CardBgGray = Color(0xFFC0C0C0)
private val LightTabGray = Color(0xFFC4C4C4)
private val SubtitleTextBlue = Color(0xFF6E78A0)

data class VotingItem(
    val id: String,
    val titulo: String,
    val fechaCierre: LocalDateTime,
    val porcentaje: Float,
    val presupuestoAsignado: String
)

@Composable
fun VotingScreen(
    lang: AppLanguage = AppLanguage.SPANISH
) {
    var mainTabSelected by remember { mutableIntStateOf(0) } // 0: Inicio / Activas, 1: Votar, 2: Resultados
    var activeSubTab by remember { mutableStateOf("Activas") } // "Activas", "Cerradas"
    var selectedItemForVote by remember { mutableStateOf<VotingItem?>(null) }
    var voteSuccessMessage by remember { mutableStateOf<String?>(null) }

    var votedItemId by remember { mutableStateOf<String?>(null) }
    val hasVoted = votedItemId != null

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = LocalDateTime.now()
        }
    }

    val votingList = listOf(
        VotingItem("VOT-1", "Barda Perimetral", LocalDateTime.now().plusSeconds(15), 0.62f, "$552,000"),
        VotingItem("VOT-2", "Parque Comunitario", LocalDateTime.now().plusDays(2).plusHours(5), 0.35f, "$408,000"),
        VotingItem("VOT-3", "Servicios Públicos", LocalDateTime.now().minusDays(1), 0.18f, "$240,000")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // --- Top Dark Blue Banner Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(PrimaryDarkBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (mainTabSelected) {
                    0 -> if (StringsHelper.isEnglish(lang)) "Active Voting" else "Votaciones Activas"
                    1 -> if (StringsHelper.isEnglish(lang)) "Cast Vote" else "Emitir Voto"
                    else -> if (StringsHelper.isEnglish(lang)) "RESULTS" else "RESULTADOS"
                },
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // --- Screen Content Container ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (mainTabSelected) {
                0, 1 -> {
                    // Active / Closed Voting View
                    Column {
                        // Sub Tabs (Activas / Cerradas)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            val isActive = activeSubTab == "Activas"
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isActive) PrimaryDarkBlue else LightTabGray,
                                modifier = Modifier.clickable { activeSubTab = "Activas" }
                            ) {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Active" else "Activas",
                                    color = if (isActive) Color.White else PrimaryDarkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
                                )
                            }

                            val isClosed = activeSubTab == "Cerradas"
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isClosed) PrimaryDarkBlue else LightTabGray,
                                modifier = Modifier.clickable { activeSubTab = "Cerradas" }
                            ) {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Closed" else "Cerradas",
                                    color = if (isClosed) Color.White else PrimaryDarkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
                                )
                            }
                        }

                        val filteredList = if (activeSubTab == "Activas") {
                            votingList.filter { currentTime.isBefore(it.fechaCierre) }
                        } else {
                            votingList.filter { currentTime.isAfter(it.fechaCierre) }
                        }

                        //activeSubTab == "Activas"
                        if(filteredList.isNotEmpty())
                        {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(votingList) { item ->
                                    VotingCardItem(
                                        item = item,
                                        lang = lang,
                                        currentTime = currentTime,
                                        hasVoted = hasVoted,
                                        isMyVote = votedItemId == item.id,
                                        onVoteClick = { selectedItemForVote = item }
                                    )
                                }
                            }
                        } else {
                            // Closed Voting Message
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "No closed polls at this moment." else "No hay votaciones cerradas en este momento.",
                                    color = SubtitleTextBlue,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Results View (Matching Screenshot 3)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (StringsHelper.isEnglish(lang)) "Closed Voting - 1,284 votes" else "Votación cerrada - 1,284 votos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        votingList.forEach { item ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.titulo,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${(item.porcentaje * 100).toInt()}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { item.porcentaje },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = ProgressBlue,
                                    trackColor = LightTabGray
                                )

                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Assigned budget: ${item.presupuestoAsignado}" else "Presupuesto asignado: ${item.presupuestoAsignado}",
                                    fontSize = 13.sp,
                                    color = SubtitleTextBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Total Budget Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryDarkBlue)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = if (StringsHelper.isEnglish(lang)) "Total assigned budget" else "Presupuesto total asignado",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$1,200,000 MXN",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Bottom Sub-Navigation Bar (Inicio, Votar, Resultados) ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Option 1: Inicio
                val isTab0 = mainTabSelected == 0
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { mainTabSelected = 0 }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isTab0) ProgressBlue else ProgressBlue.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = if (StringsHelper.isEnglish(lang)) "Home" else "Inicio",
                        fontSize = 12.sp,
                        fontWeight = if (isTab0) FontWeight.Bold else FontWeight.Medium,
                        color = ProgressBlue
                    )
                }

                // Option 2: Votar
                val isTab1 = mainTabSelected == 1
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { mainTabSelected = 1 }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isTab1) ProgressBlue else ProgressBlue.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.HowToVote, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = if (StringsHelper.isEnglish(lang)) "Vote" else "Votar",
                        fontSize = 12.sp,
                        fontWeight = if (isTab1) FontWeight.Bold else FontWeight.Medium,
                        color = ProgressBlue
                    )
                }

                // Option 3: Resultados
                val isTab2 = mainTabSelected == 2
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { mainTabSelected = 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isTab2) ProgressBlue else ProgressBlue.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Poll, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = if (StringsHelper.isEnglish(lang)) "Results" else "Resultados",
                        fontSize = 12.sp,
                        fontWeight = if (isTab2) FontWeight.Bold else FontWeight.Medium,
                        color = ProgressBlue
                    )
                }
            }
        }
    }

    // --- Confirm Vote Dialog (Matching Screenshot 2) ---
    if (selectedItemForVote != null) {
        val item = selectedItemForVote!!
        AlertDialog(
            onDismissRequest = { selectedItemForVote = null },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Help / Question Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SubtitleTextBlue.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = SubtitleTextBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (StringsHelper.isEnglish(lang)) "Are you sure?" else "¿Estás seguro?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (StringsHelper.isEnglish(lang)) "Your vote cannot be modified" else "Tu voto no podrá modificarse",
                        fontSize = 13.sp,
                        color = SubtitleTextBlue
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected Option Highlight Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardBgGray)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryDarkBlue,
                                modifier = Modifier.size(16.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = item.titulo,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkBlue
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            votedItemId = item.id
                            voteSuccessMessage = "¡Voto registrado para '${item.titulo}'!"
                            selectedItemForVote = null },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.2.dp, PrimaryDarkBlue)
                    ) {
                        Text(if (StringsHelper.isEnglish(lang)) "Cancel" else "Cancelar", color = PrimaryDarkBlue)
                    }

                    Button(
                        onClick = {
                            voteSuccessMessage = "¡Voto registrado para '${item.titulo}'!"
                            selectedItemForVote = null
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue)
                    ) {
                        Text(if (StringsHelper.isEnglish(lang)) "Yes, confirm" else "Si, confirmar", color = Color.White)
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (voteSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = { voteSuccessMessage = null },
            title = { Text("Votación Registrada", fontWeight = FontWeight.Bold, color = PrimaryDarkBlue) },
            text = { Text(voteSuccessMessage!!, fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { voteSuccessMessage = null }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue)) {
                    Text(StringsHelper.understandBtn(lang), color = Color.White)
                }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun VotingCardItem(
    item: VotingItem,
    lang: AppLanguage,
    currentTime: LocalDateTime,
    hasVoted: Boolean,
    isMyVote: Boolean,
    onVoteClick: () -> Unit
) {
    val isClosed = currentTime.isAfter(item.fechaCierre)
    val duration = Duration.between(currentTime, item.fechaCierre)

    val timeRemainingText = if (isClosed) {
        if (StringsHelper.isEnglish(lang)) "Poll Closed" else "Votación Cerrada"
    } else {
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60

        if (days > 0) {
            if (StringsHelper.isEnglish(lang)) "Closes in $days d $hours h" else "Cierra en $days d $hours h"
        } else {
            // Cuenta regresiva estilo cronómetro
            if (StringsHelper.isEnglish(lang)) "Closes in ${hours}h ${minutes}m ${seconds}s"
            else "Cierra en ${hours}h ${minutes}m ${seconds}s"
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkBlue
            )
            Text(
                text = timeRemainingText,
                fontSize = 12.sp,
                color = if (isClosed || (duration.toHours() == 0L && duration.toMinutes() < 5)) Color.Red else SubtitleTextBlue,
                fontWeight = if (isClosed) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { item.porcentaje },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = ProgressBlue,
                trackColor = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Turnout ${(item.porcentaje * 100).toInt()}%" else "Participación ${(item.porcentaje * 100).toInt()}%",
                    fontSize = 13.sp,
                    color = SubtitleTextBlue,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onVoteClick,
                    enabled = !hasVoted,
                    shape = RoundedCornerShape(14.dp),
                    //colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue)
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDarkBlue,
                        disabledContainerColor = if (isMyVote) ProgressBlue else Color.Gray.copy(alpha = 0.5f),
                        disabledContentColor = if (isMyVote) Color.White else Color.DarkGray
                    )
                ) {
                    Text(
                        //text = if (StringsHelper.isEnglish(lang)) "VOTE" else "VOTAR",
                        text = when {
                            isMyVote -> if (StringsHelper.isEnglish(lang)) "VOTED" else "VOTADO"
                            hasVoted -> if (StringsHelper.isEnglish(lang)) "LOCKED" else "BLOQUEADO"
                            else -> if (StringsHelper.isEnglish(lang)) "VOTE" else "VOTAR"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
