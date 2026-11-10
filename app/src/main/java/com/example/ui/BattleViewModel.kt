package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SessionManager
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
import com.example.data.repository.BattleRepository
import com.example.data.repository.MatchResultInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object ForgotPassword : Screen()
    data class PlayerDashboard(val tab: PlayerTab = PlayerTab.HOME) : Screen()
    object Announcements : Screen()
    object GroupChat : Screen()
    object Profile : Screen()
    object MatchHistory : Screen()
    object Rules : Screen()
    object Support : Screen()
    object AboutUs : Screen()
    object PrivacyPolicy : Screen()
    object TermsConditions : Screen()
    object TeamRegistration : Screen()
    data class AdminDashboard(val tab: AdminTab = AdminTab.OVERVIEW) : Screen()
}

enum class PlayerTab {
    HOME, MATCHES, MY_TOURNAMENTS, PAYMENTS, LEADERBOARD
}

enum class AdminTab {
    OVERVIEW, PLAYERS, TOURNAMENTS, MATCHES, PAYMENTS, RESULTS, LEADERBOARD, ADMIN_ROOM, TEAMS, ANNOUNCEMENTS, CHAT, SLIDERS, REPORTS, SETTINGS
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class BattleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = BattleRepository(db)
    private val sessionManager = SessionManager(application)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _snackBarEvent = MutableSharedFlow<String>()
    val snackBarEvent: SharedFlow<String> = _snackBarEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Reactive data flows
    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { uid ->
        if (uid != null) repository.observeUser(uid) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTournaments: StateFlow<List<TournamentEntity>> = repository.observeAllTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myRegistrations: StateFlow<List<RegistrationEntity>> = _currentUserId.flatMapLatest { uid ->
        if (uid != null) repository.observeRegistrationsForUser(uid) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<UserEntity>> = repository.observeLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.observeAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.observeChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myPayments: StateFlow<List<PaymentEntity>> = _currentUserId.flatMapLatest { uid ->
        if (uid != null) repository.observeUserPayments(uid) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.observeAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlayers: StateFlow<List<UserEntity>> = repository.observeAllPlayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myTransactions: StateFlow<List<CoinTransactionEntity>> = _currentUserId.flatMapLatest { uid ->
        if (uid != null) repository.observeUserTransactions(uid) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sliderImages: StateFlow<List<SliderImageEntity>> = repository.observeSliders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettingsEntity?> = repository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allAdmins: StateFlow<List<UserEntity>> = repository.observeAllAdmins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminLogs: StateFlow<List<AdminLogEntity>> = repository.observeAdminLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeams: StateFlow<List<TeamEntity>> = repository.observeAllTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Check for returning user session
        val savedUserId = sessionManager.getLoggedUserId()
        val savedUserRole = sessionManager.getLoggedUserRole()
        if (savedUserId != null) {
            _currentUserId.value = savedUserId
            if (isAdminRole(savedUserRole)) {
                _currentScreen.value = Screen.AdminDashboard()
            } else {
                _currentScreen.value = Screen.PlayerDashboard()
            }
        }
    }

    fun isAdminRole(role: String?): Boolean {
        return role in listOf("ADMIN", "SUPER_ADMIN", "MAIN_ADMIN", "MATCH_ADMIN", "PLAYER_ADMIN", "PAYMENT_ADMIN")
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _snackBarEvent.emit(message)
        }
    }

    // --- Authentication ---
    fun login(identifier: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(identifier, pass)
            _isLoading.value = false
            result.onSuccess { user ->
                _currentUserId.value = user.id
                sessionManager.saveSession(user.id, user.role)
                if (isAdminRole(user.role)) {
                    _currentScreen.value = Screen.AdminDashboard()
                    val title = if (user.role == "MATCH_ADMIN") "Match Admin (Hamza)" else "Main Admin (Abdullah)"
                    showToast("🇵🇰 Welcome $title ${user.fullName}!")
                } else {
                    _currentScreen.value = Screen.PlayerDashboard()
                    showToast("Welcome back, ${user.ffName}!")
                }
            }.onFailure { error ->
                showToast(error.localizedMessage ?: "Login failed")
            }
        }
    }

    fun register(
        fullName: String,
        ffName: String,
        ffUid: String,
        email: String,
        pass: String,
        referral: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(fullName, ffName, ffUid, email, pass, referral)
            _isLoading.value = false
            result.onSuccess { user ->
                _currentUserId.value = user.id
                sessionManager.saveSession(user.id, user.role)
                _currentScreen.value = Screen.PlayerDashboard()
                showToast("Welcome ${user.ffName}! 100 Bonus Coins credited to your wallet.")
            }.onFailure { error ->
                showToast(error.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun forgotPassword(emailOrUid: String, newPass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.forgotPassword(emailOrUid, newPass)
            _isLoading.value = false
            result.onSuccess { msg ->
                showToast(msg)
                _currentScreen.value = Screen.Login
            }.onFailure { error ->
                showToast(error.localizedMessage ?: "Failed to reset password")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _currentUserId.value = null
        _currentScreen.value = Screen.Login
        showToast("Logged out successfully")
    }

    fun updateProfile(fullName: String, ffName: String, ffUid: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val res = repository.updateProfile(uid, fullName, ffName, ffUid)
            res.onSuccess {
                showToast("Profile updated successfully")
            }.onFailure {
                showToast(it.localizedMessage ?: "Update failed")
            }
        }
    }

    fun adminEditPlayerProfile(userId: Long, fullName: String, ffName: String, ffUid: String) {
        viewModelScope.launch {
            val res = repository.updateProfile(userId, fullName, ffName, ffUid)
            res.onSuccess {
                showToast("Player profile updated by Admin")
            }.onFailure {
                showToast(it.localizedMessage ?: "Failed to update player profile")
            }
        }
    }

    // --- Player Actions ---
    fun joinTournament(tournamentId: Long) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.joinTournament(uid, tournamentId)
            _isLoading.value = false
            result.onSuccess {
                showToast("Successfully joined tournament! Good luck warrior!")
            }.onFailure {
                showToast(it.localizedMessage ?: "Failed to join")
            }
        }
    }

    fun submitPayment(method: String, amount: Double, coins: Int, transactionId: String, note: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.submitPayment(uid, method, amount, coins, transactionId, note)
            _isLoading.value = false
            result.onSuccess {
                showToast("Payment submitted! Admin will verify and credit coins shortly.")
            }.onFailure {
                showToast(it.localizedMessage ?: "Submission failed")
            }
        }
    }

    fun sendChatMessage(message: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.sendChatMessage(uid, message)
            result.onFailure {
                showToast(it.localizedMessage ?: "Message could not be sent")
            }
        }
    }

