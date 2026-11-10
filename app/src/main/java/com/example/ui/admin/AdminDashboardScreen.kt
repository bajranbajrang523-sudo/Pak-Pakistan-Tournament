package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SliderImageEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.example.ui.AdminTab
import com.example.ui.BattleViewModel
import com.example.ui.PlayerTab
import com.example.ui.Screen
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.StatusBadge
import com.example.ui.screens.cyberTextFieldColors
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.EsportsSurface
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: BattleViewModel,
    initialTab: AdminTab = AdminTab.OVERVIEW,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    val isSuperAdmin = currentUser?.role in listOf("ADMIN", "SUPER_ADMIN", "MAIN_ADMIN")
    val isMatchAdmin = currentUser?.role == "MATCH_ADMIN"
    val isPlayerAdmin = currentUser?.role == "PLAYER_ADMIN"
    val isPaymentAdmin = currentUser?.role == "PAYMENT_ADMIN"
    val isAdmin = isSuperAdmin || isMatchAdmin || isPlayerAdmin || isPaymentAdmin

    // Security Gate: Ensure only verified ADMIN can view this screen
    if (currentUser != null && !isAdmin) {
        viewModel.navigateTo(Screen.PlayerDashboard())
        return
    }

    var selectedTab by remember { mutableStateOf(initialTab) }
    val players by viewModel.allPlayers.collectAsState()
    val tournaments by viewModel.allTournaments.collectAsState()
    val payments by viewModel.allPayments.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val sliderImages by viewModel.sliderImages.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val admins by viewModel.allAdmins.collectAsState()
    val adminLogs by viewModel.adminLogs.collectAsState()
    val teams by viewModel.allTeams.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PAK PAKISTAN TOURNAMENT",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeonCyan
                        )
                        Text(
                            text = "${currentUser?.fullName?.uppercase() ?: "ADMIN"} (${currentUser?.role?.replace("_", " ") ?: "SUPER ADMIN"})",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GoldYellow
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.PlayerDashboard(PlayerTab.HOME)) },
                        modifier = Modifier.testTag("admin_preview_player_btn")
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = "Preview Player View", tint = NeonCyan)
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = NeonRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EsportsSurface)
            )
        },
        containerColor = EsportsBackground,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Admin Navigation Tabs based on role & permissions
            val tabs = when {
                isMatchAdmin -> listOf(
                    AdminTab.OVERVIEW to "Overview",
                    AdminTab.TOURNAMENTS to "Tournaments",
                    AdminTab.MATCHES to "Matches & Rooms",
                    AdminTab.RESULTS to "Results",
                    AdminTab.TEAMS to "Squads"
                )
                isPlayerAdmin -> listOf(
                    AdminTab.OVERVIEW to "Overview",
                    AdminTab.PLAYERS to "Players",
                    AdminTab.TEAMS to "Squads",
                    AdminTab.CHAT to "Chat Mod"
                )
                isPaymentAdmin -> listOf(
                    AdminTab.OVERVIEW to "Overview",
                    AdminTab.PAYMENTS to "Payments",
                    AdminTab.REPORTS to "Reports"
                )
                else -> listOf(
                    AdminTab.OVERVIEW to "Overview",
                    AdminTab.PLAYERS to "Players",
                    AdminTab.TOURNAMENTS to "Tournaments",
                    AdminTab.MATCHES to "Matches & Rooms",
                    AdminTab.PAYMENTS to "Payments",
                    AdminTab.RESULTS to "Results",
                    AdminTab.ADMIN_ROOM to "Admin Room (Sec 19)",
                    AdminTab.TEAMS to "Squads",
                    AdminTab.ANNOUNCEMENTS to "Announce",
                    AdminTab.CHAT to "Chat Mod",
                    AdminTab.SLIDERS to "Banners",
                    AdminTab.REPORTS to "Reports",
                    AdminTab.SETTINGS to "Settings"
                )
            }

            ScrollableTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0),
                containerColor = EsportsSurface,
                contentColor = NeonCyan,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    val index = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = NeonCyan
                    )
                }
            ) {
                tabs.forEach { (tab, label) ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = label,
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                AdminTab.OVERVIEW -> AdminOverviewTab(
                    players = players,
                    tournaments = tournaments,
                    payments = payments,
                    adminLogs = adminLogs,
                    onNavigateTab = { selectedTab = it }
                )
                AdminTab.PLAYERS -> AdminPlayersTab(viewModel = viewModel, players = players)
                AdminTab.TOURNAMENTS -> AdminTournamentsTab(viewModel = viewModel, tournaments = tournaments)
                AdminTab.MATCHES -> AdminMatchesResultsTab(viewModel = viewModel, tournaments = tournaments)
                AdminTab.PAYMENTS -> AdminPaymentsTab(viewModel = viewModel, payments = payments)
                AdminTab.RESULTS -> AdminMatchesResultsTab(viewModel = viewModel, tournaments = tournaments)
                AdminTab.ADMIN_ROOM -> AdminRoomTab(viewModel = viewModel, admins = admins, adminLogs = adminLogs, currentUser = currentUser)
                AdminTab.TEAMS -> AdminTeamsTab(viewModel = viewModel, teams = teams)
                AdminTab.ANNOUNCEMENTS -> AdminAnnouncementsTab(viewModel = viewModel, announcements = announcements)
                AdminTab.CHAT -> AdminChatModerationTab(viewModel = viewModel, messages = chatMessages)
                AdminTab.SLIDERS -> AdminSlidersTab(viewModel = viewModel, sliders = sliderImages)
                AdminTab.REPORTS -> AdminReportsTab(players = players, tournaments = tournaments, payments = payments)
                AdminTab.SETTINGS -> AdminSettingsTab(viewModel = viewModel, settings = appSettings)
                else -> AdminOverviewTab(players, tournaments, payments, adminLogs) { selectedTab = it }
            }
        }
    }
}

