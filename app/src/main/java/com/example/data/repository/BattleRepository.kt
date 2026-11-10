package com.example.data.repository

import com.example.data.local.AppDatabase
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

data class MatchResultInput(
    val registrationId: Long,
    val userId: Long,
    val kills: Int,
    val rank: Int,
    val rewardCoins: Int
)

class BattleRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val tournamentDao = db.tournamentDao()
    private val registrationDao = db.registrationDao()
    private val paymentDao = db.paymentDao()
    private val announcementDao = db.announcementDao()
    private val chatDao = db.chatMessageDao()
    private val coinDao = db.coinTransactionDao()
    private val sliderDao = db.sliderImageDao()
    private val settingsDao = db.appSettingsDao()
    private val adminLogDao = db.adminLogDao()
    private val teamDao = db.teamDao()

    // --- Authentication ---
    suspend fun login(identifier: String, password: String):Result<UserEntity> {
        val cleanIdentifier = identifier.trim()
        val user = userDao.getUserByEmailOrFfName(cleanIdentifier)
            ?: return Result.failure(Exception("No account found with this Email or Free Fire Name"))

        if (user.passwordHash != password) {
            return Result.failure(Exception("Incorrect password. Please try again or use Forgot Password."))
        }

        if (user.isBlocked) {
            return Result.failure(Exception("Your account has been suspended by Administrator."))
        }

        return Result.success(user)
    }

    suspend fun register(
        fullName: String,
        ffName: String,
        ffUid: String,
        email: String,
        password: String,
        referralCode: String
    ): Result<UserEntity> {
        if (fullName.isBlank() || ffName.isBlank() || ffUid.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("All fields marked * are required"))
        }

        val existingEmail = userDao.getUserByEmail(email.trim().lowercase())
        if (existingEmail != null) {
            return Result.failure(Exception("An account with this email already exists"))
        }

        val existingUid = userDao.getUserByFfUid(ffUid.trim())
        if (existingUid != null) {
            return Result.failure(Exception("This Free Fire UID is already registered"))
        }

        val newUser = UserEntity(
            fullName = fullName.trim(),
            ffName = ffName.trim(),
            ffUid = ffUid.trim(),
            email = email.trim().lowercase(),
            passwordHash = password,
            referralCode = referralCode.trim(),
            role = "PLAYER",
            coins = 100 // 100 welcome coins bonus
        )

        val insertedId = userDao.insertUser(newUser)
        val createdUser = newUser.copy(id = insertedId)

        // Record signup bonus coin transaction
        coinDao.insertTransaction(
            CoinTransactionEntity(
                userId = insertedId,
                amount = 100,
                type = "SIGNUP_BONUS",
                description = "Welcome bonus for registering on PAK PAKISTAN TOURNAMENT"
            )
        )

        return Result.success(createdUser)
    }

    suspend fun forgotPassword(emailOrUid: String, newPassword: String): Result<String> {
        if (emailOrUid.isBlank() || newPassword.isBlank()) {
            return Result.failure(Exception("Please enter your registered Email/UID and new password"))
        }
        val user = userDao.getUserByEmailOrFfName(emailOrUid.trim())
            ?: userDao.getUserByFfUid(emailOrUid.trim())
            ?: return Result.failure(Exception("No player found with this Email or Free Fire UID"))

        userDao.updateUser(user.copy(passwordHash = newPassword))
        return Result.success("Password reset successfully. Please log in.")
    }

    fun observeUser(userId: Long): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun getUserSync(userId: Long): UserEntity? = userDao.getUserByIdSync(userId)

    suspend fun updateProfile(userId: Long, fullName: String, ffName: String, ffUid: String): Result<Unit> {
        val user = userDao.getUserByIdSync(userId) ?: return Result.failure(Exception("User not found"))
        userDao.updateUser(user.copy(fullName = fullName.trim(), ffName = ffName.trim(), ffUid = ffUid.trim()))
        return Result.success(Unit)
    }

    fun observeAllPlayers(): Flow<List<UserEntity>> = userDao.getAllPlayers()

    fun observeLeaderboard(): Flow<List<UserEntity>> = userDao.getLeaderboard()

    suspend fun setUserBlocked(userId: Long, blocked: Boolean) = userDao.setBlocked(userId, blocked)

    suspend fun setUserChatBlocked(userId: Long, chatBlocked: Boolean) = userDao.setChatBlocked(userId, chatBlocked)

    suspend fun adminAdjustCoins(userId: Long, amount: Int, reason: String) {
        userDao.addCoins(userId, amount)
        coinDao.insertTransaction(
            CoinTransactionEntity(
                userId = userId,
                amount = amount,
                type = "ADMIN_ADJUST",
                description = if (reason.isNotBlank()) "Admin Adjustment: $reason" else "Admin Coin Adjustment"
            )
        )
    }

    // --- Tournaments ---
    fun observeAllTournaments(): Flow<List<TournamentEntity>> = tournamentDao.getAllTournaments()

    fun observeTournament(id: Long): Flow<TournamentEntity?> = tournamentDao.getTournamentById(id)

    fun observeRegistrationsForTournament(tournamentId: Long): Flow<List<RegistrationEntity>> =
        registrationDao.getRegistrationsForTournament(tournamentId)

    suspend fun getRegistrationsForTournamentSync(tournamentId: Long): List<RegistrationEntity> =
        registrationDao.getRegistrationsForTournamentSync(tournamentId)

    fun observeRegistrationsForUser(userId: Long): Flow<List<RegistrationEntity>> =
        registrationDao.getRegistrationsForUser(userId)

    suspend fun joinTournament(userId: Long, tournamentId: Long): Result<Unit> {
        val user = userDao.getUserByIdSync(userId) ?: return Result.failure(Exception("User not found"))
        val tournament = tournamentDao.getTournamentByIdSync(tournamentId)
            ?: return Result.failure(Exception("Tournament not found"))

        if (tournament.status != "UPCOMING") {
            return Result.failure(Exception("Cannot join: tournament is already ${tournament.status.lowercase()}"))
        }

        if (tournament.joinedCount >= tournament.maxPlayers) {
            return Result.failure(Exception("Tournament is completely full"))
        }

        val existing = registrationDao.getRegistration(tournamentId, userId)
        if (existing != null) {
            return Result.failure(Exception("You have already joined this tournament"))
        }

        if (user.coins < tournament.entryFee) {
            return Result.failure(Exception("Insufficient coins! Required: ${tournament.entryFee}, Available: ${user.coins}. Please deposit coins in the Payment section."))
        }

        // Deduct entry coins
        userDao.addCoins(userId, -tournament.entryFee)

        // Record registration
        registrationDao.insertRegistration(
            RegistrationEntity(
                tournamentId = tournamentId,
                userId = userId,
                userName = user.fullName,
                ffName = user.ffName,
                ffUid = user.ffUid
            )
        )

        // Increment tournament player count
        tournamentDao.incrementJoinedCount(tournamentId)

        // Record coin debit
        coinDao.insertTransaction(
            CoinTransactionEntity(
                userId = userId,
                amount = -tournament.entryFee,
                type = "JOIN_FEE",
                description = "Entry fee for ${tournament.title}"
            )
        )

        return Result.success(Unit)
    }

    suspend fun adminCreateTournament(tournament: TournamentEntity): Long =
        tournamentDao.insertTournament(tournament)

    suspend fun adminUpdateTournament(tournament: TournamentEntity) =
        tournamentDao.updateTournament(tournament)

    suspend fun adminDeleteTournament(id: Long) =
        tournamentDao.deleteTournament(id)

    suspend fun adminUpdateRoomDetails(tournamentId: Long, roomId: String, password: String) =
        tournamentDao.updateRoomDetails(tournamentId, roomId, password)

    suspend fun adminUpdateTournamentStatus(tournamentId: Long, status: String) =
        tournamentDao.updateStatus(tournamentId, status)

    suspend fun adminPublishMatchResults(
        tournamentId: Long,
        winnerName: String,
        results: List<MatchResultInput>
    ) {
        for (res in results) {
            registrationDao.updateResult(res.registrationId, res.kills, res.rank, res.rewardCoins)
            val wonInc = if (res.rank == 1) 1 else 0
            userDao.recordMatchStats(res.userId, wonInc, res.kills, res.rewardCoins)

            if (res.rewardCoins > 0) {
                coinDao.insertTransaction(
                    CoinTransactionEntity(
                        userId = res.userId,
                        amount = res.rewardCoins,
                        type = if (wonInc == 1) "WIN_PRIZE" else "KILL_BONUS",
                        description = "Prize reward for Tournament #$tournamentId (Rank: #${res.rank}, Kills: ${res.kills})"
                    )
                )
            }
        }
        tournamentDao.completeTournament(tournamentId, winnerName)
    }

    // --- Payments ---
    fun observeAllPayments(): Flow<List<PaymentEntity>> = paymentDao.getAllPayments()

    fun observeUserPayments(userId: Long): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForUser(userId)

    suspend fun submitPayment(
        userId: Long,
        method: String,
        amount: Double,
        coins: Int,
        transactionId: String,
        note: String
    ): Result<Unit> {
        val user = userDao.getUserByIdSync(userId) ?: return Result.failure(Exception("User not found"))
        if (transactionId.isBlank()) {
            return Result.failure(Exception("Transaction ID / Reference ID is required"))
        }

        paymentDao.insertPayment(
            PaymentEntity(
                userId = userId,
                userName = user.fullName,
                userEmail = user.email,
                method = method,
                amountPaid = amount,
                coinsRequested = coins,
                transactionId = transactionId.trim(),
                receiptNote = note.trim(),
                status = "PENDING"
            )
        )
        return Result.success(Unit)
    }

    suspend fun adminReviewPayment(paymentId: Long, approve: Boolean, remarks: String) {
        val payment = paymentDao.getPaymentById(paymentId) ?: return
        val newStatus = if (approve) "APPROVED" else "REJECTED"
        paymentDao.updatePaymentStatus(
            id = paymentId,
            status = newStatus,
            reviewedAt = System.currentTimeMillis(),
            remarks = remarks
        )

        if (approve) {
            if (payment.coinsRequested < 0) {
                // Withdrawal approved (already deducted at request time)
                coinDao.insertTransaction(
                    CoinTransactionEntity(
                        userId = payment.userId,
                        amount = payment.coinsRequested,
                        type = "WITHDRAW",
                        description = "Withdrawal approved via ${payment.method}: ${payment.receiptNote}"
                    )
                )
            } else {
                // Add coins to user wallet for approved deposit
                userDao.addCoins(payment.userId, payment.coinsRequested)
                coinDao.insertTransaction(
                    CoinTransactionEntity(
                        userId = payment.userId,
                        amount = payment.coinsRequested,
                        type = "DEPOSIT",
                        description = "Payment approved via ${payment.method} (TxID: ${payment.transactionId})"
                    )
                )
            }
        } else {
            // Rejected: If withdrawal, refund the coins back to user wallet
            if (payment.coinsRequested < 0) {
                val refundCoins = -payment.coinsRequested
                userDao.addCoins(payment.userId, refundCoins)
                coinDao.insertTransaction(
                    CoinTransactionEntity(
                        userId = payment.userId,
                        amount = refundCoins,
                        type = "REFUND",
                        description = "Withdrawal rejected by Admin: Refunded $refundCoins Coins"
                    )
                )
            }
        }
    }

    // --- Announcements ---
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    suspend fun adminCreateAnnouncement(title: String, message: String, target: String, priority: String) =
        announcementDao.insertAnnouncement(
            AnnouncementEntity(title = title.trim(), message = message.trim(), targetAudience = target, priority = priority)
        )

    suspend fun adminDeleteAnnouncement(id: Long) = announcementDao.deleteAnnouncement(id)

    // --- Chat Messages ---
    fun observeChatMessages(): Flow<List<ChatMessageEntity>> = chatDao.getRecentMessages()

    suspend fun sendChatMessage(senderId: Long, message: String): Result<Unit> {
        val user = userDao.getUserByIdSync(senderId) ?: return Result.failure(Exception("User not found"))
        if (user.isChatBlocked) {
            return Result.failure(Exception("You have been restricted from group chat by Administrator."))
        }
        if (message.isBlank()) return Result.failure(Exception("Message cannot be empty"))

        chatDao.insertMessage(
            ChatMessageEntity(
                senderId = senderId,
                senderName = if (user.role == "ADMIN") "ADMIN: ${user.fullName}" else user.ffName,
                senderRole = user.role,
                message = message.trim()
            )
        )
        return Result.success(Unit)
    }

    suspend fun adminDeleteChatMessage(messageId: Long) = chatDao.deleteMessage(messageId)

    // --- Coins ---
    fun observeUserTransactions(userId: Long): Flow<List<CoinTransactionEntity>> =
        coinDao.getTransactionsForUser(userId)

    // --- Sliders ---
    fun observeSliders(): Flow<List<SliderImageEntity>> = sliderDao.getAllSliders()

    suspend fun adminUpdateSlider(slider: SliderImageEntity) = sliderDao.updateSlider(slider)

    suspend fun adminInsertSlider(slider: SliderImageEntity) = sliderDao.insertSlider(slider)

    suspend fun adminDeleteSlider(id: Long) = sliderDao.deleteSlider(id)

    // --- App Settings ---
    fun observeSettings(): Flow<AppSettingsEntity?> = settingsDao.getSettings()

    suspend fun adminUpdateSettings(settings: AppSettingsEntity) =
        settingsDao.insertOrUpdateSettings(settings)

    // --- Admin Management Room (Section 19) ---
    fun observeAllAdmins(): Flow<List<UserEntity>> = userDao.getAllAdmins()

    suspend fun adminCreateSubAdmin(
        fullName: String,
        email: String,
        password: String,
        role: String,
        permissions: String,
        createdByName: String
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists"))
        }

        val newAdmin = UserEntity(
            fullName = fullName.trim(),
            ffName = fullName.trim().replace(" ", ""),
            ffUid = "ADM" + System.currentTimeMillis().toString().takeLast(6),
            email = cleanEmail,
            passwordHash = password,
            role = role,
            adminPermissions = permissions,
            coins = 50000
        )
        val id = userDao.insertUser(newAdmin)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = createdByName,
                action = "CREATE_ADMIN",
                details = "Created new admin: ${newAdmin.fullName} ($cleanEmail) with role $role and permissions: $permissions"
            )
        )
        return Result.success(newAdmin.copy(id = id))
    }

    suspend fun adminDeleteAdmin(adminId: Long, adminEmail: String, performedBy: String) {
        userDao.deleteUser(adminId)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "DELETE_ADMIN",
                details = "Deleted admin account $adminEmail (ID #$adminId)"
            )
        )
    }

    suspend fun adminToggleDeactivateAdmin(adminId: Long, deactivated: Boolean, performedBy: String) {
        userDao.setDeactivated(adminId, deactivated)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = if (deactivated) "DEACTIVATE_ADMIN" else "ACTIVATE_ADMIN",
                details = "Set deactivation status of admin #$adminId to $deactivated"
            )
        )
    }

    suspend fun adminResetUserPassword(userId: Long, newPass: String, performedBy: String) {
        userDao.resetPassword(userId, newPass)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "RESET_PASSWORD",
                details = "Reset password for user/admin #$userId"
            )
        )
    }

    suspend fun adminUpdateAdminPermissions(userId: Long, role: String, permissions: String, performedBy: String) {
        userDao.updateAdminPermissions(userId, role, permissions)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "UPDATE_PERMISSIONS",
                details = "Updated admin #$userId permissions to: $permissions ($role)"
            )
        )
    }

    // --- Player Management Extended ---
    suspend fun adminToggleWalletFreeze(userId: Long, frozen: Boolean, performedBy: String) {
        userDao.setWalletFrozen(userId, frozen)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = if (frozen) "FREEZE_WALLET" else "UNFREEZE_WALLET",
                details = "Player #$userId wallet frozen: $frozen"
            )
        )
    }

    suspend fun adminToggleUidVerification(userId: Long, verified: Boolean, performedBy: String) {
        userDao.setUidVerified(userId, verified)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "VERIFY_UID",
                details = "Player #$userId Free Fire UID verified: $verified"
            )
        )
    }

    suspend fun adminDeletePlayer(userId: Long, performedBy: String) {
        userDao.deleteUser(userId)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "DELETE_PLAYER",
                details = "Deleted player record #$userId"
            )
        )
    }

    // --- Admin Logs ---
    fun observeAdminLogs(): Flow<List<AdminLogEntity>> = adminLogDao.getRecentLogs()

    suspend fun logAdminAction(adminName: String, action: String, details: String) {
        adminLogDao.insertLog(AdminLogEntity(adminName = adminName, action = action, details = details))
    }

    // --- Teams Management ---
    fun observeAllTeams(): Flow<List<TeamEntity>> = teamDao.getAllTeams()

    suspend fun adminApproveTeam(teamId: Long, approved: Boolean, performedBy: String) {
        teamDao.updateApproval(teamId, approved)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = if (approved) "APPROVE_TEAM" else "REJECT_TEAM",
                details = "Team #$teamId approval set to $approved"
            )
        )
    }

    suspend fun adminDeleteTeam(teamId: Long, performedBy: String) {
        teamDao.deleteTeam(teamId)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "DELETE_TEAM",
                details = "Deleted squad team #$teamId"
            )
        )
    }

    suspend fun adminUpdateTeamCaptain(teamId: Long, captainId: Long, captainName: String, captainUid: String, performedBy: String) {
        teamDao.updateCaptain(teamId, captainId, captainName, captainUid)
        adminLogDao.insertLog(
            AdminLogEntity(
                adminName = performedBy,
                action = "UPDATE_TEAM_CAPTAIN",
                details = "Team #$teamId captain updated to $captainName ($captainUid)"
            )
        )
    }

    suspend fun registerTeam(team: TeamEntity): Long = teamDao.insertTeam(team)
}
