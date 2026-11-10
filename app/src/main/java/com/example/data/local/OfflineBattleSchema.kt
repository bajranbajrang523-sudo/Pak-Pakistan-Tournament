package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/*
 * ============================================================================
 * PRO GENIUS BATTLE - OFFLINE ROOM DATABASE SCHEMA
 * Manages tournament data, match rooms, and user profiles locally with offline support.
 * Covers:
 * - Tournament System (Solo/Duo/Squad, Daily/Weekly/Monthly, Free/Paid, Waiting List, Cancellation)
 * - Match Room System (Room ID/Pass, Admin Release Control, Countdown Timers, Status)
 * - User Profiles & Statistics (Matches, Wins, Kills, Points, Best Performance, Seasons)
 * - Leaderboard & Audit Logs (Correction Audit Logs, Seasons)
 * - Coupons, Support Tickets, Notifications, and Multi-Admin Permissions
 * ============================================================================
 */

// 1. User Profile Entity (Offline profile & career telemetry)
@Entity(tableName = "offline_user_profiles")
data class UserProfileData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val ffName: String,
    val ffUid: String,
    val email: String,
    val passwordHash: String,
    val referralCode: String,
    val referredByCode: String = "",
    val role: String = "PLAYER", // "PLAYER", "SUPER_ADMIN", "MATCH_MANAGER"
    val coins: Int = 100,
    val totalMatches: Int = 0,
    val matchesWon: Int = 0,
    val totalKills: Int = 0,
    val totalPoints: Int = 0,
    val winPercentage: Float = 0f,
    val bestPerformance: String = "0 Kills - Placement #--",
    val recentPerformance: String = "No recent matches",
    val playerRanking: Int = 0,
    val activeSeasonId: Int = 1,
    val isEmailVerified: Boolean = true,
    val isBlocked: Boolean = false,
    val isChatBlocked: Boolean = false,
    val lastLoginAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

// 2. Tournament Data Entity (Daily, Weekly, Monthly, Free/Paid, Deadlines, Waiting List)
@Entity(tableName = "offline_tournaments")
data class TournamentData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val map: String = "Bermuda", // "Bermuda", "Purgatory", "Kalahari", "Alpine"
    val matchType: String = "Solo", // "Solo", "Duo", "Squad"
    val frequency: String = "DAILY", // "DAILY", "WEEKLY", "MONTHLY"
    val isFree: Boolean = false,
    val entryFee: Int = 25,
    val prizePool: Int = 650,
    val perKillReward: Int = 8,
    val maxPlayers: Int = 48,
    val joinedCount: Int = 0,
    val waitingListLimit: Int = 10,
    val waitingListCount: Int = 0,
    val registrationDeadline: Long = System.currentTimeMillis() + 86400000L,
    val matchStartTimeMillis: Long = System.currentTimeMillis() + 90000000L,
    val matchTimeDisplay: String = "Tonight 09:30 PM",
    val status: String = "UPCOMING", // "UPCOMING", "REGISTRATION_CLOSED", "ONGOING", "COMPLETED", "CANCELLED"
    val cancellationReason: String = "",
    val seasonId: Int = 1,
    val description: String = "Free Fire Battle Royale Tournament. Fair play rules strictly enforced.",
    val rulesAcceptedRequired: Boolean = true,
    val winnerName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// 3. Match Room Entity (Room ID & Password, Release timer, Live status)
@Entity(tableName = "offline_match_rooms")
data class MatchRoomData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val roomId: String = "",
    val roomPassword: String = "",
    val releaseTimeMillis: Long = 0L, // Time when credentials become visible to players
    val isReleasedByAdmin: Boolean = false,
    val matchStartTimeMillis: Long = 0L,
    val matchCountdownSeconds: Long = 900L, // 15 mins default
    val isMatchStartingNotified: Boolean = false,
    val status: String = "PREPARING", // "PREPARING", "ROOM_READY", "LIVE", "COMPLETED"
    val winnerName: String = "",
    val winnerUid: String = "",
    val adminNotes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