// 2. Admin Players Tab
@Composable
fun AdminPlayersTab(viewModel: BattleViewModel, players: List<UserEntity>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlayerToEditCoins by remember { mutableStateOf<UserEntity?>(null) }
    var selectedPlayerToEditProfile by remember { mutableStateOf<UserEntity?>(null) }
    var playerToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val filtered = players.filter {
        searchQuery.isBlank() ||
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.ffName.contains(searchQuery, ignoreCase = true) ||
                it.ffUid.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Name, FF Name, UID, Email") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = cyberTextFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (filtered.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No players found.", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                }
            }
        } else {
            items(filtered) { player ->
                AdminPlayerCard(
                    player = player,
                    onToggleBlock = { viewModel.adminToggleUserBlock(player.id, player.isBlocked) },
                    onToggleChatBlock = { viewModel.adminToggleUserChatBlock(player.id, player.isChatBlocked) },
                    onToggleWalletFreeze = { viewModel.adminToggleWalletFreeze(player.id, player.isWalletFrozen) },
                    onToggleUidVerify = { viewModel.adminToggleUidVerification(player.id, player.isUidVerified) },
                    onAdjustCoins = { selectedPlayerToEditCoins = player },
                    onEditProfile = { selectedPlayerToEditProfile = player },
                    onDelete = { playerToDelete = player }
                )
            }
        }
    }

    if (selectedPlayerToEditCoins != null) {
        AdjustCoinsDialog(
            player = selectedPlayerToEditCoins!!,
            onDismiss = { selectedPlayerToEditCoins = null },
            onConfirm = { delta, reason ->
                viewModel.adminAdjustCoins(selectedPlayerToEditCoins!!.id, delta, reason)
                selectedPlayerToEditCoins = null
            }
        )
    }

    if (selectedPlayerToEditProfile != null) {
        AdminEditProfileDialog(
            player = selectedPlayerToEditProfile!!,
            onDismiss = { selectedPlayerToEditProfile = null },
            onConfirm = { fullName, ffName, ffUid ->
                viewModel.adminEditPlayerProfile(selectedPlayerToEditProfile!!.id, fullName, ffName, ffUid)
                selectedPlayerToEditProfile = null
            }
        )
    }

    if (playerToDelete != null) {
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = {
                Text(
                    text = "DELETE PLAYER RECORD",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonRed
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete '${playerToDelete?.fullName}' (${playerToDelete?.email})? All stats and registrations will be removed.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                CyberButton(
                    text = "DELETE PLAYER",
                    containerColor = NeonRed,
                    onClick = {
                        viewModel.adminDeletePlayer(playerToDelete!!.id)
                        playerToDelete = null
                    }
                )
            },
            dismissButton = {
                CyberOutlinedButton(text = "CANCEL", onClick = { playerToDelete = null })
            },
            containerColor = EsportsSurfaceVariant
        )
    }
}

