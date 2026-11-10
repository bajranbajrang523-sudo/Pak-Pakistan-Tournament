package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.filled.Lock
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppSettingsEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.RegistrationEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.example.ui.BattleViewModel
import com.example.ui.PlayerTab
import com.example.ui.Screen
import com.example.ui.components.CoinBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.HomeSlider
import com.example.ui.components.MapBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BronzeColor
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsCard
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.ClearFontFamily
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlayerDashboardScreen(
    viewModel: BattleViewModel,
    initialTab: PlayerTab = PlayerTab.HOME,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(initialTab) }

    val currentUser by viewModel.currentUser.collectAsState()
    val sliderImages by viewModel.sliderImages.collectAsState()
    val tournaments by viewModel.allTournaments.collectAsState()
    val myRegistrations by viewModel.myRegistrations.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val myPayments by viewModel.myPayments.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = EsportsSurface,
                modifier = Modifier.width(300.dp)
            ) {
                DrawerNavigationContent(
                    currentUser = currentUser,
                    onNavigate = { screen ->
                        coroutineScope.launch { drawerState.close() }
                        when (screen) {
                            is Screen.PlayerDashboard -> selectedTab = screen.tab
                            else -> viewModel.navigateTo(screen)
                        }
                    },
                    onLogout = {
                        coroutineScope.launch { drawerState.close() }
                        viewModel.logout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                PlayerTopBar(
                    currentUser = currentUser,
                    onMenuClick = { coroutineScope.launch { drawerState.open() } },
                    onCoinsClick = { selectedTab = PlayerTab.PAYMENTS },
                    onProfileClick = { viewModel.navigateTo(Screen.Profile) },
                    onAnnouncementsClick = { viewModel.navigateTo(Screen.Announcements) }
                )
            },
            bottomBar = {
                PlayerBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onProfileClick = { viewModel.navigateTo(Screen.Profile) }
                )
            },
            containerColor = EsportsBackground,
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    PlayerTab.HOME -> PlayerHomeContent(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        sliderImages = sliderImages,
                        tournaments = tournaments,
                        myRegistrations = myRegistrations,
                        announcements = announcements,
                        onViewAllMatches = { selectedTab = PlayerTab.MATCHES },
                        onViewLeaderboard = { selectedTab = PlayerTab.LEADERBOARD }
                    )

                    PlayerTab.MATCHES -> PlayerMatchesContent(
                        viewModel = viewModel,
                        tournaments = tournaments,
                        myRegistrations = myRegistrations,
                        currentUser = currentUser
                    )

                    PlayerTab.MY_TOURNAMENTS -> PlayerMyTournamentsContent(
                        viewModel = viewModel,
                        tournaments = tournaments,
                        myRegistrations = myRegistrations,
                        onBrowseMatches = { selectedTab = PlayerTab.MATCHES }
                    )

                    PlayerTab.PAYMENTS -> PlayerPaymentContent(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        myPayments = myPayments,
                        appSettings = appSettings
                    )

                    PlayerTab.LEADERBOARD -> PlayerLeaderboardContent(
                        leaderboard = leaderboard,
                        currentUserId = currentUser?.id ?: 0L
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerTopBar(
    currentUser: UserEntity?,
    onMenuClick: () -> Unit,
    onCoinsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAnnouncementsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(com.example.ui.theme.SecondaryBackground)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Name displayed in a stylish header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_pak_tournament_logo),
                contentDescription = "PAK PAKISTAN TOURNAMENT Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            Brush.sweepGradient(
                                listOf(
                                    com.example.ui.theme.FireOrange,
                                    com.example.ui.theme.ElectricCyan,
                                    com.example.ui.theme.FireOrange
                                )
                            )
                        ),
                        CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "PAK PAKISTAN",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = com.example.ui.theme.ElectricCyan,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "TOURNAMENT • FIRE ESPORTS",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = com.example.ui.theme.FireOrange,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Profile button and three-line menu button on the top right
        Row(verticalAlignment = Alignment.CenterVertically) {
            CoinBadge(
                coins = currentUser?.coins ?: 0,
                onClick = onCoinsClick,
                modifier = Modifier.testTag("topbar_coins_badge")
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.testTag("topbar_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = com.example.ui.theme.ElectricCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.testTag("player_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Menu",
                    tint = com.example.ui.theme.TextWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerBottomNav(
    selectedTab: PlayerTab,
    onTabSelected: (PlayerTab) -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = EsportsSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            Triple(PlayerTab.HOME, "Home", Icons.Default.Home),
            Triple(PlayerTab.MATCHES, "Matches", Icons.Default.SportsEsports),
            Triple(PlayerTab.PAYMENTS, "Wallet", Icons.Default.AccountBalanceWallet),
            Triple(PlayerTab.LEADERBOARD, "Ranking", Icons.Default.WorkspacePremium),
            Triple(PlayerTab.MY_TOURNAMENTS, "Profile", Icons.Default.Person)
        )

        items.forEach { (tab, label, icon) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (tab == PlayerTab.MY_TOURNAMENTS) {
                        onProfileClick()
                    } else {
                        onTabSelected(tab)
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF03141A),
                    selectedTextColor = NeonCyan,
                    indicatorColor = NeonCyan,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("bottom_nav_${label.lowercase()}")
            )
        }
    }
}

