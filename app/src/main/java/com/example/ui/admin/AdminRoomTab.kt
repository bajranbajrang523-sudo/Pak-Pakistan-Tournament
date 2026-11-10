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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminLogEntity
import com.example.data.model.UserEntity
import com.example.ui.BattleViewModel
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberOutlinedButton
import com.example.ui.components.StatusBadge
import com.example.ui.screens.cyberTextFieldColors
import com.example.ui.theme.ElectricPurple
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

@Composable
fun AdminRoomTab(
    viewModel: BattleViewModel,
    admins: List<UserEntity>,
    adminLogs: List<AdminLogEntity>,
    currentUser: UserEntity?
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var adminToEditPermissions by remember { mutableStateOf<UserEntity?>(null) }
    var adminToResetPassword by remember { mutableStateOf<UserEntity?>(null) }
    var adminToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ADMIN MANAGEMENT ROOM",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "Section 19: Full administrative control & role delegation",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = GoldYellow,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Current Super Admin Card (Abdullah)
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = GoldYellow
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoldYellow.copy(alpha = 0.2f))
                                    .border(1.dp, GoldYellow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldYellow,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MAIN SUPER ADMIN: ABDULLAH",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GoldYellow
                                )
                                Text(
                                    text = "abdullah@pakpakistan.com (Full Root Access)",
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                        StatusBadge(text = "SUPER ADMIN", color = GoldYellow)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Abdullah possesses exclusive authority to create sub-admin accounts, delegate tournament/match permissions, reset credentials, and review activity audit logs.",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button to Create Sub-Admin
            CyberButton(
                text = "CREATE NEW ADMIN ACCOUNT",
                onClick = { showCreateDialog = true },
                icon = Icons.Default.Add,
                containerColor = ElectricPurple,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ADMINISTRATIVE ACCOUNTS (${admins.size})",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Admin Accounts List
        items(admins) { admin ->
            val isAbdullah = admin.email.equals("abdullah@pakpakistan.com", ignoreCase = true)
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                borderColor = if (isAbdullah) GoldYellow.copy(alpha = 0.7f) else NeonCyan.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = admin.fullName,
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isAbdullah) GoldYellow else TextPrimary
                                )
                                if (isAbdullah) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Main Admin",
                                        tint = GoldYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${admin.email} • UID: ${admin.ffUid}",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        StatusBadge(
                            text = admin.role.replace("_", " "),
                            color = when (admin.role) {
                                "SUPER_ADMIN" -> GoldYellow
                                "MATCH_ADMIN" -> NeonCyan
                                "PLAYER_ADMIN" -> NeonGreen
                                "PAYMENT_ADMIN" -> ElectricPurple
                                else -> NeonBlue
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Permissions: ${admin.adminPermissions}",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (admin.isDeactivated) "Deactivated" else "Active",
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 11.sp,
                                color = if (admin.isDeactivated) NeonRed else NeonGreen
                            )
                            if (!isAbdullah) {
                                Switch(
                                    checked = !admin.isDeactivated,
                                    onCheckedChange = { active ->
                                        viewModel.adminToggleDeactivateAdmin(admin.id, !active)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonGreen,
                                        checkedTrackColor = NeonGreen.copy(alpha = 0.5f),
                                        uncheckedThumbColor = NeonRed,
                                        uncheckedTrackColor = NeonRed.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CyberOutlinedButton(
                            text = "PERMISSIONS",
                            onClick = { adminToEditPermissions = admin },
                            icon = Icons.Default.Edit,
                            borderColor = NeonCyan,
                            textColor = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                        CyberOutlinedButton(
                            text = "RESET PASS",
                            onClick = { adminToResetPassword = admin },
                            icon = Icons.Default.Key,
                            borderColor = GoldYellow,
                            textColor = GoldYellow,
                            modifier = Modifier.weight(1f)
                        )
                        if (!isAbdullah) {
                            CyberOutlinedButton(
                                text = "DELETE",
                                onClick = { adminToDelete = admin },
                                icon = Icons.Default.Delete,
                                borderColor = NeonRed,
                                textColor = NeonRed,
                                modifier = Modifier.weight(0.9f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "ADMIN ACTION AUDIT TRAIL",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonCyan
            )
            Text(
                text = "Tamper-proof event logs of sub-admin creations, password resets, and changes",
                fontFamily = RajdhaniFontFamily,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(adminLogs.take(20)) { log ->
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                borderColor = NeonCyan.copy(alpha = 0.2f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
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
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = log.details,
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Executed by: ${log.adminName}",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 10.sp,
                        color = NeonCyan
                    )
                }
            }
        }
    }

    // Dialog: Create Sub-Admin
    if (showCreateDialog) {
        CreateAdminDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, email, pass, role, perms ->
                viewModel.adminCreateSubAdmin(name, email, pass, role, perms)
                showCreateDialog = false
            }
        )
    }

    // Dialog: Edit Permissions
    if (adminToEditPermissions != null) {
        EditPermissionsDialog(
            admin = adminToEditPermissions!!,
            onDismiss = { adminToEditPermissions = null },
            onConfirm = { role, perms ->
                viewModel.adminUpdateAdminPermissions(adminToEditPermissions!!.id, role, perms)
                adminToEditPermissions = null
            }
        )
    }

    // Dialog: Reset Password
    if (adminToResetPassword != null) {
        ResetAdminPasswordDialog(
            admin = adminToResetPassword!!,
            onDismiss = { adminToResetPassword = null },
            onConfirm = { newPass ->
                viewModel.adminResetPassword(adminToResetPassword!!.id, newPass)
                adminToResetPassword = null
            }
        )
    }

    // Dialog: Delete Admin
    if (adminToDelete != null) {
        AlertDialog(
            onDismissRequest = { adminToDelete = null },
            title = {
                Text(
                    text = "DELETE ADMIN ACCOUNT",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonRed
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently revoke admin access for '${adminToDelete?.fullName}' (${adminToDelete?.email})?",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                CyberButton(
                    text = "YES, DELETE",
                    containerColor = NeonRed,
                    onClick = {
                        viewModel.adminDeleteAdmin(adminToDelete!!.id, adminToDelete!!.email)
                        adminToDelete = null
                    }
                )
            },
            dismissButton = {
                CyberOutlinedButton(
                    text = "CANCEL",
                    onClick = { adminToDelete = null }
                )
            },
            containerColor = EsportsSurfaceVariant
        )
    }
}

@Composable
fun CreateAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, pass: String, role: String, permissions: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("MATCH_ADMIN") }
    var selectedPermissions by remember { mutableStateOf("MATCHES_ONLY") }

    val roleOptions = listOf(
        Triple("Match Admin (Hamza)", "MATCH_ADMIN", "MATCHES_ONLY"),
        Triple("Player Management Admin", "PLAYER_ADMIN", "PLAYERS_ONLY"),
        Triple("Payments Admin", "PAYMENT_ADMIN", "PAYMENTS_ONLY"),
        Triple("Results & Prize Admin", "RESULTS_ADMIN", "RESULTS_ONLY"),
        Triple("Full Sub-Admin", "ADMIN", "ALL")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CREATE NEW ADMIN ACCOUNT",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Provide administrator credentials and select specific permission level.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name (e.g. Hamza)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (e.g. hamza@pakpakistan.com)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (e.g. hamza123)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )

                Text(
                    text = "Select Permissions / Role:",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GoldYellow
                )

                roleOptions.forEach { (label, roleVal, permVal) ->
                    val isSelected = selectedRole == roleVal
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedRole = roleVal
                            selectedPermissions = permVal
                        },
                        label = {
                            Text(
                                text = label,
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            containerColor = EsportsSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            CyberButton(
                text = "CREATE ADMIN",
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        onConfirm(fullName, email, password, selectedRole, selectedPermissions)
                    }
                }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        },
        containerColor = EsportsSurfaceVariant
    )
}

