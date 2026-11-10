package com.example.ui.admin

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminLogEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.example.ui.AdminTab
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.theme.ElectricPurple
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
fun AdminOverviewTab(
    players: List<UserEntity>,
    tournaments: List<TournamentEntity>,
    payments: List<PaymentEntity>,
    adminLogs: List<AdminLogEntity>,
    onNavigateTab: (AdminTab) -> Unit
) {
    // 12 Live KPIs
    val totalPlayers = players.size
    val activePlayers = players.count { !it.isBlocked && !it.isDeactivated }
    val blockedPlayers = players.count { it.isBlocked }
    val totalTournaments = tournaments.size
    val upcomingMatches = tournaments.count { it.status == "UPCOMING" }
    val liveMatches = tournaments.count { it.status == "ONGOING" }
    val completedMatches = tournaments.count { it.status == "COMPLETED" }
    val totalRevenue = payments.filter { it.status == "APPROVED" && it.coinsRequested > 0 }.sumOf { it.amountPaid }
    val totalPrizeDistributed = tournaments.filter { it.status == "COMPLETED" }.sumOf { it.prizePool }
    val pendingDeposits = payments.count { it.status == "PENDING" && it.coinsRequested > 0 }
    val pendingWithdrawals = payments.count { it.status == "PENDING" && it.coinsRequested < 0 }
    val totalCoinsCirculation = players.sumOf { it.coins }

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "OPERATIONAL DASHBOARD",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NeonCyan
            )
            Text(
                text = "12 Real-Time Key Performance Indicators (KPIs) & Audit Logs",
                fontFamily = RajdhaniFontFamily,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: Players & Revenue
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("TOTAL PLAYERS", "$totalPlayers", NeonCyan, Icons.Default.People, Modifier.weight(1f)) { onNavigateTab(AdminTab.PLAYERS) }
                KpiCard("ACTIVE PLAYERS", "$activePlayers", NeonGreen, Icons.Default.CheckCircle, Modifier.weight(1f)) { onNavigateTab(AdminTab.PLAYERS) }
                KpiCard("BLOCKED", "$blockedPlayers", NeonRed, Icons.Default.Block, Modifier.weight(1f)) { onNavigateTab(AdminTab.PLAYERS) }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Tournaments & Matches
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("TOURNAMENTS", "$totalTournaments", ElectricPurple, Icons.Default.SportsEsports, Modifier.weight(1f)) { onNavigateTab(AdminTab.TOURNAMENTS) }
                KpiCard("UPCOMING", "$upcomingMatches", NeonBlue, Icons.Default.Schedule, Modifier.weight(1f)) { onNavigateTab(AdminTab.MATCHES) }
                KpiCard("LIVE NOW", "$liveMatches", GoldYellow, Icons.Default.PlayArrow, Modifier.weight(1f)) { onNavigateTab(AdminTab.MATCHES) }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Row 3: Completed & Finance
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("COMPLETED", "$completedMatches", NeonGreen, Icons.Default.EmojiEvents, Modifier.weight(1f)) { onNavigateTab(AdminTab.RESULTS) }
                KpiCard("REVENUE (PKR)", "₨${String.format(Locale.US, "%.0f", totalRevenue)}", NeonCyan, Icons.Default.MonetizationOn, Modifier.weight(1f)) { onNavigateTab(AdminTab.PAYMENTS) }
                KpiCard("PRIZE MONEY", "$totalPrizeDistributed C", GoldYellow, Icons.Default.EmojiEvents, Modifier.weight(1f)) { onNavigateTab(AdminTab.RESULTS) }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Row 4: Pending Approvals & Coins
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("PENDING DEPOSITS", "$pendingDeposits", if (pendingDeposits > 0) NeonRed else NeonGreen, Icons.Default.AccountBalanceWallet, Modifier.weight(1f)) { onNavigateTab(AdminTab.PAYMENTS) }
                KpiCard("PENDING WITHDRAWALS", "$pendingWithdrawals", if (pendingWithdrawals > 0) GoldYellow else NeonGreen, Icons.Default.AccountBalanceWallet, Modifier.weight(1f)) { onNavigateTab(AdminTab.PAYMENTS) }
                KpiCard("COINS CIRCULATION", "$totalCoinsCirculation", NeonCyan, Icons.Default.MonetizationOn, Modifier.weight(1f)) { onNavigateTab(AdminTab.LEADERBOARD) }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ADMIN SHORTCUTS",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CyberButton(
                    text = "CREATE MATCH",
                    onClick = { onNavigateTab(AdminTab.TOURNAMENTS) },
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                )
                CyberButton(
                    text = "ADMIN ROOM (SEC 19)",
                    onClick = { onNavigateTab(AdminTab.ADMIN_ROOM) },
                    icon = Icons.Default.AdminPanelSettings,
                    containerColor = ElectricPurple,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CyberOutlinedButton(
                    text = "PAYMENTS (${pendingDeposits + pendingWithdrawals})",
                    onClick = { onNavigateTab(AdminTab.PAYMENTS) },
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f)
                )
                CyberOutlinedButton(
                    text = "TEAM ROSTERS",
                    onClick = { onNavigateTab(AdminTab.TEAMS) },
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RECENT ADMIN ACTIVITY LOGS",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
            Text(
                text = "Real-time audit record of system operations",
                fontFamily = RajdhaniFontFamily,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (adminLogs.isEmpty()) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No recent activity recorded yet.",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(adminLogs.take(15)) { log ->
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    borderColor = NeonCyan.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.action,
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GoldYellow
                            )
                            Text(
                                text = dateFormat.format(Date(log.timestamp)),
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = log.details,
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Admin: ${log.adminName}",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CyberCard(
        modifier = modifier.clickable { onClick() },
        borderColor = color.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
            Text(label, fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
        }
    }
}