@Composable
fun DrawerNavigationContent(
    currentUser: UserEntity?,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Player Header Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, NeonGreen, CircleShape)
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pak_tournament_logo),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(46.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = currentUser?.fullName ?: "Guest Player",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "FF: ${currentUser?.ffName ?: "N/A"} | UID: ${currentUser?.ffUid ?: "N/A"}",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = NeonGreen
                )
                Text(
                    text = "Balance: ${currentUser?.coins ?: 0} Coins",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GoldYellow
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(EsportsCardBorder)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                DrawerMenuItem(Icons.Default.Home, "Home Dashboard", { onNavigate(Screen.PlayerDashboard(PlayerTab.HOME)) })
                DrawerMenuItem(Icons.Default.SportsEsports, "Tournaments & Matches", { onNavigate(Screen.PlayerDashboard(PlayerTab.MATCHES)) })
                DrawerMenuItem(Icons.Default.AccountBalanceWallet, "Wallet (EasyPaisa/JazzCash)", { onNavigate(Screen.PlayerDashboard(PlayerTab.PAYMENTS)) })
                DrawerMenuItem(Icons.Default.WorkspacePremium, "Leaderboard & Ranking", { onNavigate(Screen.PlayerDashboard(PlayerTab.LEADERBOARD)) })
                DrawerMenuItem(Icons.Default.SportsEsports, "Team Registration (Squad)", { onNavigate(Screen.TeamRegistration) })
                DrawerMenuItem(Icons.Default.Person, "My Profile", { onNavigate(Screen.Profile) })
                DrawerMenuItem(Icons.Default.History, "Match History", { onNavigate(Screen.MatchHistory) })
                DrawerMenuItem(Icons.Default.Chat, "Player Community Chat", { onNavigate(Screen.GroupChat) })
                DrawerMenuItem(Icons.Default.Campaign, "Announcements & Alerts", { onNavigate(Screen.Announcements) })
                DrawerMenuItem(Icons.Default.Gavel, "Rules & Regulations", { onNavigate(Screen.Rules) })
                DrawerMenuItem(Icons.Default.HeadsetMic, "Support & Help Desk", { onNavigate(Screen.Support) })
                DrawerMenuItem(Icons.Default.Star, "About Us", { onNavigate(Screen.AboutUs) })
                DrawerMenuItem(Icons.Default.Lock, "Privacy Policy", { onNavigate(Screen.PrivacyPolicy) })
                DrawerMenuItem(Icons.Default.Notifications, "Terms & Conditions", { onNavigate(Screen.TermsConditions) })
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(EsportsCardBorder)
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.Logout,
            title = "Logout",
            onClick = onLogout,
            tint = NeonRed
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    tint: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontFamily = RajdhaniFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = tint
        )
    }
}

@Composable
fun HeroBanner(
    onQuickJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(
                        listOf(
                            com.example.ui.theme.FireOrange,
                            com.example.ui.theme.ElectricCyan,
                            com.example.ui.theme.PurpleAccent
                        )
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .height(160.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_pak_championship),
            contentDescription = "Free Fire Esports Artwork",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.35f to Color(0x99080D1A),
                            1.0f to Color(0xF5080D1A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(com.example.ui.theme.FireOrange)
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = "🔥 FIRE ESPORTS PRO",
                        fontFamily = ClearFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(com.example.ui.theme.ElectricCyan.copy(alpha = 0.2f))
                        .border(BorderStroke(0.8.dp, com.example.ui.theme.ElectricCyan), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = "FREE FIRE ARENA",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = com.example.ui.theme.ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "BATTLE FOR GLORY & CASH PRIZES",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )

            Text(
                text = "Solo, Duo & Squad tournaments with instant coin rewards",
                fontFamily = ClearFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = com.example.ui.theme.TextLightGray
            )
        }
    }
}