    // --- Admin Actions ---
    fun adminCreateTournament(
        title: String,
        map: String,
        matchType: String,
        matchTime: String,
        entryFee: Int,
        prizePool: Int,
        perKill: Int,
        maxPlayers: Int,
        description: String
    ) {
        viewModelScope.launch {
            repository.adminCreateTournament(
                TournamentEntity(
                    title = title,
                    map = map,
                    matchType = matchType,
                    matchTime = matchTime,
                    entryFee = entryFee,
                    prizePool = prizePool,
                    perKillReward = perKill,
                    maxPlayers = maxPlayers,
                    description = description,
                    status = "UPCOMING"
                )
            )
            showToast("Tournament '$title' created successfully!")
        }
    }

    fun adminUpdateTournament(tournament: TournamentEntity) {
        viewModelScope.launch {
            repository.adminUpdateTournament(tournament)
            showToast("Tournament updated")
        }
    }

    fun adminDeleteTournament(id: Long) {
        viewModelScope.launch {
            repository.adminDeleteTournament(id)
            showToast("Tournament deleted")
        }
    }

    fun adminUpdateRoomDetails(tournamentId: Long, roomId: String, pass: String) {
        viewModelScope.launch {
            repository.adminUpdateRoomDetails(tournamentId, roomId, pass)
            showToast("Custom Room ID & Password published to players!")
        }
    }