@Composable
fun EditPermissionsDialog(
    admin: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (role: String, permissions: String) -> Unit
) {
    var selectedRole by remember { mutableStateOf(admin.role) }
    var selectedPermissions by remember { mutableStateOf(admin.adminPermissions) }

    val roleOptions = listOf(
        Triple("Match Admin Only", "MATCH_ADMIN", "MATCHES_ONLY"),
        Triple("Player Management Only", "PLAYER_ADMIN", "PLAYERS_ONLY"),
        Triple("Payment Management Only", "PAYMENT_ADMIN", "PAYMENTS_ONLY"),
        Triple("Results Management Only", "RESULTS_ADMIN", "RESULTS_ONLY"),
        Triple("Full Sub-Admin Access", "ADMIN", "ALL")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "EDIT PERMISSIONS: ${admin.fullName}",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Update permission scopes for ${admin.email}",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                roleOptions.forEach { (label, roleVal, permVal) ->
                    val isSelected = selectedRole == roleVal
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedRole = roleVal
                            selectedPermissions = permVal
                        },
                        label = {
                            Text(
                                text = label,
                                fontFamily = RajdhaniFontFamily,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            containerColor = EsportsSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            CyberButton(
                text = "SAVE PERMISSIONS",
                onClick = { onConfirm(selectedRole, selectedPermissions) }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        },
        containerColor = EsportsSurfaceVariant
    )
}

@Composable
fun ResetAdminPasswordDialog(
    admin: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (newPass: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "RESET PASSWORD: ${admin.fullName}",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GoldYellow
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Enter a new secure password for ${admin.email}",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = cyberTextFieldColors()
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "CONFIRM RESET",
                onClick = {
                    if (newPassword.isNotBlank()) onConfirm(newPassword)
                }
            )
        },
        dismissButton = {
            CyberOutlinedButton(text = "CANCEL", onClick = onDismiss)
        },
        containerColor = EsportsSurfaceVariant
    )
}