@Composable
fun PlayerHomeContent(
    viewModel: BattleViewModel,
    currentUser: UserEntity?,
    sliderImages: List<com.example.data.model.SliderImageEntity>,
    tournaments: List<TournamentEntity>,
    myRegistrations: List<RegistrationEntity>,
    announcements: List<com.example.data.model.AnnouncementEntity>,
    onViewAllMatches: () -> Unit,
    onViewLeaderboard: () -> Unit
) {
    var selectedTournamentToJoin by remember { mutableStateOf<TournamentEntity?>(null) }
    var matchStatusFilter by remember { mutableStateOf("ALL") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        // 1. Beautiful Hero Banner with Free Fire esports artwork
        item {
            HeroBanner(onQuickJoin = onViewAllMatches)
        }

        // 2. Five Rotating Promotional Banners with smooth transitions
        item {
            HomeSlider(sliders = sliderImages)
        }

        // 3. Modern Player Profile Card (Name, Free Fire UID, Coins, Statistics)
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                borderColor = com.example.ui.theme.SurfaceCardBorder,
                glowEffect = true
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(
                                        BorderStroke(1.5.dp, Brush.sweepGradient(listOf(com.example.ui.theme.FireOrange, com.example.ui.theme.ElectricCyan))),
                                        CircleShape
                                    )
                                    .background(com.example.ui.theme.PrimaryBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_pak_tournament_logo),
                                    contentDescription = "Player Avatar",
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentUser?.fullName ?: "Guest Player",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "FF: ${currentUser?.ffName ?: "Warrior"} | UID: ${currentUser?.ffUid ?: "---"}",
                                    fontFamily = RajdhaniFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = com.example.ui.theme.ElectricCyan
                                )
                            }
                        }

                        CoinBadge(
                            coins = currentUser?.coins ?: 0,
                            onClick = { viewModel.navigateTo(Screen.PlayerDashboard(PlayerTab.PAYMENTS)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Player Statistics Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(com.example.ui.theme.PrimaryBackground)
                            .border(BorderStroke(1.dp, com.example.ui.theme.SurfaceCardBorder), RoundedCornerShape(8.dp))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PlayerStatItem("Matches", "${currentUser?.totalMatches ?: 0}", com.example.ui.theme.ElectricCyan)
                        PlayerStatItem("Wins", "${currentUser?.matchesWon ?: 0}", com.example.ui.theme.SuccessGreen)
                        val winRate = if ((currentUser?.totalMatches ?: 0) > 0) {
                            ((currentUser?.matchesWon ?: 0).toFloat() / (currentUser?.totalMatches ?: 1).toFloat() * 100).toInt()
                        } else 0
                        PlayerStatItem("Win %", "$winRate%", com.example.ui.theme.PurpleAccent)
                        PlayerStatItem("Kills", "${currentUser?.totalKills ?: 0}", com.example.ui.theme.FireOrange)
                    }
                }
            }
        }

        // 4. Daily Check-in & Team Registration Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CyberButton(
                    text = "🎁 DAILY CHECK-IN (+20)",
                    onClick = { viewModel.claimDailyBonus() },
                    containerColor = com.example.ui.theme.FireOrange,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                CyberOutlinedButton(
                    text = "🛡️ REGISTER SQUAD",
                    onClick = { viewModel.navigateTo(Screen.TeamRegistration) },
                    borderColor = com.example.ui.theme.ElectricCyan,
                    textColor = com.example.ui.theme.ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Quick Join Button
        item {
            val nextUpcoming = tournaments.firstOrNull { it.status == "UPCOMING" && it.joinedCount < it.maxPlayers }
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
                CyberButton(
                    text = if (nextUpcoming != null) "⚡ QUICK JOIN NEXT MATCH (${nextUpcoming.entryFee} COINS)" else "⚡ QUICK JOIN TOURNAMENT",
                    onClick = {
                        if (nextUpcoming != null) {
                            selectedTournamentToJoin = nextUpcoming
                        } else {
                            onViewAllMatches()
                        }
                    },
                    containerColor = com.example.ui.theme.ElectricCyan,
                    contentColor = com.example.ui.theme.PrimaryBackground,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Ticker for latest announcement
        if (announcements.isNotEmpty()) {
            item {
                val latest = announcements.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(com.example.ui.theme.SecondaryBackground)
                        .border(BorderStroke(1.dp, com.example.ui.theme.ElectricCyan.copy(alpha = 0.5f)), RoundedCornerShape(10.dp))
                        .clickable { viewModel.navigateTo(Screen.Announcements) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = com.example.ui.theme.FireOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${latest.title}: ${latest.message}",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 6. Featured Tournament Matches Header & Status Filter
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOURNAMENT MATCHES",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = com.example.ui.theme.ElectricCyan
                    )
                    Text(
                        text = "VIEW ALL >",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = com.example.ui.theme.FireOrange,
                        modifier = Modifier.clickable { onViewAllMatches() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Filter chips: ALL, UPCOMING, LIVE NOW, COMPLETED
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL", "UPCOMING", "ONGOING", "COMPLETED").forEach { statusLabel ->
                        val isSelected = matchStatusFilter == statusLabel
                        val chipText = when (statusLabel) {
                            "ONGOING" -> "LIVE NOW"
                            else -> statusLabel
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { matchStatusFilter = statusLabel },
                            label = {
                                Text(
                                    text = chipText,
                                    fontFamily = ClearFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (statusLabel == "ONGOING") com.example.ui.theme.FireOrange else com.example.ui.theme.ElectricCyan,
                                selectedLabelColor = if (statusLabel == "ONGOING") Color.White else com.example.ui.theme.PrimaryBackground,
                                containerColor = com.example.ui.theme.SecondaryBackground,
                                labelColor = com.example.ui.theme.TextLightGray
                            )
                        )
                    }
                }
            }
        }

        // Tournaments list matching the selected status
        val displayedTournaments = tournaments.filter {
            if (matchStatusFilter == "ALL") true else it.status.equals(matchStatusFilter, ignoreCase = true)
        }

        if (displayedTournaments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No $matchStatusFilter tournament matches available right now.",
                        fontFamily = RajdhaniFontFamily,
                        color = com.example.ui.theme.TextLightGray
                    )
                }
            }
        } else {
            items(displayedTournaments) { tournament ->
                val hasJoined = myRegistrations.any { it.tournamentId == tournament.id }
                TournamentCard(
                    tournament = tournament,
                    hasJoined = hasJoined,
                    onJoinClick = { selectedTournamentToJoin = tournament }
                )
            }
        }
    }

    if (selectedTournamentToJoin != null) {
        JoinTournamentDialog(
            tournament = selectedTournamentToJoin!!,
            userCoins = currentUser?.coins ?: 0,
            onDismiss = { selectedTournamentToJoin = null },
            onConfirmJoin = {
                viewModel.joinTournament(selectedTournamentToJoin!!.id)
                selectedTournamentToJoin = null
            }
        )
    }
}