    fun adminUpdateTournamentStatus(tournamentId: Long, status: String) {
        viewModelScope.launch {
            repository.adminUpdateTournamentStatus(tournamentId, status)
            showToast("Match status changed to $status")
        }
    }

    fun adminPublishResults(tournamentId: Long, winnerName: String, results: List<MatchResultInput>) {
        viewModelScope.launch {
            repository.adminPublishMatchResults(tournamentId, winnerName, results)
            showToast("Match completed! Coins and stats distributed to players.")
        }
    }

    fun adminReviewPayment(paymentId: Long, approve: Boolean, remarks: String) {
        viewModelScope.launch {
            repository.adminReviewPayment(paymentId, approve, remarks)
            showToast(if (approve) "Payment approved! Coins credited." else "Payment rejected.")
        }
    }

    fun adminToggleUserBlock(userId: Long, currentBlocked: Boolean) {
        viewModelScope.launch {
            repository.setUserBlocked(userId, !currentBlocked)
            showToast(if (!currentBlocked) "Player account suspended" else "Player account unblocked")
        }
    }

    fun adminToggleUserChatBlock(userId: Long, currentChatBlocked: Boolean) {
        viewModelScope.launch {
            repository.setUserChatBlocked(userId, !currentChatBlocked)
            showToast(if (!currentChatBlocked) "Player restricted from chat" else "Player chat unblocked")
        }
    }

    fun adminAdjustCoins(userId: Long, amount: Int, reason: String) {
        viewModelScope.launch {
            repository.adminAdjustCoins(userId, amount, reason)
            showToast("Coins adjusted ($amount) for player")
        }
    }

    fun adminCreateAnnouncement(title: String, message: String, target: String, priority: String) {
        viewModelScope.launch {
            repository.adminCreateAnnouncement(title, message, target, priority)
            showToast("Announcement published!")
        }
    }

    fun adminDeleteAnnouncement(id: Long) {
        viewModelScope.launch {
            repository.adminDeleteAnnouncement(id)
            showToast("Announcement deleted")
        }
    }

    fun adminDeleteChatMessage(messageId: Long) {
        viewModelScope.launch {
            repository.adminDeleteChatMessage(messageId)
            showToast("Message deleted by Admin")
        }
    }

    fun adminUpdateSettings(settings: AppSettingsEntity) {
        viewModelScope.launch {
            repository.adminUpdateSettings(settings)
            showToast("App settings saved successfully")
        }
    }

    fun adminUpdateSlider(slider: SliderImageEntity) {
        viewModelScope.launch {
            repository.adminUpdateSlider(slider)
            showToast("Slider updated")
        }
    }

    fun adminInsertSlider(title: String, subtitle: String, tag: String, drawableName: String, order: Int) {
        viewModelScope.launch {
            repository.adminInsertSlider(
                SliderImageEntity(
                    title = title,
                    subtitle = subtitle,
                    tag = tag,
                    drawableResName = drawableName,
                    orderIndex = order
                )
            )
            showToast("New slider banner added")
        }
    }

    fun adminDeleteSlider(id: Long) {
        viewModelScope.launch {
            repository.adminDeleteSlider(id)
            showToast("Slider removed")
        }
    }

    suspend fun getTournamentRegistrations(tournamentId: Long): List<RegistrationEntity> {
        return repository.getRegistrationsForTournamentSync(tournamentId)
    }

