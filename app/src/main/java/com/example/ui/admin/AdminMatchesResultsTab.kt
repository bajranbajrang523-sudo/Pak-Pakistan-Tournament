package com.example.ui.admin

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RegistrationEntity
import com.example.data.model.TournamentEntity
import com.example.data.repository.MatchResultInput
import com.example.ui.BattleViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.StatusBadge
import com.example.ui.screens.cyberTextFieldColors
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminMatchesResultsTab(
    viewModel: BattleViewModel,
    tournaments: List<TournamentEntity>
) {
    var statusFilter by remember { mutableStateOf("ALL") }
    var matchForRoomDetails by remember { mutableStateOf<TournamentEntity?>(null) }
    var matchForResults by remember { mutableStateOf<TournamentEntity?>(null) }

    val filteredMatches = tournaments.filter {
        statusFilter == "ALL" || it.status.equals(statusFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MATCH ROOM & RESULTS",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "Control Room ID/Password, live match status, and automated prize payouts",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Filter row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "UPCOMING", "ONGOING", "COMPLETED").forEach { status ->
                    val isSelected = statusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { statusFilter = status },
                        label = {
                            Text(
                                text = status,
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            containerColor = EsportsSurfaceVariant
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (filteredMatches.isEmpty()) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No matches match current filter.",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(filteredMatches) { match ->
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    borderColor = when (match.status) {
                        "ONGOING" -> GoldYellow
                        "COMPLETED" -> NeonGreen
                        else -> NeonCyan
                    }.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = match.title,
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            StatusBadge(
                                text = match.status,
                                color = when (match.status) {
                                    "ONGOING" -> GoldYellow
                                    "COMPLETED" -> NeonGreen
                                    "CANCELLED" -> NeonRed
                                    else -> NeonCyan
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Map: ${match.map} • Type: ${match.matchType} • Time: ${match.matchTime}",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Prize Pool: ${match.prizePool} Coins • Per Kill: ${match.perKillReward} C • Slots: ${match.joinedCount}/${match.maxPlayers}",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = GoldYellow
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Room details row
                        if (match.roomId.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Room ID: ${match.roomId}",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NeonCyan
                                )
                                Text(
                                    text = "Password: ${match.roomPassword}",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ElectricPurple
                                )
                            }
                        } else {
                            Text(
                                text = "Room credentials not yet published",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        if (match.winnerName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🏆 Champion: ${match.winnerName}",
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GoldYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CyberOutlinedButton(
                                text = "ROOM ID/PASS",
                                onClick = { matchForRoomDetails = match },
                                icon = Icons.Default.Key,
                                borderColor = NeonCyan,
                                textColor = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )

                            if (match.status == "UPCOMING") {
                                CyberButton(
                                    text = "START MATCH",
                                    onClick = { viewModel.adminUpdateTournamentStatus(match.id, "ONGOING") },
                                    icon = Icons.Default.PlayArrow,
                                    containerColor = GoldYellow,
                                    contentColor = androidx.compose.ui.graphics.Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                            } else if (match.status == "ONGOING") {
                                CyberButton(
                                    text = "SUBMIT RESULTS",
                                    onClick = { matchForResults = match },
                                    icon = Icons.Default.EmojiEvents,
                                    containerColor = NeonGreen,
                                    contentColor = androidx.compose.ui.graphics.Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                CyberOutlinedButton(
                                    text = "VIEW RESULTS",
                                    onClick = { matchForResults = match },
                                    icon = Icons.Default.EmojiEvents,
                                    borderColor = GoldYellow,
                                    textColor = GoldYellow,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Set Room ID & Password
    if (matchForRoomDetails != null) {
        SetRoomDetailsDialog(
            tournament = matchForRoomDetails!!,
            onDismiss = { matchForRoomDetails = null },
            onConfirm = { roomId, pass ->
                viewModel.adminUpdateRoomDetails(matchForRoomDetails!!.id, roomId, pass)
                matchForRoomDetails = null
            }
        )
    }

    // Dialog: Submit Results & Auto-Award Coins
    if (matchForResults != null) {
        PublishMatchResultsDialog(
            viewModel = viewModel,
            tournament = matchForResults!!,
            onDismiss = { matchForResults = null },
            onPublish = { winner, results ->
                viewModel.adminPublishResults(matchForResults!!.id, winner, results)
                matchForResults = null
            }
        )
    }
}

@Composable
fun SetRoomDetailsDialog(
    tournament: TournamentEntity,
    onDismiss: () -> Unit,
    onConfirm: (roomId: String, pass: String) -> Unit
) {
    var roomId by remember { mutableStateOf(tournament.roomId) }
    var password by remember { mutableStateOf(tournament.roomPassword) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "SET FREE FIRE CUSTOM ROOM",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Room details will be instantly made available to all joined players for '${tournament.title}'.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = roomId,
                    onValueChange = { roomId = it },
                    label = { Text("Room ID (e.g. 981245)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Room Password (e.g. pak786)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "SAVE & RELEASE",
                onClick = {
                    if (roomId.isNotBlank() && password.isNotBlank()) {
                        onConfirm(roomId, password)
                    }
                }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        },
        containerColor = EsportsSurfaceVariant
    )
}

@Composable
fun PublishMatchResultsDialog(
    viewModel: BattleViewModel,
    tournament: TournamentEntity,
    onDismiss: () -> Unit,
    onPublish: (winnerName: String, results: List<MatchResultInput>) -> Unit
) {
    var registrations by remember { mutableStateOf<List<RegistrationEntity>>(emptyList()) }
    var winnerName by remember { mutableStateOf(tournament.winnerName) }
    var isFetching by remember { mutableStateOf(true) }

    // Map of regId to mutable input
    val killsMap = remember { mutableMapOf<Long, String>() }
    val ranksMap = remember { mutableMapOf<Long, String>() }
    val prizeCoinsMap = remember { mutableMapOf<Long, String>() }

    LaunchedEffect(tournament.id) {
        val list = viewModel.getTournamentRegistrations(tournament.id)
        registrations = list
        list.forEach { reg ->
            killsMap[reg.id] = reg.kills.toString()
            ranksMap[reg.id] = if (reg.rank > 0) reg.rank.toString() else "0"
            prizeCoinsMap[reg.id] = if (reg.rewardCoins > 0) reg.rewardCoins.toString() else "0"
        }
        if (winnerName.isBlank() && list.isNotEmpty()) {
            winnerName = list.first().ffName
        }
        isFetching = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "MATCH RESULTS & PRIZE PAYOUT",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GoldYellow
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Distribute coins and log statistics for ${tournament.title}",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = winnerName,
                    onValueChange = { winnerName = it },
                    label = { Text("Winning Champion / Squad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Player Scores (${registrations.size} Participants):",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NeonCyan
                )

                if (registrations.isEmpty()) {
                    Text(
                        text = "No players have registered for this tournament yet.",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(240.dp)) {
                        items(registrations) { reg ->
                            var killsStr by remember { mutableStateOf(killsMap[reg.id] ?: "0") }
                            var rankStr by remember { mutableStateOf(ranksMap[reg.id] ?: "0") }
                            var coinsStr by remember { mutableStateOf(prizeCoinsMap[reg.id] ?: "0") }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${reg.userName} (${reg.ffName} - ${reg.ffUid})",
                                    fontFamily = RajdhaniFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = rankStr,
                                        onValueChange = {
                                            rankStr = it
                                            ranksMap[reg.id] = it
                                        },
                                        label = { Text("Rank") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = cyberTextFieldColors()
                                    )
                                    OutlinedTextField(
                                        value = killsStr,
                                        onValueChange = {
                                            killsStr = it
                                            killsMap[reg.id] = it
                                        },
                                        label = { Text("Kills") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        colors = cyberTextFieldColors()
                                    )
                                    OutlinedTextField(
                                        value = coinsStr,
                                        onValueChange = {
                                            coinsStr = it
                                            prizeCoinsMap[reg.id] = it
                                        },
                                        label = { Text("Coins") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1.2f),
                                        colors = cyberTextFieldColors()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            CyberButton(
                text = "PUBLISH & PAY COINS",
                containerColor = NeonGreen,
                contentColor = androidx.compose.ui.graphics.Color.Black,
                onClick = {
                    val resultsList = registrations.map { reg ->
                        val k = killsMap[reg.id]?.toIntOrNull() ?: 0
                        val r = ranksMap[reg.id]?.toIntOrNull() ?: 0
                        val c = prizeCoinsMap[reg.id]?.toIntOrNull() ?: 0
                        MatchResultInput(
                            registrationId = reg.id,
                            userId = reg.userId,
                            kills = k,
                            rank = r,
                            rewardCoins = c
                        )
                    }
                    onPublish(winnerName, resultsList)
                }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        },
        containerColor = EsportsSurfaceVariant
    )
}