@Composable
fun PlayerStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = color
        )
        Text(
            text = label,
            fontFamily = ClearFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = com.example.ui.theme.TextLightGray
        )
    }
}

@Composable
fun TournamentCard(
    tournament: TournamentEntity,
    hasJoined: Boolean,
    onJoinClick: () -> Unit
) {
    val context = LocalContext.current
    val isFull = tournament.joinedCount >= tournament.maxPlayers
    val availableSlots = (tournament.maxPlayers - tournament.joinedCount).coerceAtLeast(0)

    val glowingBorder = when (tournament.status) {
        "ONGOING" -> BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(com.example.ui.theme.FireOrange, com.example.ui.theme.NeonRed))
        )
        "COMPLETED" -> BorderStroke(
            1.2.dp,
            com.example.ui.theme.SuccessGreen
        )
        else -> BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    com.example.ui.theme.ElectricCyan.copy(alpha = 0.8f),
                    com.example.ui.theme.PurpleAccent.copy(alpha = 0.8f)
                )
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .border(glowingBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.SecondaryBackground)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Map/Type Badge + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MapBadge(map = tournament.map, matchType = tournament.matchType)
                StatusBadge(status = tournament.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tournament Name & Mode
            Text(
                text = tournament.title,
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mode: ${tournament.matchType} • ",
                    fontFamily = ClearFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = com.example.ui.theme.FireOrange
                )
                Text(
                    text = "Time: ${tournament.matchTime}",
                    fontFamily = ClearFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = com.example.ui.theme.ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Entry Fee, Prize Pool, Per Kill Metrics Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(com.example.ui.theme.PrimaryBackground)
                    .border(BorderStroke(1.dp, com.example.ui.theme.SurfaceCardBorder), RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ENTRY FEE", fontFamily = ClearFontFamily, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = com.example.ui.theme.TextLightGray)
                    Text("${tournament.entryFee} Coins", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = com.example.ui.theme.FireOrange)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PRIZE POOL", fontFamily = ClearFontFamily, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = com.example.ui.theme.TextLightGray)
                    Text("${tournament.prizePool} Coins", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = com.example.ui.theme.SuccessGreen)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PER KILL", fontFamily = ClearFontFamily, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = com.example.ui.theme.TextLightGray)
                    Text("+${tournament.perKillReward} Coins", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = com.example.ui.theme.ElectricCyan)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Player progress bar & Available Slots
            val progress = (tournament.joinedCount.toFloat() / tournament.maxPlayers.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Available Slots: $availableSlots / ${tournament.maxPlayers} Left",
                    fontFamily = ClearFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (availableSlots <= 5) com.example.ui.theme.FireOrange else com.example.ui.theme.TextLightGray
                )
                Text(
                    text = "${(progress * 100).toInt()}% Full",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = com.example.ui.theme.ElectricCyan
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 0.8f) com.example.ui.theme.FireOrange else com.example.ui.theme.ElectricCyan,
                trackColor = Color(0xFF131E33)
            )

            // Room Credentials (only revealed if joined)
            if (hasJoined) {
                Spacer(modifier = Modifier.height(10.dp))
                if (tournament.roomId.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF221603))
                            .border(1.5.dp, GoldYellow, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = GoldYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "MATCH ROOM CREDENTIALS",
                                        fontFamily = OrbitronFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = GoldYellow
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonGreen)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                 ) {
                                    Text(
                                        text = "LIVE",
                                        fontFamily = ClearFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ROOM ID:", fontFamily = ClearFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextSecondary)
                                    Text(tournament.roomId, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                                }
                                CyberOutlinedButton(
                                    text = "COPY ID",
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Room ID", tournament.roomId))
                                        Toast.makeText(context, "Room ID copied: ${tournament.roomId}", Toast.LENGTH_SHORT).show()
                                    },
                                    borderColor = GoldYellow,
                                    textColor = GoldYellow,
                                    modifier = Modifier.height(34.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ROOM PASSWORD:", fontFamily = ClearFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextSecondary)
                                    Text(tournament.roomPassword, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                                }
                                CyberOutlinedButton(
                                    text = "COPY PASS",
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Room Password", tournament.roomPassword))
                                        Toast.makeText(context, "Password copied: ${tournament.roomPassword}", Toast.LENGTH_SHORT).show()
                                    },
                                    borderColor = GoldYellow,
                                    textColor = GoldYellow,
                                    modifier = Modifier.height(34.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Instructions: Open Free Fire > Custom Room > Enter Room ID & Password to join match.",
                                fontFamily = ClearFontFamily,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF131B2D))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Room ID & Password will be released here 15 mins before start time.",
                                fontFamily = ClearFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            when {
                hasJoined -> {
                    CyberOutlinedButton(
                        text = "JOINED ✓",
                        onClick = { },
                        borderColor = NeonGreen,
                        textColor = NeonGreen,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                tournament.status != "UPCOMING" -> {
                    CyberOutlinedButton(
                        text = "MATCH ${tournament.status}",
                        onClick = { },
                        borderColor = EsportsCardBorder,
                        textColor = TextSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                isFull -> {
                    CyberOutlinedButton(
                        text = "TOURNAMENT FULL",
                        onClick = { },
                        borderColor = NeonRed,
                        textColor = NeonRed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    CyberButton(
                        text = "JOIN MATCH (${tournament.entryFee} COINS)",
                        onClick = onJoinClick,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "join_tournament_btn_${tournament.id}"
                    )
                }
            }
        }
    }
}

@Composable
fun JoinTournamentDialog(
    tournament: TournamentEntity,
    userCoins: Int,
    onDismiss: () -> Unit,
    onConfirmJoin: () -> Unit
) {
    val canAfford = userCoins >= tournament.entryFee

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsSurface,
        title = {
            Text(
                text = "CONFIRM ENTRY",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NeonCyan
            )
        },
        text = {
            Column {
                Text(
                    text = tournament.title,
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Map: ${tournament.map} | Match Type: ${tournament.matchType}",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Time: ${tournament.matchTime}",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EsportsCard)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Your Balance:", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                            Text("$userCoins Coins", fontFamily = OrbitronFontFamily, color = GoldYellow)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Entry Fee:", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                            Text("-${tournament.entryFee} Coins", fontFamily = OrbitronFontFamily, color = NeonRed)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EsportsCardBorder))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Remaining Balance:", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                            Text("${userCoins - tournament.entryFee} Coins", fontFamily = OrbitronFontFamily, color = if (canAfford) NeonGreen else NeonRed)
                        }
                    }
                }

                if (!canAfford) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Insufficient coins! Please deposit coins via Payments.",
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeonRed
                    )
                }
            }
        },
        confirmButton = {
            if (canAfford) {
                CyberButton(
                    text = "PAY & JOIN",
                    onClick = onConfirmJoin,
                    modifier = Modifier.testTag("confirm_join_btn")
                )
            }
        },
        dismissButton = {
            CyberOutlinedButton(
                text = "CANCEL",
                onClick = onDismiss,
                borderColor = EsportsCardBorder,
                textColor = TextSecondary
            )
        }
    )
}