@Composable
fun AdminPlayerCard(
    player: UserEntity,
    onToggleBlock: () -> Unit,
    onToggleChatBlock: () -> Unit,
    onToggleWalletFreeze: () -> Unit,
    onToggleUidVerify: () -> Unit,
    onAdjustCoins: () -> Unit,
    onEditProfile: () -> Unit,
    onDelete: () -> Unit
) {
    CyberCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        borderColor = if (player.isBlocked) NeonRed else if (player.isWalletFrozen) GoldYellow else EsportsCardBorder
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(player.fullName, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        if (player.isUidVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "UID Verified", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text("FF: ${player.ffName} | UID: ${player.ffUid}", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                    Text("Email: ${player.email}", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${player.coins} Coins", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldYellow)
                    Text("Wins: ${player.matchesWon} | Kills: ${player.totalKills}", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (player.isBlocked) "Blocked" else "Active", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = if (player.isBlocked) NeonRed else NeonGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = !player.isBlocked,
                        onCheckedChange = { onToggleBlock() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonGreen, uncheckedThumbColor = NeonRed)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (player.isWalletFrozen) "Wallet Frozen" else "Wallet Active", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = if (player.isWalletFrozen) GoldYellow else NeonGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = !player.isWalletFrozen,
                        onCheckedChange = { onToggleWalletFreeze() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonGreen, uncheckedThumbColor = GoldYellow)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CyberOutlinedButton(
                    text = "EDIT",
                    onClick = onEditProfile,
                    borderColor = NeonCyan,
                    textColor = NeonCyan,
                    modifier = Modifier.weight(1f).height(34.dp)
                )
                CyberOutlinedButton(
                    text = "± COINS",
                    onClick = onAdjustCoins,
                    borderColor = GoldYellow,
                    textColor = GoldYellow,
                    modifier = Modifier.weight(1f).height(34.dp)
                )
                CyberOutlinedButton(
                    text = if (player.isUidVerified) "UNVERIFY" else "VERIFY UID",
                    onClick = onToggleUidVerify,
                    borderColor = NeonGreen,
                    textColor = NeonGreen,
                    modifier = Modifier.weight(1.2f).height(34.dp)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Player", tint = NeonRed)
                }
            }
        }
    }
}

@Composable
fun AdminEditProfileDialog(
    player: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (fullName: String, ffName: String, ffUid: String) -> Unit
) {
    var name by remember { mutableStateOf(player.fullName) }
    var ff by remember { mutableStateOf(player.ffName) }
    var uid by remember { mutableStateOf(player.ffUid) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = {
            Text("ADMIN EDIT PLAYER PROFILE", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NeonCyan)
        },
        text = {
            Column {
                Text("Editing Account for ${player.email}", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = ff,
                    onValueChange = { ff = it },
                    label = { Text("Free Fire Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = uid,
                    onValueChange = { uid = it },
                    label = { Text("Free Fire UID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "SAVE CHANGES",
                onClick = {
                    if (name.isNotBlank() && ff.isNotBlank() && uid.isNotBlank()) {
                        onConfirm(name, ff, uid)
                    }
                }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        }
    )
}

@Composable
fun AdjustCoinsDialog(
    player: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (delta: Int, reason: String) -> Unit
) {
    var amountText by remember { mutableStateOf("100") }
    var reasonText by remember { mutableStateOf("") }
    var isAdd by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = {
            Text("ADJUST PLAYER COINS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GoldYellow)
        },
        text = {
            Column {
                Text("Player: ${player.fullName} (${player.ffName})", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Current Coins: ${player.coins}", fontFamily = OrbitronFontFamily, color = GoldYellow)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = isAdd,
                        onClick = { isAdd = true },
                        label = { Text("+ ADD COINS") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = !isAdd,
                        onClick = { isAdd = false },
                        label = { Text("- DEDUCT") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonRed, selectedLabelColor = Color.White)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) amountText = it },
                    label = { Text("Coins Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Reason (e.g. Tournament reward, dispute resolution)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "APPLY",
                onClick = {
                    val amount = amountText.toIntOrNull() ?: 0
                    if (amount > 0) {
                        val delta = if (isAdd) amount else -amount
                        onConfirm(delta, reasonText)
                    }
                }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        }
    )
}

// 3. Admin Tournaments Tab
@Composable
fun AdminTournamentsTab(viewModel: BattleViewModel, tournaments: List<TournamentEntity>) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            CyberButton(
                text = "+ CREATE NEW TOURNAMENT",
                onClick = { showCreateDialog = true },
                icon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(tournaments) { tournament ->
            CyberCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                borderColor = EsportsCardBorder
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tournament.title, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        StatusBadge(status = tournament.status)
                    }
                    Text("Map: ${tournament.map} | ${tournament.matchType} | Time: ${tournament.matchTime}", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = NeonCyan)
                    Text("Entry: ${tournament.entryFee} | Prize: ${tournament.prizePool} | Per Kill: +${tournament.perKillReward} | Joined: ${tournament.joinedCount}/${tournament.maxPlayers}", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
                    if (tournament.roomId.isNotBlank()) {
                        Text("Room: ${tournament.roomId} | Pass: ${tournament.roomPassword}", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldYellow)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (tournament.status == "UPCOMING") {
                            CyberOutlinedButton(
                                text = "START MATCH",
                                onClick = { viewModel.adminUpdateTournamentStatus(tournament.id, "ONGOING") },
                                borderColor = NeonGreen,
                                textColor = NeonGreen,
                                modifier = Modifier.weight(1f).height(36.dp)
                            )
                        } else if (tournament.status == "ONGOING") {
                            CyberOutlinedButton(
                                text = "COMPLETE",
                                onClick = { viewModel.adminUpdateTournamentStatus(tournament.id, "COMPLETED") },
                                borderColor = GoldYellow,
                                textColor = GoldYellow,
                                modifier = Modifier.weight(1f).height(36.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.adminDeleteTournament(tournament.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRed)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTournamentDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, map, type, time, entry, prize, kill, max, desc ->
                viewModel.adminCreateTournament(title, map, type, time, entry, prize, kill, max, desc)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateTournamentDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, map: String, type: String, time: String, entry: Int, prize: Int, kill: Int, max: Int, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var map by remember { mutableStateOf("Bermuda") }
    var matchType by remember { mutableStateOf("Solo") }
    var matchTime by remember { mutableStateOf("Tonight 09:30 PM PKT") }
    var entryFee by remember { mutableStateOf("25") }
    var prizePool by remember { mutableStateOf("800") }
    var perKill by remember { mutableStateOf("10") }
    var maxPlayers by remember { mutableStateOf("48") }
    var desc by remember { mutableStateOf("Free Fire Battle Royale match. Room credentials released 15 mins before match start.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = {
            Text("CREATE NEW TOURNAMENT", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NeonCyan)
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Match Title (e.g. LAHORE RUSH #109)") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Map:", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Bermuda", "Purgatory", "Kalahari", "Alpine").forEach { m ->
                            FilterChip(selected = map == m, onClick = { map = m }, label = { Text(m, fontSize = 11.sp) })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Match Type:", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Solo", "Duo", "Squad").forEach { t ->
                            FilterChip(selected = matchType == t, onClick = { matchType = t }, label = { Text(t, fontSize = 11.sp) })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = matchTime, onValueChange = { matchTime = it }, label = { Text("Match Date & Time") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = entryFee, onValueChange = { entryFee = it }, label = { Text("Entry Coins") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), colors = cyberTextFieldColors())
                        OutlinedTextField(value = prizePool, onValueChange = { prizePool = it }, label = { Text("Prize Pool") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), colors = cyberTextFieldColors())
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = perKill, onValueChange = { perKill = it }, label = { Text("Per Kill Reward") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), colors = cyberTextFieldColors())
                        OutlinedTextField(value = maxPlayers, onValueChange = { maxPlayers = it }, label = { Text("Max Slots") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), colors = cyberTextFieldColors())
                    }
                }
            }
        },
        confirmButton = {
            CyberButton(
                text = "CREATE MATCH",
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(
                            title, map, matchType, matchTime,
                            entryFee.toIntOrNull() ?: 20,
                            prizePool.toIntOrNull() ?: 500,
                            perKill.toIntOrNull() ?: 5,
                            maxPlayers.toIntOrNull() ?: 48,
                            desc
                        )
                    }
                }
            )
        },
        dismissButton = { CyberOutlinedButton(text = "CANCEL", onClick = onDismiss) }
    )
}

// 4. Admin Payments Tab
@Composable
fun AdminPaymentsTab(viewModel: BattleViewModel, payments: List<PaymentEntity>) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedPaymentForReview by remember { mutableStateOf<PaymentEntity?>(null) }

    val filtered = payments.filter {
        if (selectedFilter == "ALL") true else it.status == selectedFilter
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ALL", "PENDING", "APPROVED", "REJECTED").forEach { st ->
                FilterChip(
                    selected = selectedFilter == st,
                    onClick = { selectedFilter = st },
                    label = { Text(st, fontFamily = OrbitronFontFamily, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No payment requests found.", fontFamily = RajdhaniFontFamily, color = TextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered) { payment ->
                    CyberCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColor = when (payment.status) {
                            "PENDING" -> GoldYellow
                            "APPROVED" -> NeonGreen
                            else -> NeonRed
                        }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (payment.coinsRequested < 0) "WITHDRAWAL" else "DEPOSIT",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (payment.coinsRequested < 0) GoldYellow else NeonGreen
                                )
                                StatusBadge(status = payment.status)
                            }

                            Text("Player: ${payment.userName} (${payment.userEmail})", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Method: ${payment.method} | Amount: ₨${payment.amountPaid} | Coins: ${payment.coinsRequested}", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = NeonCyan)
                            Text("TxID: ${payment.transactionId}", fontFamily = OrbitronFontFamily, fontSize = 11.sp, color = GoldYellow)
                            if (payment.receiptNote.isNotBlank()) {
                                Text("Note/Account: ${payment.receiptNote}", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = TextSecondary)
                            }

                            if (payment.status == "PENDING") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CyberButton(
                                        text = "APPROVE",
                                        onClick = { viewModel.adminReviewPayment(payment.id, true, "Approved by Admin") },
                                        containerColor = NeonGreen,
                                        contentColor = Color.Black,
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    )
                                    CyberOutlinedButton(
                                        text = "REJECT",
                                        onClick = { viewModel.adminReviewPayment(payment.id, false, "Rejected by Admin") },
                                        borderColor = NeonRed,
                                        textColor = NeonRed,
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. Admin Announcements Tab
@Composable
fun AdminAnnouncementsTab(viewModel: BattleViewModel, announcements: List<AnnouncementEntity>) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            CyberButton(
                text = "+ POST NEW ANNOUNCEMENT",
                onClick = { showCreateDialog = true },
                icon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(announcements) { ann ->
            CyberCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), borderColor = EsportsCardBorder) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ann.title, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                        IconButton(onClick = { viewModel.adminDeleteAnnouncement(ann.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRed)
                        }
                    }
                    Text(ann.message, fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = TextPrimary)
                }
            }
        }
    }

    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("NORMAL") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = EsportsSurface,
            title = { Text("BROADCAST ANNOUNCEMENT", fontFamily = OrbitronFontFamily, color = NeonCyan, fontSize = 15.sp) },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Announcement Body") }, modifier = Modifier.fillMaxWidth(), minLines = 3, colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = priority == "NORMAL", onClick = { priority = "NORMAL" }, label = { Text("NORMAL") })
                        FilterChip(selected = priority == "HIGH", onClick = { priority = "HIGH" }, label = { Text("HIGH PRIORITY") })
                    }
                }
            },
            confirmButton = {
                CyberButton(
                    text = "PUBLISH",
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            viewModel.adminCreateAnnouncement(title, message, "ALL", priority)
                            showCreateDialog = false
                        }
                    }
                )
            },
            dismissButton = { CyberOutlinedButton(text = "CANCEL", onClick = { showCreateDialog = false }) }
        )
    }
}

// 6. Admin Chat Moderation Tab
@Composable
fun AdminChatModerationTab(viewModel: BattleViewModel, messages: List<ChatMessageEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("GROUP CHAT MODERATION", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan)
            Text("Admin can remove inappropriate messages or block abusive players directly:", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(messages.reversed()) { msg ->
            CyberCard(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), borderColor = EsportsCardBorder) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${msg.senderName} (${msg.senderRole})", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NeonCyan)
                        Text(msg.message, fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = TextPrimary)
                    }
                    Row {
                        IconButton(onClick = { viewModel.adminDeleteChatMessage(msg.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Message", tint = NeonRed)
                        }
                    }
                }
            }
        }
    }
}

// 7. Admin Sliders Tab (Five Rotating Pictures Management)
@Composable
fun AdminSlidersTab(viewModel: BattleViewModel, sliders: List<SliderImageEntity>) {
    var selectedSliderToEdit by remember { mutableStateOf<SliderImageEntity?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("FIVE ROTATING PICTURES MANAGEMENT", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan)
            Text("Admin can change titles, subtitles, tags, and reorder the 5 home pictures:", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(sliders) { slider ->
            CyberCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), borderColor = EsportsCardBorder) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Picture ${slider.orderIndex + 1}: ${slider.title}", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                        Text(slider.subtitle, fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextPrimary)
                        Text("Tag: ${slider.tag} | Asset: ${slider.drawableResName}", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = GoldYellow)
                    }
                    IconButton(onClick = { selectedSliderToEdit = slider }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Slider", tint = NeonCyan)
                    }
                }
            }
        }
    }

    if (selectedSliderToEdit != null) {
        var title by remember { mutableStateOf(selectedSliderToEdit!!.title) }
        var subtitle by remember { mutableStateOf(selectedSliderToEdit!!.subtitle) }
        var tag by remember { mutableStateOf(selectedSliderToEdit!!.tag) }
        var orderText by remember { mutableStateOf("${selectedSliderToEdit!!.orderIndex}") }

        AlertDialog(
            onDismissRequest = { selectedSliderToEdit = null },
            containerColor = EsportsSurface,
            title = { Text("EDIT BANNER PICTURE", fontFamily = OrbitronFontFamily, color = NeonCyan, fontSize = 15.sp) },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Tag") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = orderText, onValueChange = { orderText = it }, label = { Text("Display Order (0-4)") }, modifier = Modifier.fillMaxWidth(), colors = cyberTextFieldColors())
                }
            },
            confirmButton = {
                CyberButton(
                    text = "SAVE",
                    onClick = {
                        val order = orderText.toIntOrNull() ?: selectedSliderToEdit!!.orderIndex
                        viewModel.adminUpdateSlider(
                            selectedSliderToEdit!!.copy(title = title, subtitle = subtitle, tag = tag, orderIndex = order)
                        )
                        selectedSliderToEdit = null
                    }
                )
            },
            dismissButton = { CyberOutlinedButton(text = "CANCEL", onClick = { selectedSliderToEdit = null }) }
        )
    }
}
