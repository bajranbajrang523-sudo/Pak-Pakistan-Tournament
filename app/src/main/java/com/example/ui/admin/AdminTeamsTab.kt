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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamEntity
import com.example.ui.BattleViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.StatusBadge
import com.example.ui.screens.cyberTextFieldColors
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminTeamsTab(
    viewModel: BattleViewModel,
    teams: List<TeamEntity>
) {
    var teamToEditCaptain by remember { mutableStateOf<TeamEntity?>(null) }
    var teamToDelete by remember { mutableStateOf<TeamEntity?>(null) }

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
                        text = "TEAM & SQUAD ROSTERS",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "Section 9: Manage Duo & Squad esports rosters, captains, and approvals",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = ElectricPurple,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (teams.isEmpty()) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "No squads or duo teams registered yet.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Players can form and register teams from the Player Dashboard Team Registration section.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = NeonCyan
                        )
                    }
                }
            }
        } else {
            items(teams) { team ->
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    borderColor = if (team.isApproved) NeonGreen.copy(alpha = 0.5f) else GoldYellow.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${team.teamName} [${team.teamTag}]",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldYellow
                            )
                            StatusBadge(
                                text = if (team.isApproved) "APPROVED" else "PENDING",
                                color = if (team.isApproved) NeonGreen else GoldYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Captain: ${team.captainName} (UID: ${team.captainUid})",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeonCyan
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Column {
                            if (team.player2Uid.isNotBlank()) {
                                Text(
                                    text = "• Player 2 UID: ${team.player2Uid}",
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            if (team.player3Uid.isNotBlank()) {
                                Text(
                                    text = "• Player 3 UID: ${team.player3Uid}",
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            if (team.player4Uid.isNotBlank()) {
                                Text(
                                    text = "• Player 4 UID: ${team.player4Uid}",
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Approval Status:",
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Switch(
                                    checked = team.isApproved,
                                    onCheckedChange = { approved ->
                                        viewModel.adminApproveTeam(team.id, approved)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonGreen,
                                        checkedTrackColor = NeonGreen.copy(alpha = 0.5f),
                                        uncheckedThumbColor = GoldYellow,
                                        uncheckedTrackColor = GoldYellow.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                CyberOutlinedButton(
                                    text = "CAPTAIN",
                                    onClick = { teamToEditCaptain = team },
                                    icon = Icons.Default.Edit,
                                    borderColor = NeonCyan,
                                    textColor = NeonCyan
                                )
                                CyberOutlinedButton(
                                    text = "DELETE",
                                    onClick = { teamToDelete = team },
                                    icon = Icons.Default.Delete,
                                    borderColor = NeonRed,
                                    textColor = NeonRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (teamToEditCaptain != null) {
        ChangeCaptainDialog(
            team = teamToEditCaptain!!,
            onDismiss = { teamToEditCaptain = null },
            onConfirm = { newName, newUid ->
                viewModel.adminUpdateTeamCaptain(teamToEditCaptain!!.id, teamToEditCaptain!!.captainId, newName, newUid)
                teamToEditCaptain = null
            }
        )
    }

    if (teamToDelete != null) {
        AlertDialog(
            onDismissRequest = { teamToDelete = null },
            title = {
                Text(
                    text = "DELETE TEAM ROSTER",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonRed
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove team '${teamToDelete?.teamName}'?",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                CyberButton(
                    text = "YES, DELETE",
                    containerColor = NeonRed,
                    onClick = {
                        viewModel.adminDeleteTeam(teamToDelete!!.id)
                        teamToDelete = null
                    }
                )
            },
            dismissButton = {
                CyberOutlinedButton(text = "CANCEL", onClick = { teamToDelete = null })
            },
            containerColor = EsportsSurfaceVariant
        )
    }
}

@Composable
fun ChangeCaptainDialog(
    team: TeamEntity,
    onDismiss: () -> Unit,
    onConfirm: (newName: String, newUid: String) -> Unit
) {
    var captainName by remember { mutableStateOf(team.captainName) }
    var captainUid by remember { mutableStateOf(team.captainUid) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CHANGE TEAM CAPTAIN",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Assign a new leader for '${team.teamName}'",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = captainName,
                    onValueChange = { captainName = it },
                    label = { Text("New Captain Free Fire Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
                OutlinedTextField(
                    value = captainUid,
                    onValueChange = { captainUid = it },
                    label = { Text("New Captain Free Fire UID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "UPDATE CAPTAIN",
                onClick = {
                    if (captainName.isNotBlank() && captainUid.isNotBlank()) {
                        onConfirm(captainName, captainUid)
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