@Composable
fun PlayerMatchesContent(
    viewModel: BattleViewModel,
    tournaments: List<TournamentEntity>,
    myRegistrations: List<RegistrationEntity>,
    currentUser: UserEntity?
) {
    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var selectedTournamentToJoin by remember { mutableStateOf<TournamentEntity?>(null) }

    val filtered = tournaments.filter { t ->
        val typeMatch = if (selectedTypeFilter == "ALL") true else t.matchType.equals(selectedTypeFilter, ignoreCase = true)
        val statusMatch = if (selectedStatusFilter == "ALL") true else t.status.equals(selectedStatusFilter, ignoreCase = true)
        typeMatch && statusMatch
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Match Type Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL", "Solo", "Duo", "Squad").forEach { type ->
                val isSelected = selectedTypeFilter == type
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTypeFilter = type },
                    label = { Text(type, fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = Color(0xFF03141A),
                        containerColor = EsportsSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // Status Tabs (All, Upcoming, Ongoing, Completed)
        TabRow(
            selectedTabIndex = when (selectedStatusFilter) {
                "UPCOMING" -> 1
                "ONGOING" -> 2
                "COMPLETED" -> 3
                else -> 0
            },
            containerColor = EsportsSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                val index = when (selectedStatusFilter) {
                    "UPCOMING" -> 1
                    "ONGOING" -> 2
                    "COMPLETED" -> 3
                    else -> 0
                }
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = NeonCyan
                )
            }
        ) {
            listOf("ALL" to "All", "UPCOMING" to "Upcoming", "ONGOING" to "Ongoing", "COMPLETED" to "Completed").forEach { (status, title) ->
                Tab(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    text = {
                        Text(
                            text = title,
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matches match current filter.",
                            fontFamily = RajdhaniFontFamily,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(filtered) { tournament ->
                    val hasJoined = myRegistrations.any { it.tournamentId == tournament.id }
                    TournamentCard(
                        tournament = tournament,
                        hasJoined = hasJoined,
                        onJoinClick = { selectedTournamentToJoin = tournament }
                    )
                }
            }
        }
    }

    if (selectedTournamentToJoin != null) {
        JoinTournamentDialog(
            tournament = selectedTournamentToJoin!!,
            userCoins = currentUser?.coins ?: 0,
            onDismiss = { selectedTournamentToJoin = null },
            onConfirmJoin = {
                viewModel.joinTournament(selectedTournamentToJoin!!.id)
                selectedTournamentToJoin = null
            }
        )
    }
}

@Composable
fun PlayerMyTournamentsContent(
    viewModel: BattleViewModel,
    tournaments: List<TournamentEntity>,
    myRegistrations: List<RegistrationEntity>,
    onBrowseMatches: () -> Unit
) {
    val myTournamentsMap = remember(tournaments, myRegistrations) {
        myRegistrations.mapNotNull { reg ->
            val tourney = tournaments.find { it.id == reg.tournamentId }
            if (tourney != null) reg to tourney else null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "MY JOINED TOURNAMENTS",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NeonCyan
            )
            Text(
                text = "All matches you are participating in or have completed",
                fontFamily = RajdhaniFontFamily,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (myTournamentsMap.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "You haven't joined any tournaments yet!",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Browse available tournaments and win prize coins.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CyberButton(text = "BROWSE TOURNAMENTS", onClick = onBrowseMatches)
                    }
                }
            }
        } else {
            items(myTournamentsMap) { (reg, tournament) ->
                TournamentCard(
                    tournament = tournament,
                    hasJoined = true,
                    onJoinClick = { }
                )
            }
        }
    }
}