// 4. Tournament Registration Record (With waiting list support)
@Entity(tableName = "offline_tournament_registrations")
data class TournamentRegistrationData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tournamentId: Long,
    val userId: Long,
    val userName: String,
    val ffName: String,
    val ffUid: String,
    val teamName: String = "",
    val isWaitingList: Boolean = false,
    val isConfirmed: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis(),
    val kills: Int = 0,
    val rank: Int = 0,
    val points: Int = 0,
    val rewardCoins: Int = 0
)

// 5. Coupon System Entity (Promo codes, discounts, free entry)
@Entity(tableName = "offline_coupons")
data class CouponData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val discountType: String = "FREE_ENTRY", // "FREE_ENTRY", "PERCENTAGE", "FIXED_COINS"
    val discountValue: Int = 100, // e.g. 100% or 20 coins
    val maxUses: Int = 100,
    val usedCount: Int = 0,
    val expiryDate: Long = System.currentTimeMillis() + (7L * 86400000L),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

// 6. Support Ticket Entity (Report problem, Payment/Match/Account complaints)
@Entity(tableName = "offline_support_tickets")
data class SupportTicketData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val category: String = "PAYMENT", // "PAYMENT", "MATCH", "ACCOUNT", "OTHER"
    val subject: String,
    val description: String,
    val status: String = "OPEN", // "OPEN", "IN_REVIEW", "RESOLVED", "CLOSED"
    val adminReply: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// 7. Notification Item Entity (In-app notification center)
@Entity(tableName = "offline_notifications")
data class NotificationData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // 0 for broadcast to all users
    val title: String,
    val message: String,
    val type: String = "TOURNAMENT", // "TOURNAMENT", "MATCH_START", "PAYMENT_APPROVED", "PAYMENT_REJECTED", "REWARD", "MAINTENANCE", "ANNOUNCEMENT"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// 8. Result Correction & Admin Audit Log
@Entity(tableName = "offline_audit_logs")
data class AuditLogData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminName: String,
    val adminRole: String, // "SUPER_ADMIN" or "MATCH_MANAGER"
    val action: String, // "CORRECT_RESULT", "CANCEL_TOURNAMENT", "ADJUST_COINS", "RELEASE_ROOM", "CREATE_COUPON"
    val targetEntity: String,
    val targetId: Long,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

// 9. Season Entity (Season 1, Season 2, rewards, dates)
@Entity(tableName = "offline_seasons")
data class SeasonData(
    @PrimaryKey val seasonNumber: Int = 1,
    val title: String = "SEASON 1: RISE OF CHAMPIONS",
    val startDate: Long = System.currentTimeMillis() - (7L * 86400000L),
    val endDate: Long = System.currentTimeMillis() + (23L * 86400000L),
    val isActive: Boolean = true,
    val totalPrizePool: Int = 50000,
    val rulesSummary: String = "Play daily and weekly cups to earn season ranking points. Top 10 warriors win exclusive rewards."
)

// 10. Multi-Admin Permissions Entity
@Entity(tableName = "offline_admins")
data class AdminProfileData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "Abdullah", "Hamza"
    val email: String,
    val passwordHash: String,
    val role: String, // "SUPER_ADMIN" (Abdullah) or "MATCH_MANAGER" (Hamza)
    val canManageTournaments: Boolean = true,
    val canManagePayments: Boolean = false, // Only Super Admin
    val canManageAdmins: Boolean = false, // Only Super Admin
    val canManageSettings: Boolean = false, // Only Super Admin
    val canAuditResults: Boolean = true,
    val isActive: Boolean = true
)

