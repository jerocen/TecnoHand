package com.atom.tecnohandapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Refresh
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@Composable
fun VotingScreen(
    lang: AppLanguage = AppLanguage.SPANISH,
    isAdmin: Boolean = false,
    currentUserId: Long = 1L,
    votingViewModel: VotingViewModel = viewModel()
) {
    var mainTabSelected by remember { mutableIntStateOf(0) }
    var activeSubTab by remember { mutableStateOf("Activas") }
    var selectedItemForVote by remember { mutableStateOf<VotingItem?>(null) }
    var voteSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Estados exclusivos para Administrador
    var showAddPollDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<VotingItem?>(null) }

    val votingList by votingViewModel.votingList.collectAsState()
    val votedItemIds by votingViewModel.votedItemIds.collectAsState()
    //val totalViviendas = votingViewModel.totalViviendas

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = LocalDateTime.now()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
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

            IconButton(
                onClick = { votingViewModel.cargarEncuestas() },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refrescar",
                    tint = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (mainTabSelected) {
                0, 1 -> {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
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

                            if (isAdmin) {
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.clickable { showAddPollDialog = true }
                                ) {
                                    Text(
                                        text = if (StringsHelper.isEnglish(lang)) "+ New" else "+ Nueva",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        val filteredList = if (activeSubTab == "Activas") {
                            votingList.filter { currentTime.isBefore(it.fechaCierre) }
                        } else {
                            votingList.filter { currentTime.isAfter(it.fechaCierre) }
                        }

                        if (filteredList.isNotEmpty()) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredList, key = { it.id }) { item ->
                                    val isVoted = votedItemIds.contains("${currentUserId}_${item.id}")
                                    val porcentaje = (item.votos.toFloat() / item.totalViviendas.coerceAtLeast(1)).coerceIn(0f, 1f)
                                    VotingCardItem(
                                        item = item,
                                        porcentaje = porcentaje,
                                        lang = lang,
                                        currentTime = currentTime,
                                        isMyVote = isVoted,
                                        isAdmin = isAdmin,
                                        onVoteClick = { selectedItemForVote = item },
                                        onDeleteClick = { itemToDelete = item }
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (activeSubTab == "Activas") {
                                        if (StringsHelper.isEnglish(lang)) "No active polls at this moment." else "No hay votaciones activas en este momento."
                                    } else {
                                        if (StringsHelper.isEnglish(lang)) "No closed polls at this moment." else "No hay votaciones cerradas en este momento."
                                    },
                                    color = SubtitleTextBlue,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                2 -> {
                    val totalVotosEmitidos = votingList.sumOf { it.votos }
                    val totalPresupuesto = votingList.sumOf { extraerMontoPresupuesto(it.presupuestoAsignado) }
                    val formattedBudget = NumberFormat.getNumberInstance(Locale.US).format(totalPresupuesto.toLong())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (StringsHelper.isEnglish(lang)) "Total votes registered: $totalVotosEmitidos" else "Total de votos registrados: $totalVotosEmitidos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        votingList.forEach { item ->
                            val porcentaje = (item.votos.toFloat() / item.totalViviendas.coerceAtLeast(1)).coerceIn(0f, 1f)

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.titulo} (${item.votos})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${(porcentaje * 100).toInt()}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { porcentaje },
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
                                    text = "$$formattedBudget MXN",
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

    // Diálogo de confirmación de voto
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
                        onClick = { selectedItemForVote = null },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.2.dp, PrimaryDarkBlue)
                    ) {
                        Text(if (StringsHelper.isEnglish(lang)) "Cancel" else "Cancelar", color = PrimaryDarkBlue)
                    }

                    Button(
                        onClick = {
                            votingViewModel.registrarVoto(item.id, currentUserId)
                            voteSuccessMessage = "¡Voto registrado para '${item.titulo}'!"
                            selectedItemForVote = null
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue)
                    ) {
                        Text(if (StringsHelper.isEnglish(lang)) "Yes, confirm" else "Sí, confirmar", color = Color.White)
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Diálogo de voto exitoso
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

    // Diálogo para AGREGAR encuesta (Solo Admin)
    if (showAddPollDialog && isAdmin) {
        var nuevoTitulo by remember { mutableStateOf("") }
        var nuevaDesc by remember { mutableStateOf("") }
        var nuevoPresupuesto by remember { mutableStateOf("") }
        var duracionHoras by remember { mutableStateOf("24") }

        AlertDialog(
            onDismissRequest = { showAddPollDialog = false },
            title = {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "New Poll" else "Nueva Encuesta",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkBlue
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nuevoTitulo,
                        onValueChange = { nuevoTitulo = it },
                        label = { Text(if (StringsHelper.isEnglish(lang)) "Poll Title" else "Título de la encuesta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevaDesc,
                        onValueChange = { nuevaDesc = it },
                        label = { Text(if (StringsHelper.isEnglish(lang)) "Description" else "Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoPresupuesto,
                        onValueChange = { nuevoPresupuesto = it },
                        label = { Text(if (StringsHelper.isEnglish(lang)) "Budget (e.g. $150,000)" else "Presupuesto (ej. $150,000)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = duracionHoras,
                        onValueChange = { if (it.all { c -> c.isDigit() }) duracionHoras = it },
                        label = { Text(if (StringsHelper.isEnglish(lang)) "Duration (hours)" else "Duración (en horas)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        votingViewModel.agregarEncuesta(
                            titulo = nuevoTitulo,
                            descripcion = nuevaDesc,
                            presupuesto = nuevoPresupuesto,
                            duracionHoras = duracionHoras.toLongOrNull() ?: 24L
                        )
                        showAddPollDialog = false
                    },
                    enabled = nuevoTitulo.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue)
                ) {
                    Text(if (StringsHelper.isEnglish(lang)) "Create" else "Crear", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddPollDialog = false }) {
                    Text(if (StringsHelper.isEnglish(lang)) "Cancel" else "Cancelar", color = PrimaryDarkBlue)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Diálogo para ELIMINAR encuesta (Solo Admin)
    if (itemToDelete != null && isAdmin) {
        val target = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = if (StringsHelper.isEnglish(lang)) "Delete Poll" else "Eliminar Encuesta",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Text(
                    text = if (StringsHelper.isEnglish(lang))
                        "Are you sure you want to delete '${target.titulo}'? This action cannot be undone."
                    else
                        "¿Seguro que deseas eliminar '${target.titulo}'? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        votingViewModel.eliminarEncuesta(target.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(if (StringsHelper.isEnglish(lang)) "Delete" else "Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToDelete = null }) {
                    Text(if (StringsHelper.isEnglish(lang)) "Cancel" else "Cancelar", color = PrimaryDarkBlue)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun VotingCardItem(
    item: VotingItem,
    porcentaje: Float,
    lang: AppLanguage,
    currentTime: LocalDateTime,
    isMyVote: Boolean,
    isAdmin: Boolean,
    onVoteClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                    if (item.descripcion.isNotBlank()) {
                        Text(
                            text = item.descripcion,
                            fontSize = 12.sp,
                            color = PrimaryDarkBlue.copy(alpha = 0.75f)
                        )
                    }
                }

                if (isAdmin) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar encuesta",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            }

            Text(
                text = timeRemainingText,
                fontSize = 12.sp,
                color = if (isClosed || (duration.toHours() == 0L && duration.toMinutes() < 5)) Color.Red else SubtitleTextBlue,
                fontWeight = if (isClosed) FontWeight.Bold else FontWeight.Normal
            )


            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { porcentaje },
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
                    text = if (StringsHelper.isEnglish(lang)) "Turnout ${(porcentaje * 100).toInt()}% (${item.votos})" else "Participación ${(porcentaje * 100).toInt()}% (${item.votos})",
                    fontSize = 13.sp,
                    color = SubtitleTextBlue,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onVoteClick,
                    enabled = !isMyVote && !isClosed,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDarkBlue,
                        disabledContainerColor = if (isMyVote) ProgressBlue else Color.Gray.copy(alpha = 0.5f),
                        disabledContentColor = if (isMyVote) Color.White else Color.DarkGray
                    )
                ) {
                    Text(
                        text = when {
                            isMyVote -> if (StringsHelper.isEnglish(lang)) "VOTED" else "VOTADO"
                            isClosed -> if (StringsHelper.isEnglish(lang)) "CLOSED" else "CERRADA"
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