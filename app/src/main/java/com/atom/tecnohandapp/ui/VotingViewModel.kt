package com.atom.tecnohandapp.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

data class VotingItem(
    val id: String,
    val titulo: String,
    val fechaCierre: LocalDateTime,
    val votos: Int = 0,
    val presupuestoAsignado: String
)

class VotingViewModel : ViewModel() {

    // Total de viviendas en la colonia para calcular el % de participación
    // (Con 10 viviendas, cada voto equivale a 10% para que notes el cambio al probar)
    val totalViviendas = 10

    private val _votingList = MutableStateFlow(
        listOf(
            VotingItem("VOT-1", "Barda Perimetral", LocalDateTime.now().plusSeconds(30), 0, "$552,000"),
            VotingItem("VOT-2", "Parque Comunitario", LocalDateTime.now().plusDays(2).plusHours(5), 0, "$408,000"),
            VotingItem("VOT-3", "Servicios Públicos", LocalDateTime.now().minusDays(1), 0, "$240,000")
        )
    )
    val votingList: StateFlow<List<VotingItem>> = _votingList.asStateFlow()

    private val _votedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val votedItemIds: StateFlow<Set<String>> = _votedItemIds.asStateFlow()

    fun registrarVoto(itemId: String) {
        if (_votedItemIds.value.contains(itemId)) return

        // 1. Bloquea la votación para este usuario
        _votedItemIds.update { it + itemId }

        // 2. Suma 1 voto a la opción elegida
        _votingList.update { listaActual ->
            listaActual.map { item ->
                if (item.id == itemId) {
                    item.copy(votos = item.votos + 1)
                } else {
                    item
                }
            }
        }
    }
}