/*
 * ============================================================================
 * DATA ACCESS OBJECTS (DAOs)
 * ============================================================================
 */

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM offline_user_profiles WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserProfileData?>

    @Query("SELECT * FROM offline_user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): UserProfileData?

    @Query("SELECT * FROM offline_user_profiles WHERE email = :email OR ffName = :identifier LIMIT 1")
    suspend fun getUserByEmailOrFfName(identifier: String, email: String): UserProfileData?

    @Query("SELECT * FROM offline_user_profiles WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserProfileData?

    @Query("SELECT * FROM offline_user_profiles WHERE role = 'PLAYER' ORDER BY matchesWon DESC, totalKills DESC, totalPoints DESC LIMIT 50")
    fun getLeaderboardAllTime(): Flow<List<UserProfileData>>

    @Query("SELECT * FROM offline_user_profiles WHERE role = 'PLAYER' ORDER BY totalKills DESC LIMIT 50")
    fun getLeaderboardMostKills(): Flow<List<UserProfileData>>

    @Query("SELECT * FROM offline_user_profiles WHERE role = 'PLAYER' ORDER BY matchesWon DESC LIMIT 50")
    fun getLeaderboardMostWins(): Flow<List<UserProfileData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfileData): Long

    @Update
    suspend fun updateUser(user: UserProfileData)

    @Query("UPDATE offline_user_profiles SET coins = coins + :delta WHERE id = :userId")
    suspend fun updateCoins(userId: Long, delta: Int)

    @Query("UPDATE offline_user_profiles SET isBlocked = :blocked WHERE id = :userId")
    suspend fun setBlockedStatus(userId: Long, blocked: Boolean)

    @Query("UPDATE offline_user_profiles SET isChatBlocked = :chatBlocked WHERE id = :userId")
    suspend fun setChatBlockedStatus(userId: Long, chatBlocked: Boolean)

    @Query("UPDATE offline_user_profiles SET passwordHash = :newPass WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newPass: String)

    @Query("UPDATE offline_user_profiles SET fullName = :fullName, ffName = :ffName, ffUid = :ffUid WHERE id = :userId")
    suspend fun updateProfile(userId: Long, fullName: String, ffName: String, ffUid: String)

    @Query("UPDATE offline_user_profiles SET totalMatches = totalMatches + 1, matchesWon = matchesWon + :wonInc, totalKills = totalKills + :killsInc, totalPoints = totalPoints + :pointsInc, coins = coins + :rewardCoins WHERE id = :userId")
    suspend fun recordStats(userId: Long, wonInc: Int, killsInc: Int, pointsInc: Int, rewardCoins: Int)
}

@Dao
interface TournamentDataDao {
    @Query("SELECT * FROM offline_tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<TournamentData>>

    @Query("SELECT * FROM offline_tournaments WHERE frequency = :frequency ORDER BY id DESC")
    fun getTournamentsByFrequency(frequency: String): Flow<List<TournamentData>>

    @Query("SELECT * FROM offline_tournaments WHERE id = :id LIMIT 1")
    fun getTournamentById(id: Long): Flow<TournamentData?>

    @Query("SELECT * FROM offline_tournaments WHERE id = :id LIMIT 1")
    suspend fun getTournamentByIdSync(id: Long): TournamentData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentData): Long

    @Update
    suspend fun updateTournament(tournament: TournamentData)

    @Query("DELETE FROM offline_tournaments WHERE id = :id")
    suspend fun deleteTournament(id: Long)

    @Query("UPDATE offline_tournaments SET status = 'CANCELLED', cancellationReason = :reason WHERE id = :id")
    suspend fun cancelTournament(id: Long, reason: String)

    @Query("UPDATE offline_tournaments SET joinedCount = joinedCount + 1 WHERE id = :id")
    suspend fun incrementJoinedCount(id: Long)

    @Query("UPDATE offline_tournaments SET waitingListCount = waitingListCount + 1 WHERE id = :id")
    suspend fun incrementWaitingCount(id: Long)

    @Query("UPDATE offline_tournaments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}

@Dao
interface MatchRoomDao {
    @Query("SELECT * FROM offline_match_rooms WHERE tournamentId = :tournamentId LIMIT 1")
    fun getRoomForTournament(tournamentId: Long): Flow<MatchRoomData?>

    @Query("SELECT * FROM offline_match_rooms WHERE tournamentId = :tournamentId LIMIT 1")
    suspend fun getRoomForTournamentSync(tournamentId: Long): MatchRoomData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: MatchRoomData): Long

    @Update
    suspend fun updateRoom(room: MatchRoomData)

    @Query("UPDATE offline_match_rooms SET roomId = :roomId, roomPassword = :password, isReleasedByAdmin = :released, updatedAt = :time WHERE tournamentId = :tournamentId")
    suspend fun updateRoomCredentials(tournamentId: Long, roomId: String, password: String, released: Boolean, time: Long)

    @Query("UPDATE offline_match_rooms SET status = :status WHERE tournamentId = :tournamentId")
    suspend fun updateRoomStatus(tournamentId: Long, status: String)
}

@Dao
interface TournamentRegistrationDao {
    @Query("SELECT * FROM offline_tournament_registrations WHERE tournamentId = :tournamentId ORDER BY isWaitingList ASC, joinedAt ASC")
    fun getRegistrationsForTournament(tournamentId: Long): Flow<List<TournamentRegistrationData>>

    @Query("SELECT * FROM offline_tournament_registrations WHERE tournamentId = :tournamentId ORDER BY isWaitingList ASC, joinedAt ASC")
    suspend fun getRegistrationsForTournamentSync(tournamentId: Long): List<TournamentRegistrationData>

    @Query("SELECT * FROM offline_tournament_registrations WHERE userId = :userId ORDER BY id DESC")
    fun getRegistrationsForUser(userId: Long): Flow<List<TournamentRegistrationData>>

    @Query("SELECT * FROM offline_tournament_registrations WHERE tournamentId = :tournamentId AND userId = :userId LIMIT 1")
    suspend fun getRegistration(tournamentId: Long, userId: Long): TournamentRegistrationData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: TournamentRegistrationData): Long

    @Query("UPDATE offline_tournament_registrations SET kills = :kills, rank = :rank, points = :points, rewardCoins = :rewardCoins WHERE id = :id")
    suspend fun recordResult(id: Long, kills: Int, rank: Int, points: Int, rewardCoins: Int)

    @Query("DELETE FROM offline_tournament_registrations WHERE tournamentId = :tournamentId AND userId = :userId")
    suspend fun deleteRegistration(tournamentId: Long, userId: Long)
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM offline_coupons WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveCoupons(): Flow<List<CouponData>>

    @Query("SELECT * FROM offline_coupons WHERE code = :code AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponData): Long

    @Query("UPDATE offline_coupons SET usedCount = usedCount + 1 WHERE id = :id")
    suspend fun incrementCouponUsage(id: Long)

    @Query("DELETE FROM offline_coupons WHERE id = :id")
    suspend fun deleteCoupon(id: Long)
}

@Dao
interface SupportTicketDao {
    @Query("SELECT * FROM offline_support_tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<SupportTicketData>>

    @Query("SELECT * FROM offline_support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTicketsForUser(userId: Long): Flow<List<SupportTicketData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketData): Long

    @Query("UPDATE offline_support_tickets SET status = :status, adminReply = :reply, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTicketReply(id: Long, status: String, reply: String, updatedAt: Long)
}

@Dao
interface NotificationItemDao {
    @Query("SELECT * FROM offline_notifications WHERE userId = :userId OR userId = 0 ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationData): Long

    @Query("UPDATE offline_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE offline_notifications SET isRead = 1 WHERE userId = :userId OR userId = 0")
    suspend fun markAllAsRead(userId: Long)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM offline_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogData): Long
}

@Dao
interface SeasonDao {
    @Query("SELECT * FROM offline_seasons WHERE isActive = 1 LIMIT 1")
    fun getActiveSeason(): Flow<SeasonData?>

    @Query("SELECT * FROM offline_seasons ORDER BY seasonNumber DESC")
    fun getAllSeasons(): Flow<List<SeasonData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: SeasonData)
}

@Dao
interface AdminUserDao {
    @Query("SELECT * FROM offline_admins WHERE email = :email LIMIT 1")
    suspend fun getAdminByEmail(email: String): AdminProfileData?

    @Query("SELECT * FROM offline_admins ORDER BY id ASC")
    fun getAllAdmins(): Flow<List<AdminProfileData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminProfileData): Long

    @Query("UPDATE offline_admins SET canManageTournaments = :tournaments, canManagePayments = :payments, canAuditResults = :audit WHERE id = :id")
    suspend fun updatePermissions(id: Long, tournaments: Boolean, payments: Boolean, audit: Boolean)
}

/*
 * ============================================================================
 * OFFLINE ROOM DATABASE HOLDER
 * ============================================================================
 */

@Database(
    entities = [
        UserProfileData::class,
        TournamentData::class,
        MatchRoomData::class,
        TournamentRegistrationData::class,
        CouponData::class,
        SupportTicketData::class,
        NotificationData::class,
        AuditLogData::class,
        SeasonData::class,
        AdminProfileData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OfflineBattleDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun tournamentDataDao(): TournamentDataDao
    abstract fun matchRoomDao(): MatchRoomDao
    abstract fun tournamentRegistrationDao(): TournamentRegistrationDao
    abstract fun couponDao(): CouponDao
    abstract fun supportTicketDao(): SupportTicketDao
    abstract fun notificationItemDao(): NotificationItemDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun seasonDao(): SeasonDao
    abstract fun adminUserDao(): AdminUserDao

    companion object {
        @Volatile
        private var INSTANCE: OfflineBattleDatabase? = null

        fun getInstance(context: Context): OfflineBattleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OfflineBattleDatabase::class.java,
                    "pro_genius_offline_battle.db"
                )
                    .addCallback(OfflineDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class OfflineDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedOfflineData(database)
                }
            }
        }

        private suspend fun seedOfflineData(database: OfflineBattleDatabase) {
            val adminDao = database.adminUserDao()
            val seasonDao = database.seasonDao()
            val couponDao = database.couponDao()
            val tournamentDao = database.tournamentDataDao()
            val roomDao = database.matchRoomDao()
            val userDao = database.userProfileDao()
            val notificationDao = database.notificationItemDao()

            // 1. Multi-Admin Setup as required:
            // Abdullah: Super Admin with full permissions
            adminDao.insertAdmin(
                AdminProfileData(
                    name = "Abdullah",
                    email = "abdullah@progeniusbattle.com",
                    passwordHash = "abdullahSuper123",
                    role = "SUPER_ADMIN",
                    canManageTournaments = true,
                    canManagePayments = true,
                    canManageAdmins = true,
                    canManageSettings = true,
                    canAuditResults = true
                )
            )

            // Hamza: Match Manager with match-related permissions only
            adminDao.insertAdmin(
                AdminProfileData(
                    name = "Hamza",
                    email = "hamza@progeniusbattle.com",
                    passwordHash = "hamzaManager123",
                    role = "MATCH_MANAGER",
                    canManageTournaments = true,
                    canManagePayments = false,
                    canManageAdmins = false,
                    canManageSettings = false,
                    canAuditResults = true
                )
            )

            // 2. Active Season 1
            seasonDao.insertSeason(
                SeasonData(
                    seasonNumber = 1,
                    title = "SEASON 1: RISE OF FREE FIRE CHAMPIONS",
                    totalPrizePool = 100000
                )
            )

            // 3. Promotional & Discount Coupons
            couponDao.insertCoupon(
                CouponData(
                    code = "FREEBATTLE",
                    discountType = "FREE_ENTRY",
                    discountValue = 100,
                    maxUses = 500
                )
            )
            couponDao.insertCoupon(
                CouponData(
                    code = "PROGENIUS50",
                    discountType = "PERCENTAGE",
                    discountValue = 50,
                    maxUses = 1000
                )
            )

            // 4. Daily, Weekly, and Monthly Tournaments
            val t1 = tournamentDao.insertTournament(
                TournamentData(
                    title = "DAILY BERMUDA SOLO RUSH #101",
                    map = "Bermuda",
                    matchType = "Solo",
                    frequency = "DAILY",
                    isFree = false,
                    entryFee = 20,
                    prizePool = 550,
                    perKillReward = 6,
                    maxPlayers = 48,
                    joinedCount = 38,
                    waitingListLimit = 10,
                    matchTimeDisplay = "Today 09:30 PM"
                )
            )
            roomDao.insertRoom(
                MatchRoomData(
                    tournamentId = t1,
                    roomId = "889921",
                    roomPassword = "pro88battle",
                    isReleasedByAdmin = true,
                    status = "ROOM_READY"
                )
            )

            val t2 = tournamentDao.insertTournament(
                TournamentData(
                    title = "WEEKLY PURGATORY SQUAD ROYALE #14",
                    map = "Purgatory",
                    matchType = "Squad",
                    frequency = "WEEKLY",
                    isFree = false,
                    entryFee = 80,
                    prizePool = 2500,
                    perKillReward = 20,
                    maxPlayers = 12,
                    joinedCount = 9,
                    matchTimeDisplay = "Sunday 08:00 PM"
                )
            )
            roomDao.insertRoom(
                MatchRoomData(
                    tournamentId = t2,
                    roomId = "993310",
                    roomPassword = "squadbattle",
                    isReleasedByAdmin = false,
                    status = "PREPARING"
                )
            )

            val t3 = tournamentDao.insertTournament(
                TournamentData(
                    title = "MONTHLY FREE FIRE GRAND MASTERS CUP",
                    map = "Kalahari",
                    matchType = "Duo",
                    frequency = "MONTHLY",
                    isFree = true,
                    entryFee = 0,
                    prizePool = 5000,
                    perKillReward = 25,
                    maxPlayers = 24,
                    joinedCount = 24,
                    waitingListCount = 4,
                    matchTimeDisplay = "End of Month"
                )
            )
            roomDao.insertRoom(
                MatchRoomData(
                    tournamentId = t3,
                    roomId = "",
                    roomPassword = "",
                    isReleasedByAdmin = false,
                    status = "PREPARING"
                )
            )

            // 5. Seed Initial User Profiles for Leaderboard
            userDao.insertUser(
                UserProfileData(
                    fullName = "Zubair Khan",
                    ffName = "CyberViper",
                    ffUid = "729104821",
                    email = "viper@gmail.com",
                    passwordHash = "player123",
                    referralCode = "VIPER77",
                    coins = 4200,
                    totalMatches = 38,
                    matchesWon = 22,
                    totalKills = 114,
                    totalPoints = 890,
                    winPercentage = 57.8f,
                    bestPerformance = "14 Kills - Rank #1 (Bermuda Solo)"
                )
            )
            userDao.insertUser(
                UserProfileData(
                    fullName = "Hamza Ali",
                    ffName = "ShadowHunter",
                    ffUid = "810293847",
                    email = "shadow@gmail.com",
                    passwordHash = "player123",
                    referralCode = "SHADOW99",
                    coins = 2800,
                    totalMatches = 31,
                    matchesWon = 17,
                    totalKills = 89,
                    totalPoints = 680,
                    winPercentage = 54.8f,
                    bestPerformance = "11 Kills - Rank #1 (Squad Cup)"
                )
            )

            // 6. Broadcast Welcome Notification
            notificationDao.insertNotification(
                NotificationData(
                    userId = 0,
                    title = "OFFLINE DATABASE & ROOM READY",
                    message = "Offline database schema active. Room details and player telemetry are securely synced.",
                    type = "ANNOUNCEMENT"
                )
            )
        }
    }
}
