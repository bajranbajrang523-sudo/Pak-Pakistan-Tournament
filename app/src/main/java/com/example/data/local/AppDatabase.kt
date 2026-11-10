package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdminLogEntity
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AppSettingsEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CoinTransactionEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.RegistrationEntity
import com.example.data.model.SliderImageEntity
import com.example.data.model.TeamEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        TournamentEntity::class,
        RegistrationEntity::class,
        PaymentEntity::class,
        AnnouncementEntity::class,
        ChatMessageEntity::class,
        CoinTransactionEntity::class,
        SliderImageEntity::class,
        AppSettingsEntity::class,
        AdminLogEntity::class,
        TeamEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun registrationDao(): RegistrationDao
    abstract fun paymentDao(): PaymentDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun coinTransactionDao(): CoinTransactionDao
    abstract fun sliderImageDao(): SliderImageDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun adminLogDao(): AdminLogDao
    abstract fun teamDao(): TeamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pak_pakistan_tournament.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedInitialData(database)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    ensureAdminsAndSettings(database)
                }
            }
        }

        private suspend fun ensureAdminsAndSettings(database: AppDatabase) {
            val userDao = database.userDao()
            val settingsDao = database.appSettingsDao()

            // Ensure ONLY Abdullah - Main Admin initially
            if (userDao.getUserByEmail("abdullah@pakpakistan.com") == null) {
                userDao.insertUser(
                    UserEntity(
                        fullName = "Abdullah",
                        ffName = "PakAbdullah",
                        ffUid = "100000001",
                        email = "abdullah@pakpakistan.com",
                        passwordHash = "abdullah123",
                        referralCode = "ABDULLAH",
                        role = "SUPER_ADMIN",
                        adminPermissions = "ALL",
                        coins = 999999
                    )
                )
            }

            // Ensure App Settings
            settingsDao.insertOrUpdateSettings(
                AppSettingsEntity(
                    id = 1,
                    appName = "PAK PAKISTAN TOURNAMENT",
                    easypaisaTitle = "Pak Pakistan Official EasyPaisa",
                    easypaisaNumber = "0312-7861122",
                    jazzcashTitle = "Pak Pakistan Official JazzCash",
                    jazzcashNumber = "0300-7861122",
                    paytmTitle = "Pak Pakistan Official Paytm / UPI",
                    paytmNumber = "pakpakistan@paytm",
                    supportEmail = "support@pakpakistantournament.com",
                    supportWhatsapp = "+92 300 7861122"
                )
            )
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val tournamentDao = database.tournamentDao()
            val announcementDao = database.announcementDao()
            val chatDao = database.chatMessageDao()
            val sliderDao = database.sliderImageDao()
            val settingsDao = database.appSettingsDao()

            val adminLogDao = database.adminLogDao()

            // 1. Seed Main Admin: Abdullah (ONLY admin initially)
            val abdullahId = userDao.insertUser(
                UserEntity(
                    fullName = "Abdullah",
                    ffName = "PakAbdullah",
                    ffUid = "100000001",
                    email = "abdullah@pakpakistan.com",
                    passwordHash = "abdullah123",
                    referralCode = "ABDULLAH",
                    role = "SUPER_ADMIN",
                    adminPermissions = "ALL",
                    coins = 999999
                )
            )

            // Seed initial system log
            adminLogDao.insertLog(
                com.example.data.model.AdminLogEntity(
                    adminName = "Abdullah",
                    action = "SYSTEM_INITIALIZED",
                    details = "PAK PAKISTAN TOURNAMENT system deployed by Super Admin Abdullah"
                )
            )

            // 4. Seed initial Pakistani Pro Players on Leaderboard
            val p1 = userDao.insertUser(
                UserEntity(
                    fullName = "Zubair Khan",
                    ffName = "PakViper",
                    ffUid = "729104821",
                    email = "viper@gmail.com",
                    passwordHash = "player123",
                    coins = 3450,
                    totalMatches = 38,
                    matchesWon = 24,
                    totalKills = 112
                )
            )
            val p2 = userDao.insertUser(
                UserEntity(
                    fullName = "Ali Raza",
                    ffName = "LahoreFalcon",
                    ffUid = "810293847",
                    email = "ali@gmail.com",
                    passwordHash = "player123",
                    coins = 2600,
                    totalMatches = 31,
                    matchesWon = 18,
                    totalKills = 89
                )
            )
            val p3 = userDao.insertUser(
                UserEntity(
                    fullName = "Usman Tariq",
                    ffName = "KarachiSniper",
                    ffUid = "928374615",
                    email = "usman@gmail.com",
                    passwordHash = "player123",
                    coins = 1950,
                    totalMatches = 25,
                    matchesWon = 14,
                    totalKills = 76
                )
            )
            val p4 = userDao.insertUser(
                UserEntity(
                    fullName = "Bilal Ahmed",
                    ffName = "RawalpindiRider",
                    ffUid = "618293041",
                    email = "bilal@gmail.com",
                    passwordHash = "player123",
                    coins = 1400,
                    totalMatches = 20,
                    matchesWon = 10,
                    totalKills = 58
                )
            )

            // 5. Seed Five Rotating Pictures for PAK PAKISTAN TOURNAMENT:
            // 1: Pakistan Esports Championship (epic stadium with Lahore Badshahi mosque neon)
            // 2: Free Fire Battle Royale (Bermuda combat)
            // 3: Elite Squad Scrims (character abilities)
            // 4: Gaming Championship Grand Finals
            // 5: PAK PAKISTAN TOURNAMENT Official Brand Logo (Markhor shield + crescent)
            sliderDao.insertSlider(
                SliderImageEntity(
                    title = "Pakistan Esports Championship",
                    subtitle = "The Official Free Fire Battleground for Glory",
                    tag = "🇵🇰 GRAND FINALS",
                    drawableResName = "img_pak_championship",
                    orderIndex = 0
                )
            )
            sliderDao.insertSlider(
                SliderImageEntity(
                    title = "Free Fire Battle Royale",
                    subtitle = "Survive the blue zone and conquer the tournament",
                    tag = "SOLO / SQUAD",
                    drawableResName = "img_ff_battle",
                    orderIndex = 1
                )
            )
            sliderDao.insertSlider(
                SliderImageEntity(
                    title = "Elite Squad Scrims",
                    subtitle = "Coordinate tactical character skill combos for Booyah",
                    tag = "4v4 SQUAD",
                    drawableResName = "img_ff_characters",
                    orderIndex = 2
                )
            )
            sliderDao.insertSlider(
                SliderImageEntity(
                    title = "Gaming Championship",
                    subtitle = "Top placement rewards and instant Easypaisa/JazzCash payouts",
                    tag = "CHAMPIONSHIP",
                    drawableResName = "img_gaming_champ",
                    orderIndex = 3
                )
            )
            sliderDao.insertSlider(
                SliderImageEntity(
                    title = "PAK PAKISTAN TOURNAMENT",
                    subtitle = "Official Pakistan Free Fire Esports Platform",
                    tag = "OFFICIAL BRAND",
                    drawableResName = "ic_pak_tournament_logo",
                    orderIndex = 4
                )
            )

            // 6. Seed Pakistan Free Fire Tournaments
            tournamentDao.insertTournament(
                TournamentEntity(
                    title = "LAHORE BERMUDA GRAND RUSH #108",
                    map = "Bermuda",
                    matchType = "Solo",
                    status = "UPCOMING",
                    matchTime = "Tonight 09:30 PM PKT",
                    entryFee = 25,
                    prizePool = 800,
                    perKillReward = 10,
                    maxPlayers = 48,
                    joinedCount = 33,
                    description = "Solo Classic Battle Royale. Top 3 positions win 400, 250, 150 coins + 10 coins per kill. Room details released 15 mins prior."
                )
            )
            tournamentDao.insertTournament(
                TournamentEntity(
                    title = "KARACHI SQUAD WARZONE #42",
                    map = "Purgatory",
                    matchType = "Squad",
                    status = "UPCOMING",
                    matchTime = "Tomorrow 08:00 PM PKT",
                    entryFee = 60,
                    prizePool = 2200,
                    perKillReward = 20,
                    maxPlayers = 12,
                    joinedCount = 9,
                    description = "Squad 4v4 Tactical Clash. Squad entry fee 60 coins. Winning squad receives 2200 coins directly to wallet."
                )
            )
            tournamentDao.insertTournament(
                TournamentEntity(
                    title = "ISLAMABAD KALAHARI DUO CLASH #19",
                    map = "Kalahari",
                    matchType = "Duo",
                    status = "ONGOING",
                    matchTime = "Match In Progress",
                    entryFee = 35,
                    prizePool = 1100,
                    perKillReward = 15,
                    maxPlayers = 24,
                    joinedCount = 24,
                    roomId = "992147",
                    roomPassword = "pak88battle",
                    description = "Live room active. Please join Free Fire custom room with given Room ID and Password."
                )
            )
            tournamentDao.insertTournament(
                TournamentEntity(
                    title = "PESHAWAR ALPINE MASTERS #05",
                    map = "Alpine",
                    matchType = "Solo",
                    status = "COMPLETED",
                    matchTime = "Yesterday 09:00 PM PKT",
                    entryFee = 30,
                    prizePool = 900,
                    perKillReward = 12,
                    maxPlayers = 48,
                    joinedCount = 48,
                    winnerName = "PakViper",
                    description = "Match finished. Winner PakViper secured Rank 1 with 11 kills."
                )
            )

            // 7. Seed Pakistani Announcements
            announcementDao.insertAnnouncement(
                AnnouncementEntity(
                    title = "🇵🇰 WELCOME TO PAK PAKISTAN TOURNAMENT!",
                    message = "Official Pakistan Free Fire platform launched! Register now to claim 100 Free Welcome Coins. Fast deposits via EasyPaisa and JazzCash.",
                    priority = "HIGH"
                )
            )
            announcementDao.insertAnnouncement(
                AnnouncementEntity(
                    title = "CUSTOM ROOM RULES & ANTI-CHEAT",
                    message = "Room IDs and Passwords are automatically updated on your joined tournament card 15 minutes before the start time. Strict anti-cheat policy enforced.",
                    priority = "NORMAL"
                )
            )

            // 8. Seed Initial Chat Messages
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = abdullahId,
                    senderName = "MAIN ADMIN (ABDULLAH)",
                    senderRole = "ADMIN",
                    message = "Assalam-o-Alaikum warriors! Welcome to PAK PAKISTAN TOURNAMENT. Fair play rules strictly enforced. Good luck to all players!",
                    isPinned = true
                )
            )
            chatDao.insertMessage(
                ChatMessageEntity(
                    senderId = p1,
                    senderName = "PakViper",
                    senderRole = "PLAYER",
                    message = "Best of luck everyone from Lahore! Looking forward to an intense match tonight."
                )
            )

            // 9. Seed App Settings
            settingsDao.insertOrUpdateSettings(
                AppSettingsEntity(
                    id = 1,
                    appName = "PAK PAKISTAN TOURNAMENT",
                    easypaisaTitle = "Pak Pakistan Official EasyPaisa",
                    easypaisaNumber = "0312-7861122",
                    jazzcashTitle = "Pak Pakistan Official JazzCash",
                    jazzcashNumber = "0300-7861122",
                    paytmTitle = "Pak Pakistan Official Paytm / UPI",
                    paytmNumber = "pakpakistan@paytm",
                    supportEmail = "support@pakpakistantournament.com",
                    supportWhatsapp = "+92 300 7861122"
                )
            )
        }
    }
}
