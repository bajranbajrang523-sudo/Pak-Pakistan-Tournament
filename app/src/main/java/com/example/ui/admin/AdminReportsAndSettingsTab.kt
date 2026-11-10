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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.AppSettingsEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.example.ui.BattleViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminReportsTab(
    players: List<UserEntity>,
    tournaments: List<TournamentEntity>,
    payments: List<PaymentEntity>
) {
    var showExportDialog by remember { mutableStateOf(false) }

    val totalApprovedDeposits = payments.filter { it.status == "APPROVED" && it.coinsRequested > 0 }.sumOf { it.amountPaid }
    val totalApprovedWithdrawals = payments.filter { it.status == "APPROVED" && it.coinsRequested < 0 }.sumOf { -it.amountPaid }
    val netCashflow = totalApprovedDeposits - totalApprovedWithdrawals
    val totalMatches = tournaments.size
    val completedMatches = tournaments.count { it.status == "COMPLETED" }
    val totalCoinsPrize = tournaments.filter { it.status == "COMPLETED" }.sumOf { it.prizePool }
    val totalPlayerCoins = players.sumOf { it.coins }

    val csvReportContent = buildString {
        appendLine("--- PAK PAKISTAN TOURNAMENT AUDIT REPORT ---")
        appendLine("Generated At: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
        appendLine("Super Admin: Abdullah (Pak Pakistan Official)")
        appendLine("Platform: Free Fire Pakistan Esports")
        appendLine("--------------------------------------------")
        appendLine("FINANCIAL METRICS:")
        appendLine("Total Approved Deposits: PKR ${String.format(Locale.US, "%.2f", totalApprovedDeposits)}")
        appendLine("Total Approved Withdrawals: PKR ${String.format(Locale.US, "%.2f", totalApprovedWithdrawals)}")
        appendLine("Net Revenue: PKR ${String.format(Locale.US, "%.2f", netCashflow)}")
        appendLine("Active Coin Reserve: $totalPlayerCoins Coins")
        appendLine("--------------------------------------------")
        appendLine("TOURNAMENT METRICS:")
        appendLine("Total Tournaments Created: $totalMatches")
        appendLine("Completed Tournaments: $completedMatches")
        appendLine("Total Prizes Awarded: $totalCoinsPrize Coins")
        appendLine("--------------------------------------------")
        appendLine("PLAYER METRICS:")
        appendLine("Total Registered Players: ${players.size}")
        appendLine("Active Players: ${players.count { !it.isBlocked && !it.isDeactivated }}")
        appendLine("Blocked Players: ${players.count { it.isBlocked }}")
        appendLine("--------------------------------------------")
        appendLine("TOP PLAYERS:")
        players.sortedByDescending { it.matchesWon }.take(5).forEachIndexed { i, p ->
            appendLine("${i + 1}. ${p.fullName} (${p.ffName}) - Wins: ${p.matchesWon}, Kills: ${p.totalKills}, Coins: ${p.coins}")
        }
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
                        text = "REPORTS & ANALYTICS",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "Section 10: Financial performance, match stats, and CSV audit export",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Financial Summary Card
            CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonCyan.copy(alpha = 0.5f)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("FINANCIAL HEALTH SUMMARY", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Gross Deposits: ₨${String.format(Locale.US, "%.0f", totalApprovedDeposits)}", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = NeonGreen)
                    Text("• Paid Withdrawals: ₨${String.format(Locale.US, "%.0f", totalApprovedWithdrawals)}", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = GoldYellow)
                    Text("• Net Retained Revenue: ₨${String.format(Locale.US, "%.0f", netCashflow)}", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan)
                    Text("• In-App Player Coins Circulation: $totalPlayerCoins Coins", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tournament Performance Card
            CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = ElectricPurple.copy(alpha = 0.5f)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("TOURNAMENTS & MATCH PERFORMANCE", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ElectricPurple)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Total Tournaments Hosted: $totalMatches", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = TextPrimary)
                    Text("• Matches Completed & Settled: $completedMatches", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = NeonGreen)
                    Text("• Total Coins Prize Distributed: $totalCoinsPrize Coins", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = GoldYellow)
                    Text("• Player Conversion: ${players.size} Registered Players", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CyberButton(
                text = "EXPORT AUDIT REPORT (CSV / SUMMARY)",
                onClick = { showExportDialog = true },
                icon = Icons.Default.Download,
                containerColor = GoldYellow,
                contentColor = androidx.compose.ui.graphics.Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "PAKISTAN ESPORTS AUDIT REPORT",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GoldYellow
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    item {
                        Text(
                            text = csvReportContent,
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            },
            confirmButton = {
                CyberButton(
                    text = "CLOSE REPORT",
                    onClick = { showExportDialog = false }
                )
            },
            containerColor = EsportsSurfaceVariant
        )
    }
}

@Composable
fun AdminSettingsTab(
    viewModel: BattleViewModel,
    settings: AppSettingsEntity?
) {
    val current = settings ?: AppSettingsEntity()

    var appName by remember { mutableStateOf(current.appName) }
    var easypaisaTitle by remember { mutableStateOf(current.easypaisaTitle) }
    var easypaisaNumber by remember { mutableStateOf(current.easypaisaNumber) }
    var jazzcashTitle by remember { mutableStateOf(current.jazzcashTitle) }
    var jazzcashNumber by remember { mutableStateOf(current.jazzcashNumber) }
    var paytmTitle by remember { mutableStateOf(current.paytmTitle) }
    var paytmNumber by remember { mutableStateOf(current.paytmNumber) }
    var supportEmail by remember { mutableStateOf(current.supportEmail) }
    var supportWhatsapp by remember { mutableStateOf(current.supportWhatsapp) }
    var rulesText by remember { mutableStateOf(current.rulesText) }
    var isMaintenance by remember { mutableStateOf(current.isMaintenanceMode) }

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
                        text = "APP SETTINGS & CONTROLS",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "Section 11: Configure payment accounts, support channels, and rules",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Maintenance Mode Card
            CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = if (isMaintenance) NeonRed else NeonGreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isMaintenance) "MAINTENANCE MODE: ON" else "MAINTENANCE MODE: OFF",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isMaintenance) NeonRed else NeonGreen
                        )
                        Text(
                            text = "When ON, shows maintenance advisory to players",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = isMaintenance,
                        onCheckedChange = { isMaintenance = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonRed,
                            checkedTrackColor = NeonRed.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("App Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("EasyPaisa Official Account:", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, color = NeonGreen)
            OutlinedTextField(
                value = easypaisaTitle,
                onValueChange = { easypaisaTitle = it },
                label = { Text("EasyPaisa Account Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = easypaisaNumber,
                onValueChange = { easypaisaNumber = it },
                label = { Text("EasyPaisa Mobile Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("JazzCash Official Account:", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, color = GoldYellow)
            OutlinedTextField(
                value = jazzcashTitle,
                onValueChange = { jazzcashTitle = it },
                label = { Text("JazzCash Account Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = jazzcashNumber,
                onValueChange = { jazzcashNumber = it },
                label = { Text("JazzCash Mobile Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Paytm / UPI Account:", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, color = NeonBlue)
            OutlinedTextField(
                value = paytmTitle,
                onValueChange = { paytmTitle = it },
                label = { Text("Paytm / UPI Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = paytmNumber,
                onValueChange = { paytmNumber = it },
                label = { Text("Paytm Number / VPA") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Support Channels:", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, color = NeonCyan)
            OutlinedTextField(
                value = supportWhatsapp,
                onValueChange = { supportWhatsapp = it },
                label = { Text("Official Support WhatsApp") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = supportEmail,
                onValueChange = { supportEmail = it },
                label = { Text("Official Support Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Rules & Fair-Play Policy:", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, color = TextPrimary)
            OutlinedTextField(
                value = rulesText,
                onValueChange = { rulesText = it },
                label = { Text("Tournament Rules") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = cyberTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CyberButton(
                text = "SAVE ALL APP SETTINGS",
                onClick = {
                    viewModel.adminUpdateSettings(
                        current.copy(
                            appName = appName,
                            easypaisaTitle = easypaisaTitle,
                            easypaisaNumber = easypaisaNumber,
                            jazzcashTitle = jazzcashTitle,
                            jazzcashNumber = jazzcashNumber,
                            paytmTitle = paytmTitle,
                            paytmNumber = paytmNumber,
                            supportEmail = supportEmail,
                            supportWhatsapp = supportWhatsapp,
                            rulesText = rulesText,
                            isMaintenanceMode = isMaintenance
                        )
                    )
                },
                icon = Icons.Default.Save,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
