package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val ffName: String,
    val ffUid: String,
    val email: String,
    val passwordHash: String,
    val referralCode: String = "",
    val role: String = "PLAYER", // "PLAYER", "SUPER_ADMIN", "MATCH_ADMIN", "PLAYER_ADMIN", "PAYMENT_ADMIN", "ADMIN"
    val adminPermissions: String = "ALL", // "ALL", "MATCHES_ONLY", "PLAYERS_ONLY", "PAYMENTS_ONLY", "RESULTS_ONLY"
    val coins: Int = 100, // 100 welcome coins bonus
    val totalMatches: Int = 0,
    val matchesWon: Int = 0,
    val totalKills: Int = 0,
    val isBlocked: Boolean = false,
    val isChatBlocked: Boolean = false,
    val isWalletFrozen: Boolean = false,
    val isUidVerified: Boolean = true,
    val isDeactivated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val map: String = "Bermuda", // "Bermuda", "Purgatory", "Kalahari", "Alpine"
    val matchType: String = "Solo", // "Solo", "Duo", "Squad"
    val status: String = "UPCOMING", // "UPCOMING", "ONGOING", "COMPLETED", "CANCELLED"
    val matchTime: String,
    val entryFee: Int = 20,
    val prizePool: Int = 500,
    val perKillReward: Int = 5,
    val maxPlayers: Int = 48,
    val joinedCount: Int = 0,
    val roomId: String = "",
    val roomPassword: String = "",
    val winnerName: String = "",
    val description: String = "Free Fire Battle Royale match. Room ID and password are released 15 minutes before match start.",
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tournament_registrations")
data class RegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val userId: Long,
    val userName: String,
    val ffName: String,
    val ffUid: String,
    val joinedAt: Long = System.currentTimeMillis(),
    val kills: Int = 0,
    val rank: Int = 0,
    val rewardCoins: Int = 0,
    val slotNumber: Int = 0
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val method: String, // "EasyPaisa", "JazzCash", "Paytm"
    val amountPaid: Double,
    val coinsRequested: Int, // positive for deposit, negative for withdrawal
    val transactionId: String,
    val receiptNote: String = "",
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val adminRemarks: String = ""
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val targetAudience: String = "ALL", // "ALL" or target UID
    val priority: String = "NORMAL", // "HIGH", "NORMAL"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: Long,
    val senderName: String,
    val senderRole: String = "PLAYER", // "ADMIN" or "PLAYER"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val amount: Int, // positive for credit, negative for debit
    val type: String, // "DEPOSIT", "JOIN_FEE", "WIN_PRIZE", "KILL_BONUS", "BONUS", "ADMIN_ADJUST", "WITHDRAW", "REFUND"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "slider_images")
data class SliderImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val tag: String,
    val drawableResName: String,
    val orderIndex: Int = 0
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminName: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teamName: String,
    val teamTag: String,
    val captainId: Long,
    val captainName: String,
    val captainUid: String,
    val player2Name: String = "",
    val player2Uid: String = "",
    val player3Name: String = "",
    val player3Uid: String = "",
    val player4Name: String = "",
    val player4Uid: String = "",
    val isApproved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val appName: String = "PAK PAKISTAN TOURNAMENT",
    val easypaisaTitle: String = "Pak Pakistan Official EasyPaisa",
    val easypaisaNumber: String = "0312-7861122",
    val jazzcashTitle: String = "Pak Pakistan Official JazzCash",
    val jazzcashNumber: String = "0300-7861122",
    val paytmTitle: String = "Pak Pakistan Official Paytm / UPI",
    val paytmNumber: String = "pakpakistan@paytm",
    val supportEmail: String = "support@pakpakistantournament.com",
    val supportWhatsapp: String = "+92 300 7861122",
    val isMaintenanceMode: Boolean = false,
    val rulesText: String = """
1. FAIR PLAY & ANTI-CHEAT:
   - Emulators, hacks, headshot scripts, speed mods, or third-party tools are strictly prohibited.
   - Any player caught cheating will be permanently banned and wallet coins forfeited.

2. ROOM DETAILS:
   - Room ID and Password will be displayed on the tournament details card exactly 15 minutes before the match start time.
   - Do not share room credentials with non-registered players.

3. MATCH INSTRUCTIONS:
   - Enter room within 10 minutes of room ID release.
   - Use the Free Fire Name and UID registered on your PAK PAKISTAN TOURNAMENT profile.
   - In squad/duo matches, your squad must be in your allocated team slot.

4. RESULTS & PRIZES:
   - Match results will be verified by admin after match completion.
   - Prize coins and per-kill bonus will be credited directly to your in-app wallet within 30 minutes.
   - Keep a screenshot of your final kill/rank screen for dispute resolution.

5. PAYMENTS & COINS:
   - 1 Cash Unit = 1 Coin. Coins can be used to join any available tournament.
   - Minimum withdrawal is 100 Coins. Submit valid mobile numbers for disbursement.
    """.trimIndent()
)
