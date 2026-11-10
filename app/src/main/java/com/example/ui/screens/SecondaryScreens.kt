package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.UserEntity
import com.example.ui.BattleViewModel
import com.example.ui.PlayerTab
import com.example.ui.Screen
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsCard
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.GoldYellow
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
fun SimpleTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NeonCyan
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonCyan
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = EsportsSurface)
    )
}

// 1. Announcements Screen
@Composable
fun AnnouncementsScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val announcements by viewModel.announcements.collectAsState()

    Scaffold(
        topBar = { SimpleTopBar(title = "ANNOUNCEMENTS", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (announcements.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No announcements at this time.", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                    }
                }
            } else {
                items(announcements) { ann ->
                    AnnouncementCard(announcement = ann)
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: AnnouncementEntity) {
    val dateStr = remember(announcement.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(announcement.timestamp))
    }

    CyberCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        borderColor = if (announcement.priority == "HIGH") NeonRed else NeonCyan
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = announcement.title,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (announcement.priority == "HIGH") NeonRed else NeonCyan
                )
                StatusBadge(status = announcement.priority)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = announcement.message,
                fontFamily = RajdhaniFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateStr,
                fontFamily = RajdhaniFontFamily,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

// 2. Group Chat Screen
@Composable
fun GroupChatScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val messages by viewModel.chatMessages.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = { SimpleTopBar(title = "PLAYER GROUP CHAT", onBack = onBack) },
        containerColor = EsportsBackground,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EsportsSurface)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Type message to warriors...", fontFamily = RajdhaniFontFamily, color = TextSecondary) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    singleLine = true,
                    colors = cyberTextFieldColors()
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendChatMessage(inputMessage)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF03141A)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (currentUser?.isChatBlocked == true) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3B0B14))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your account is currently restricted from group chat.",
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = NeonRed,
                        fontSize = 12.sp
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == currentUser?.id
                    val isAdmin = msg.senderRole == "ADMIN"
                    ChatMessageBubble(message = msg, isMe = isMe, isAdmin = isAdmin)
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity, isMe: Boolean, isAdmin: Boolean) {
    val timeStr = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = message.senderName,
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isAdmin) GoldYellow else if (isMe) NeonCyan else TextSecondary
            )
            if (isAdmin) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(GoldYellow)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text("ADMIN", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 8.sp, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(timeStr, fontFamily = RajdhaniFontFamily, fontSize = 10.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isAdmin) Color(0xFF281C06) else if (isMe) Color(0xFF132B45) else EsportsCard
                )
                .border(
                    BorderStroke(
                        1.dp,
                        if (isAdmin) GoldYellow else if (isMe) NeonCyan else EsportsCardBorder
                    ),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.message,
                fontFamily = RajdhaniFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

// 3. Player Profile Screen
@Composable
fun ProfileScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val currentUser by viewModel.currentUser.collectAsState()
    var isEditingProfile by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SimpleTopBar(title = "PLAYER PROFILE", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Profile Card Header
            item {
                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonCyan) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(2.dp, NeonCyan, CircleShape)
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_pro_genius_logo),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(68.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentUser?.fullName ?: "Player",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Free Fire: ${currentUser?.ffName ?: "N/A"}",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NeonCyan
                        )
                        Text(
                            text = "UID: ${currentUser?.ffUid ?: "N/A"}",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CyberOutlinedButton(
                            text = "EDIT PROFILE",
                            onClick = { isEditingProfile = true },
                            icon = Icons.Default.Edit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Career Stats Grid
            item {
                Text(
                    text = "CAREER STATS",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatsBox("TOTAL MATCHES", "${currentUser?.totalMatches ?: 0}", NeonCyan, Modifier.weight(1f))
                    StatsBox("MATCHES WON", "${currentUser?.matchesWon ?: 0}", NeonGreen, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatsBox("TOTAL KILLS", "${currentUser?.totalKills ?: 0}", NeonRed, Modifier.weight(1f))
                    val winRate = if ((currentUser?.totalMatches ?: 0) > 0) {
                        ((currentUser!!.matchesWon.toFloat() / currentUser!!.totalMatches.toFloat()) * 100).toInt()
                    } else 0
                    StatsBox("WIN RATE", "$winRate%", ElectricPurple, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                StatsBox("WALLET COINS", "${currentUser?.coins ?: 0} Coins", GoldYellow, Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Navigation Shortcuts
            item {
                Text(
                    text = "QUICK SHORTCUTS",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                CyberCard(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(Screen.MatchHistory) },
                    borderColor = EsportsCardBorder
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("View Personal Match History", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("OPEN >", fontFamily = OrbitronFontFamily, fontSize = 12.sp, color = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                CyberCard(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(Screen.PlayerDashboard(PlayerTab.PAYMENTS)) },
                    borderColor = EsportsCardBorder
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Deposit Coins & Payment History", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("OPEN >", fontFamily = OrbitronFontFamily, fontSize = 12.sp, color = GoldYellow)
                    }
                }
            }
        }
    }

    if (isEditingProfile) {
        EditProfileDialog(
            currentUser = currentUser,
            onDismiss = { isEditingProfile = false },
            onSave = { name, ff, uid ->
                viewModel.updateProfile(name, ff, uid)
                isEditingProfile = false
            }
        )
    }
}

@Composable
fun StatsBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    CyberCard(modifier = modifier, borderColor = EsportsCardBorder) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(label, fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun EditProfileDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSave: (fullName: String, ffName: String, ffUid: String) -> Unit
) {
    var name by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var ff by remember { mutableStateOf(currentUser?.ffName ?: "") }
    var uid by remember { mutableStateOf(currentUser?.ffUid ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = {
            Text("EDIT WARRIOR PROFILE", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeonCyan)
        },
        text = {
            Column {
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
                    onValueChange = { if (it.all { char -> char.isDigit() }) uid = it },
                    label = { Text("Free Fire UID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "SAVE CHANGES",
                onClick = { onSave(name, ff, uid) }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        }
    )
}

// 4. Match History Screen
@Composable
fun MatchHistoryScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val myRegistrations by viewModel.myRegistrations.collectAsState()
    val allTournaments by viewModel.allTournaments.collectAsState()

    val history = remember(myRegistrations, allTournaments) {
        myRegistrations.mapNotNull { reg ->
            val tourney = allTournaments.find { it.id == reg.tournamentId }
            if (tourney != null) reg to tourney else null
        }
    }

    Scaffold(
        topBar = { SimpleTopBar(title = "MATCH HISTORY", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (history.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No match history available.", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                    }
                }
            } else {
                items(history) { (reg, tourney) ->
                    CyberCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        borderColor = if (reg.rank == 1) GoldYellow else EsportsCardBorder
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tourney.title, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                StatusBadge(status = tourney.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Map: ${tourney.map} | ${tourney.matchType}", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = NeonCyan)
                            Text("Your Performance: Kills: ${reg.kills} | Placement: #${if (reg.rank > 0) reg.rank else "Pending"}", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            if (reg.rewardCoins > 0) {
                                Text("Reward Won: +${reg.rewardCoins} Coins", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldYellow)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. Rules Screen
@Composable
fun RulesScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val appSettings by viewModel.appSettings.collectAsState()

    Scaffold(
        topBar = { SimpleTopBar(title = "TOURNAMENT RULES", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = NeonCyan) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OFFICIAL ESPORTS RULEBOOK",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = appSettings?.rulesText ?: "Follow all Fair Play guidelines and keep screenshots for verification.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

// 6. Support Screen
@Composable
fun SupportScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()

    Scaffold(
        topBar = { SimpleTopBar(title = "SUPPORT & HELP", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "CUSTOMER SUPPORT",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeonCyan
                )
                Text(
                    text = "Need help with payments, room passwords, or match results? Contact our admin team 24/7:",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // WhatsApp Support
                CyberCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    borderColor = NeonGreen
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = NeonGreen)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("WHATSAPP SUPPORT", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonGreen)
                                Text(appSettings?.supportWhatsapp ?: "+92 300 1234567", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            }
                        }
                        IconButton(onClick = {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("WhatsApp", appSettings?.supportWhatsapp ?: "+92 300 1234567"))
                            viewModel.showToast("WhatsApp number copied!")
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonGreen)
                        }
                    }
                }

                // Email Support
                CyberCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    borderColor = NeonCyan
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("EMAIL SUPPORT", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                                Text(appSettings?.supportEmail ?: "support@progeniusbattle.com", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            }
                        }
                        IconButton(onClick = {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("Email", appSettings?.supportEmail ?: "support@progeniusbattle.com"))
                            viewModel.showToast("Support email copied!")
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "FREQUENTLY ASKED QUESTIONS",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                FaqCard(
                    question = "When do I get the Custom Room ID & Password?",
                    answer = "Room details are published directly on your joined match card 15 minutes before the match start time."
                )
                FaqCard(
                    question = "How long does payment approval take?",
                    answer = "Payments via EasyPaisa, JazzCash, and Paytm are typically verified by an admin within 10 to 30 minutes."
                )
                FaqCard(
                    question = "Can I play on PC / Emulator?",
                    answer = "No, emulators are strictly banned to ensure 100% fair play for mobile players."
                )
            }
        }
    }
}

@Composable
fun FaqCard(question: String, answer: String) {
    CyberCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), borderColor = EsportsCardBorder) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Q: $question", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NeonCyan)
            Spacer(modifier = Modifier.height(4.dp))
            Text("A: $answer", fontFamily = RajdhaniFontFamily, fontSize = 13.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AboutUsScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = { SimpleTopBar(title = "ABOUT US", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pak_tournament_logo),
                        contentDescription = "PAK PAKISTAN TOURNAMENT",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "🇵🇰 PAK PAKISTAN TOURNAMENT",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NeonCyan,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "The Ultimate Free Fire Esports Platform in Pakistan",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = GoldYellow,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("OUR MISSION", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "PAK PAKISTAN TOURNAMENT is dedicated to empowering mobile Free Fire gamers across Pakistan with professional, verified tournament competitions. We provide seamless custom rooms, transparent prize pools, and instant coin redemptions via EasyPaisa and JazzCash.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ADMINISTRATION & MANAGEMENT", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Main Admin: Abdullah (Finance, Player Relations, Overall Platform)", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Match Admin: Hamza (Custom Rooms, Match Schedules, Result Scrims)", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("FAIR PLAY GUARANTEE", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "We enforce strict mobile-only rules. Emulators, mod APKs, headshot scripts, and unsportsmanlike conduct result in immediate permanent bans and coin forfeitures.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = { SimpleTopBar(title = "PRIVACY POLICY", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("PRIVACY POLICY", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeonCyan)
                Text("Last updated: Season 2024", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(14.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("1. DATA COLLECTION", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "We collect your player name, Free Fire In-Game Name, Free Fire UID, and transaction reference numbers solely to verify match registrations, allocate custom room slots, and disburse prize coins.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("2. FINANCIAL TRANSACTIONS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "We never store banking passwords or OTPs. Deposits and withdrawals are handled via manual verification of EasyPaisa, JazzCash, and Paytm transaction proofs.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("3. ACCOUNT SECURITY", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldYellow)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Player passwords are protected securely. Do not share your room credentials with unregistered players or external parties.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TermsConditionsScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = { SimpleTopBar(title = "TERMS & CONDITIONS", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("TERMS AND CONDITIONS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NeonCyan)
                Text("Rules of participation and dispute arbitration", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(14.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("1. ELIGIBILITY", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Players must register with their exact Free Fire UID and in-game name. Players entering a custom room with a mismatched UID will be kicked without refund.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("2. CANCELLATION & REFUNDS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldYellow)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "If a tournament is cancelled by an administrator, 100% of the entry coins will be automatically refunded to all registered players' wallets.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("3. DISPUTES & SCREENSHOTS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonRed)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All players must take a screenshot of their end-game kill and placement screen. Any dispute must be filed in the Support tab within 30 minutes of match completion.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamRegistrationScreen(viewModel: BattleViewModel, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val currentUser by viewModel.currentUser.collectAsState()
    var teamName by remember { mutableStateOf("") }
    var teamTag by remember { mutableStateOf("") }
    var p2Uid by remember { mutableStateOf("") }
    var p2Name by remember { mutableStateOf("") }
    var p3Uid by remember { mutableStateOf("") }
    var p3Name by remember { mutableStateOf("") }
    var p4Uid by remember { mutableStateOf("") }
    var p4Name by remember { mutableStateOf("") }

    Scaffold(
        topBar = { SimpleTopBar(title = "TEAM REGISTRATION", onBack = onBack) },
        containerColor = EsportsBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "REGISTER SQUAD / DUO TEAM",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeonCyan
                )
                Text(
                    text = "Reserve your squad slots for upcoming Squad & Duo tournaments",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TEAM IDENTITY", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldYellow)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            label = { Text("Team Name (e.g. Markhor Legends)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = cyberTextFieldColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = teamTag,
                            onValueChange = { teamTag = it.take(4).uppercase() },
                            label = { Text("Team Tag (e.g. ML)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = cyberTextFieldColors()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                CyberCard(modifier = Modifier.fillMaxWidth(), borderColor = EsportsCardBorder) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("CAPTAIN (SLOT 1)", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Captain: ${currentUser?.fullName ?: "You"} (${currentUser?.ffName}) | UID: ${currentUser?.ffUid}", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("PLAYER 2", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = p2Name,
                                onValueChange = { p2Name = it },
                                label = { Text("FF Name") },
                                modifier = Modifier.weight(1f),
                                colors = cyberTextFieldColors()
                            )
                            OutlinedTextField(
                                value = p2Uid,
                                onValueChange = { p2Uid = it },
                                label = { Text("FF UID") },
                                modifier = Modifier.weight(1f),
                                colors = cyberTextFieldColors()
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("PLAYER 3", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = p3Name,
                                onValueChange = { p3Name = it },
                                label = { Text("FF Name") },
                                modifier = Modifier.weight(1f),
                                colors = cyberTextFieldColors()
                            )
                            OutlinedTextField(
                                value = p3Uid,
                                onValueChange = { p3Uid = it },
                                label = { Text("FF UID") },
                                modifier = Modifier.weight(1f),
                                colors = cyberTextFieldColors()
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("PLAYER 4", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = p4Name,
                                onValueChange = { p4Name = it },
                                label = { Text("FF Name") },
                                modifier = Modifier.weight(1f),
                                colors = cyberTextFieldColors()
                            )
                            OutlinedTextField(
                                value = p4Uid,
                                onValueChange = { p4Uid = it },
                                label = { Text("FF UID") },
                                modifier = Modifier.weight(1f),
                                colors = cyberTextFieldColors()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))

                CyberButton(
                    text = "REGISTER SQUAD TEAM",
                    onClick = {
                        if (teamName.isNotBlank() && teamTag.isNotBlank()) {
                            viewModel.createTeam(teamName, teamTag, p2Uid, p3Uid, p4Uid)
                        } else {
                            viewModel.showToast("Please enter Team Name and Tag")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