    fun claimDailyBonus() {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.adminAdjustCoins(uid, 20, "Daily Check-in Bonus")
            showToast("🎉 Daily Check-in Claimed! +20 Coins added to your wallet.")
        }
    }

    fun createTeam(teamName: String, teamTag: String, p2Uid: String, p3Uid: String, p4Uid: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.registerTeam(
                TeamEntity(
                    teamName = teamName.trim(),
                    teamTag = teamTag.trim(),
                    captainId = user.id,
                    captainName = user.ffName,
                    captainUid = user.ffUid,
                    player2Uid = p2Uid.trim(),
                    player3Uid = p3Uid.trim(),
                    player4Uid = p4Uid.trim(),
                    isApproved = true
                )
            )
            showToast("Team '$teamName [$teamTag]' registered successfully! Squad slot reserved.")
            _currentScreen.value = Screen.PlayerDashboard(PlayerTab.MATCHES)
        }
    }

    fun requestWithdrawal(method: String, accountNumber: String, accountTitle: String, amountCoins: Int) {
        val user = currentUser.value ?: return
        if (user.coins < amountCoins) {
            showToast("Insufficient coin balance for withdrawal!")
            return
        }
        viewModelScope.launch {
            val note = "WITHDRAWAL REQUEST | Acct: $accountNumber ($accountTitle)"
            val result = repository.submitPayment(
                userId = user.id,
                method = method,
                amount = amountCoins.toDouble(),
                coins = -amountCoins,
                transactionId = "WD-${System.currentTimeMillis() % 1000000}",
                note = note
            )
            result.onSuccess {
                repository.adminAdjustCoins(user.id, -amountCoins, "Withdrawal Request: $amountCoins Coins to $method")
                showToast("Withdrawal request submitted! Admin Abdullah will approve shortly.")
            }.onFailure {
                showToast(it.localizedMessage ?: "Failed to submit withdrawal request")
            }
        }
    }

    // --- Admin Management Room (Section 19) ---
    fun adminCreateSubAdmin(fullName: String, email: String, pass: String, role: String, permissions: String) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.adminCreateSubAdmin(fullName, email, pass, role, permissions, currentAdmin)
            _isLoading.value = false
            res.onSuccess {
                showToast("Admin account '${it.fullName}' created with role '$role'!")
            }.onFailure {
                showToast(it.localizedMessage ?: "Failed to create admin")
            }
        }
    }

    fun adminDeleteAdmin(adminId: Long, adminEmail: String) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminDeleteAdmin(adminId, adminEmail, currentAdmin)
            showToast("Admin account deleted")
        }
    }

    fun adminToggleDeactivateAdmin(adminId: Long, deactivated: Boolean) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminToggleDeactivateAdmin(adminId, deactivated, currentAdmin)
            showToast(if (deactivated) "Admin account deactivated" else "Admin account activated")
        }
    }

    fun adminResetPassword(userId: Long, newPass: String) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminResetUserPassword(userId, newPass, currentAdmin)
            showToast("Password reset successfully")
        }
    }

    fun adminUpdateAdminPermissions(userId: Long, role: String, permissions: String) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminUpdateAdminPermissions(userId, role, permissions, currentAdmin)
            showToast("Admin permissions updated")
        }
    }

    // --- Player Management Extended ---
    fun adminToggleWalletFreeze(userId: Long, currentFrozen: Boolean) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminToggleWalletFreeze(userId, !currentFrozen, currentAdmin)
            showToast(if (!currentFrozen) "Player wallet frozen" else "Player wallet unfrozen")
        }
    }

    fun adminToggleUidVerification(userId: Long, currentVerified: Boolean) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminToggleUidVerification(userId, !currentVerified, currentAdmin)
            showToast(if (!currentVerified) "Player UID verified" else "Player UID unverified")
        }
    }

    fun adminDeletePlayer(userId: Long) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminDeletePlayer(userId, currentAdmin)
            showToast("Player account deleted")
        }
    }

    // --- Team Management ---
    fun adminApproveTeam(teamId: Long, approved: Boolean) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminApproveTeam(teamId, approved, currentAdmin)
            showToast(if (approved) "Team approved" else "Team rejected")
        }
    }

    fun adminDeleteTeam(teamId: Long) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminDeleteTeam(teamId, currentAdmin)
            showToast("Team deleted")
        }
    }

    fun adminUpdateTeamCaptain(teamId: Long, captainId: Long, captainName: String, captainUid: String) {
        val currentAdmin = currentUser.value?.fullName ?: "Abdullah"
        viewModelScope.launch {
            repository.adminUpdateTeamCaptain(teamId, captainId, captainName, captainUid, currentAdmin)
            showToast("Team captain updated")
        }
    }
}