@Composable
fun PlayerPaymentContent(
    viewModel: BattleViewModel,
    currentUser: UserEntity?,
    myPayments: List<PaymentEntity>,
    appSettings: AppSettingsEntity?
) {
    val context = LocalContext.current
    var walletMode by remember { mutableStateOf("DEPOSIT") } // "DEPOSIT" or "WITHDRAW"
    var selectedMethod by remember { mutableStateOf("EasyPaisa") }
    var amountText by remember { mutableStateOf("200") }
    var transactionIdText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    var withdrawMethod by remember { mutableStateOf("EasyPaisa") }
    var withdrawAccount by remember { mutableStateOf("") }
    var withdrawTitle by remember { mutableStateOf("") }
    var withdrawAmountText by remember { mutableStateOf("500") }

    val coinsToReceive = amountText.toIntOrNull() ?: 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Balance Card
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = GoldYellow
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CURRENT COIN BALANCE", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                        Text("${currentUser?.coins ?: 0} COINS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GoldYellow)
                        Text("1 Cash (PKR/INR) = 1 Coin", fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = NeonGreen)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF382A05))
                            .border(1.dp, GoldYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(24.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Wallet Action Mode Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (walletMode == "DEPOSIT") {
                    CyberButton(
                        text = "DEPOSIT COINS",
                        onClick = { walletMode = "DEPOSIT" },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    CyberOutlinedButton(
                        text = "DEPOSIT COINS",
                        onClick = { walletMode = "DEPOSIT" },
                        borderColor = EsportsCardBorder,
                        textColor = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (walletMode == "WITHDRAW") {
                    CyberButton(
                        text = "WITHDRAW CASH",
                        onClick = { walletMode = "WITHDRAW" },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    CyberOutlinedButton(
                        text = "WITHDRAW CASH",
                        onClick = { walletMode = "WITHDRAW" },
                        borderColor = EsportsCardBorder,
                        textColor = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (walletMode == "DEPOSIT") {
            // Admin Accounts Details
            item {
                Text(
                    text = "DEPOSIT PAYMENT DETAILS",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                )
                Text(
                    text = "Send cash to any of the accounts below, then submit your transaction ID:",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // EasyPaisa Account
                AdminPaymentAccountCard(
                    method = "EasyPaisa",
                    title = appSettings?.easypaisaTitle ?: "Pak Pakistan Official EasyPaisa",
                    number = appSettings?.easypaisaNumber ?: "0312-7861122",
                    color = NeonGreen,
                    onCopy = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("EasyPaisa", appSettings?.easypaisaNumber ?: "0312-7861122"))
                        viewModel.showToast("EasyPaisa number copied!")
                    }
                )

                // JazzCash Account
                AdminPaymentAccountCard(
                    method = "JazzCash",
                    title = appSettings?.jazzcashTitle ?: "Pak Pakistan Official JazzCash",
                    number = appSettings?.jazzcashNumber ?: "0300-7861122",
                    color = NeonRed,
                    onCopy = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("JazzCash", appSettings?.jazzcashNumber ?: "0300-7861122"))
                        viewModel.showToast("JazzCash number copied!")
                    }
                )

                // Paytm Account
                AdminPaymentAccountCard(
                    method = "Paytm / UPI",
                    title = appSettings?.paytmTitle ?: "Pak Pakistan Official Paytm",
                    number = appSettings?.paytmNumber ?: "pakpakistan@paytm",
                    color = NeonCyan,
                    onCopy = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("Paytm", appSettings?.paytmNumber ?: "pakpakistan@paytm"))
                        viewModel.showToast("Paytm UPI copied!")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Payment Submission Form
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SUBMIT PAYMENT PROOF",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeonCyan
                        )
                        Text(
                            text = "Admin will manually verify transaction ID before crediting coins",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Method Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("EasyPaisa", "JazzCash", "Paytm").forEach { method ->
                                val isSelected = selectedMethod == method
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedMethod = method },
                                    label = { Text(method, fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyan,
                                        selectedLabelColor = Color(0xFF03141A),
                                        containerColor = EsportsSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { if (it.all { char -> char.isDigit() }) amountText = it },
                            label = { Text("Amount Paid (Coins to add: $coinsToReceive)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("payment_amount_input"),
                            colors = cyberTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = transactionIdText,
                            onValueChange = { transactionIdText = it },
                            label = { Text("Transaction ID / Reference ID *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("payment_txid_input"),
                            colors = cyberTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("Sender Name / Mobile Number / Remarks") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("payment_note_input"),
                            colors = cyberTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CyberButton(
                            text = "SUBMIT DEPOSIT PROOF",
                            onClick = {
                                val amount = amountText.toDoubleOrNull() ?: 0.0
                                if (amount <= 0) {
                                    viewModel.showToast("Please enter a valid amount")
                                } else if (transactionIdText.isBlank()) {
                                    viewModel.showToast("Transaction ID is mandatory!")
                                } else {
                                    viewModel.submitPayment(
                                        method = selectedMethod,
                                        amount = amount,
                                        coins = coinsToReceive,
                                        transactionId = transactionIdText,
                                        note = noteText
                                    )
                                    transactionIdText = ""
                                    noteText = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "submit_payment_btn"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            // Withdrawal Request Form
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WITHDRAW CASH TO ACCOUNT",
                            fontFamily = OrbitronFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeonGreen
                        )
                        Text(
                            text = "Admin Abdullah verifies & transfers PKR directly to your account",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("EasyPaisa", "JazzCash", "Paytm").forEach { method ->
                                val isSelected = withdrawMethod == method
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { withdrawMethod = method },
                                    label = { Text(method, fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonGreen,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EsportsSurfaceVariant,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = withdrawAccount,
                            onValueChange = { withdrawAccount = it },
                            label = { Text("$withdrawMethod Mobile / Account Number *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = cyberTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = withdrawTitle,
                            onValueChange = { withdrawTitle = it },
                            label = { Text("Account Holder Name *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = cyberTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = withdrawAmountText,
                            onValueChange = { if (it.all { char -> char.isDigit() }) withdrawAmountText = it },
                            label = { Text("Coins to Withdraw (Minimum 100)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = cyberTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CyberButton(
                            text = "SUBMIT WITHDRAWAL REQUEST",
                            onClick = {
                                val coins = withdrawAmountText.toIntOrNull() ?: 0
                                if (coins < 100) {
                                    viewModel.showToast("Minimum withdrawal is 100 Coins")
                                } else if (withdrawAccount.isBlank() || withdrawTitle.isBlank()) {
                                    viewModel.showToast("Please provide your Account Number and Holder Name")
                                } else {
                                    viewModel.requestWithdrawal(withdrawMethod, withdrawAccount, withdrawTitle, coins)
                                    withdrawAccount = ""
                                    withdrawTitle = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Payment History
        item {
            Text(
                text = "YOUR PAYMENT HISTORY",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (myPayments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No deposit submissions yet.", fontFamily = RajdhaniFontFamily, color = TextSecondary)
                }
            }
        } else {
            items(myPayments) { payment ->
                PaymentHistoryCard(payment = payment)
            }
        }
    }
}

@Composable
fun AdminPaymentAccountCard(
    method: String,
    title: String,
    number: String,
    color: Color,
    onCopy: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderColor = color.copy(alpha = 0.5f),
        backgroundColor = EsportsSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(method, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
                Text(title, fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Text(number, fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
            }
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Number", tint = color)
            }
        }
    }
}

@Composable
fun PaymentHistoryCard(payment: PaymentEntity) {
    val dateStr = remember(payment.submittedAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(payment.submittedAt))
    }

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderColor = EsportsCardBorder
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${payment.method} - ${payment.amountPaid.toInt()} PKR/INR",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                StatusBadge(status = payment.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("TxID: ${payment.transactionId}", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeonCyan)
            Text("Coins: +${payment.coinsRequested} Coins", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldYellow)
            Text(dateStr, fontFamily = RajdhaniFontFamily, fontSize = 11.sp, color = TextSecondary)
            if (payment.adminRemarks.isNotBlank()) {
                Text("Admin note: ${payment.adminRemarks}", fontFamily = RajdhaniFontFamily, fontSize = 12.sp, color = NeonRed)
            }
        }
    }
}

@Composable
fun PlayerLeaderboardContent(
    leaderboard: List<UserEntity>,
    currentUserId: Long
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "WARRIOR LEADERBOARD",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NeonCyan
            )
            Text(
                text = "Ranked by tournament victories and total verified kills",
                fontFamily = RajdhaniFontFamily,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Top 3 Podium
        if (leaderboard.size >= 3) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    LeaderboardPodiumItem(user = leaderboard[1], rank = 2, color = SilverColor, height = 110.dp)
                    LeaderboardPodiumItem(user = leaderboard[0], rank = 1, color = GoldYellow, height = 130.dp)
                    LeaderboardPodiumItem(user = leaderboard[2], rank = 3, color = BronzeColor, height = 95.dp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        items(leaderboard.drop(3)) { user ->
            val rankIndex = leaderboard.indexOf(user) + 1
            val isCurrent = user.id == currentUserId
            LeaderboardRowItem(user = user, rank = rankIndex, isCurrent = isCurrent)
        }
    }
}

@Composable
fun LeaderboardPodiumItem(user: UserEntity, rank: Int, color: Color, height: androidx.compose.ui.unit.Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = user.ffName,
            fontFamily = OrbitronFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${user.matchesWon} Wins",
            fontFamily = RajdhaniFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = color
                )
                Text(
                    text = "${user.totalKills} Kills",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun LeaderboardRowItem(user: UserEntity, rank: Int, isCurrent: Boolean) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        borderColor = if (isCurrent) NeonCyan else EsportsCardBorder,
        backgroundColor = if (isCurrent) Color(0xFF162540) else EsportsCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$rank",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isCurrent) NeonCyan else TextSecondary,
                    modifier = Modifier.width(36.dp)
                )
                Column {
                    Text(
                        text = user.ffName,
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "UID: ${user.ffUid}",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("${user.matchesWon} Wins", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonGreen)
                    Text("${user.totalKills} Kills", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NeonRed)
                }
            }
        }
    }
}
