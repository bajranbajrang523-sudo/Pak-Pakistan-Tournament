package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE (email = :identifier OR ffName = :identifier) LIMIT 1")
    suspend fun getUserByEmailOrFfName(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE ffUid = :ffUid LIMIT 1")
    suspend fun getUserByFfUid(ffUid: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'PLAYER' ORDER BY id DESC")
    fun getAllPlayers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'PLAYER' ORDER BY matchesWon DESC, totalKills DESC, coins DESC LIMIT 50")
    fun getLeaderboard(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET coins = coins + :delta WHERE id = :userId")
    suspend fun addCoins(userId: Long, delta: Int)

    @Query("UPDATE users SET coins = :coins WHERE id = :userId")
    suspend fun setCoins(userId: Long, coins: Int)

    @Query("UPDATE users SET isBlocked = :blocked WHERE id = :userId")
    suspend fun setBlocked(userId: Long, blocked: Boolean)

    @Query("UPDATE users SET isChatBlocked = :chatBlocked WHERE id = :userId")
    suspend fun setChatBlocked(userId: Long, chatBlocked: Boolean)

    @Query("UPDATE users SET totalMatches = totalMatches + 1, matchesWon = matchesWon + :wonInc, totalKills = totalKills + :killsInc, coins = coins + :rewardCoins WHERE id = :userId")
    suspend fun recordMatchStats(userId: Long, wonInc: Int, killsInc: Int, rewardCoins: Int)

    @Query("SELECT * FROM users WHERE role != 'PLAYER' ORDER BY id ASC")
    fun getAllAdmins(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    @Query("UPDATE users SET isWalletFrozen = :frozen WHERE id = :userId")
    suspend fun setWalletFrozen(userId: Long, frozen: Boolean)

    @Query("UPDATE users SET isUidVerified = :verified WHERE id = :userId")
    suspend fun setUidVerified(userId: Long, verified: Boolean)

    @Query("UPDATE users SET isDeactivated = :deactivated WHERE id = :userId")
    suspend fun setDeactivated(userId: Long, deactivated: Boolean)

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun resetPassword(userId: Long, passwordHash: String)

    @Query("UPDATE users SET role = :role, adminPermissions = :permissions WHERE id = :userId")
    suspend fun updateAdminPermissions(userId: Long, role: String, permissions: String)
}

@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE status = :status ORDER BY id DESC")
    fun getTournamentsByStatus(status: String): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    fun getTournamentById(id: Long): Flow<TournamentEntity?>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    suspend fun getTournamentByIdSync(id: Long): TournamentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity): Long

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    @Query("DELETE FROM tournaments WHERE id = :id")
    suspend fun deleteTournament(id: Long)

    @Query("UPDATE tournaments SET roomId = :roomId, roomPassword = :password WHERE id = :id")
    suspend fun updateRoomDetails(id: Long, roomId: String, password: String)

    @Query("UPDATE tournaments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE tournaments SET joinedCount = joinedCount + 1 WHERE id = :id")
    suspend fun incrementJoinedCount(id: Long)

    @Query("UPDATE tournaments SET status = 'COMPLETED', winnerName = :winnerName WHERE id = :id")
    suspend fun completeTournament(id: Long, winnerName: String)
}

@Dao
interface RegistrationDao {
    @Query("SELECT * FROM tournament_registrations WHERE tournamentId = :tournamentId ORDER BY id ASC")
    fun getRegistrationsForTournament(tournamentId: Long): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM tournament_registrations WHERE tournamentId = :tournamentId ORDER BY id ASC")
    suspend fun getRegistrationsForTournamentSync(tournamentId: Long): List<RegistrationEntity>

    @Query("SELECT * FROM tournament_registrations WHERE userId = :userId ORDER BY id DESC")
    fun getRegistrationsForUser(userId: Long): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM tournament_registrations WHERE tournamentId = :tournamentId AND userId = :userId LIMIT 1")
    suspend fun getRegistration(tournamentId: Long, userId: Long): RegistrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: RegistrationEntity): Long

    @Query("UPDATE tournament_registrations SET kills = :kills, rank = :rank, rewardCoins = :rewardCoins WHERE id = :regId")
    suspend fun updateResult(regId: Long, kills: Int, rank: Int, rewardCoins: Int)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY submittedAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getPaymentsForUser(userId: Long): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query("UPDATE payments SET status = :status, reviewedAt = :reviewedAt, adminRemarks = :remarks WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, status: String, reviewedAt: Long, remarks: String)

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentById(id: Long): PaymentEntity?
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC LIMIT :limit")
    fun getRecentMessages(limit: Int = 100): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)
}

@Dao
interface CoinTransactionDao {
    @Query("SELECT * FROM coin_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: Long): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity): Long
}

@Dao
interface SliderImageDao {
    @Query("SELECT * FROM slider_images ORDER BY orderIndex ASC")
    fun getAllSliders(): Flow<List<SliderImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlider(slider: SliderImageEntity): Long

    @Update
    suspend fun updateSlider(slider: SliderImageEntity)

    @Query("DELETE FROM slider_images WHERE id = :id")
    suspend fun deleteSlider(id: Long)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettingsEntity)
}

@Dao
interface AdminLogDao {
    @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<AdminLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AdminLogEntity): Long
}

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams ORDER BY id DESC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity): Long

    @Update
    suspend fun updateTeam(team: TeamEntity)

    @Query("DELETE FROM teams WHERE id = :id")
    suspend fun deleteTeam(id: Long)

    @Query("UPDATE teams SET isApproved = :approved WHERE id = :id")
    suspend fun updateApproval(id: Long, approved: Boolean)

    @Query("UPDATE teams SET captainId = :captainId, captainName = :captainName, captainUid = :captainUid WHERE id = :teamId")
    suspend fun updateCaptain(teamId: Long, captainId: Long, captainName: String, captainUid: String)
}
