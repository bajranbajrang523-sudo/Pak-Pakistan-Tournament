package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.BattleViewModel
import com.example.ui.Screen
import com.example.ui.components.CyberButton
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsCard
import com.example.ui.theme.EsportsCardBorder
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    viewModel: BattleViewModel,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Emblem
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            2.dp,
                            Brush.sweepGradient(listOf(com.example.ui.theme.FireOrange, com.example.ui.theme.ElectricCyan, com.example.ui.theme.FireOrange))
                        ),
                        CircleShape
                    )
                    .background(com.example.ui.theme.SecondaryBackground),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pak_tournament_logo),
                    contentDescription = "PAK PAKISTAN TOURNAMENT Logo",
                    modifier = Modifier.size(82.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PAK PAKISTAN TOURNAMENT",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = com.example.ui.theme.ElectricCyan,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔥 FIRE ESPORTS PRO ARENA",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = com.example.ui.theme.FireOrange,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Login Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(com.example.ui.theme.SecondaryBackground)
                    .border(
                        BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    com.example.ui.theme.ElectricCyan.copy(alpha = 0.8f),
                                    com.example.ui.theme.FireOrange.copy(alpha = 0.8f)
                                )
                            )
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(22.dp)
            ) {
                Column {
                    Text(
                        text = "PLAYER LOGIN",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = com.example.ui.theme.TextWhite
                    )
                    Text(
                        text = "Enter Email or Free Fire Name to access your dashboard",
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 13.sp,
                        color = com.example.ui.theme.TextLightGray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Email or Free Fire Name") },
                        leadingIcon = {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = com.example.ui.theme.ElectricCyan)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.ElectricCyan)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = com.example.ui.theme.TextLightGray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text(
                            text = "Forgot Password?",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = com.example.ui.theme.ElectricCyan,
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(Screen.ForgotPassword) }
                                .padding(vertical = 4.dp)
                                .testTag("forgot_password_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = com.example.ui.theme.ElectricCyan, modifier = Modifier.size(36.dp))
                        }
                    } else {
                        CyberButton(
                            text = "⚡ ENTER BATTLEGROUND",
                            onClick = {
                                viewModel.login(identifier, password)
                            },
                            containerColor = com.example.ui.theme.ElectricCyan,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "login_submit_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New Warrior? ",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Register Account",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NeonCyan,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.Register) }
                        .testTag("goto_register_button")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: BattleViewModel,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var ffName by remember { mutableStateOf("") }
    var ffUid by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PLAYER REGISTRATION",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = NeonCyan
            )
            Text(
                text = "Claim 100 Free Bonus Coins upon registration!",
                fontFamily = RajdhaniFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = GoldYellow
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EsportsCard)
                    .border(1.dp, EsportsCardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ffName,
                        onValueChange = { ffName = it },
                        label = { Text("Free Fire In-Game Name *") },
                        leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_ffname_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ffUid,
                        onValueChange = { if (it.all { char -> char.isDigit() }) ffUid = it },
                        label = { Text("Free Fire UID * (e.g. 729104821)") },
                        leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("reg_ffuid_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("reg_password_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = referralCode,
                        onValueChange = { referralCode = it.uppercase() },
                        label = { Text("Referral Code (Optional)") },
                        leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = null, tint = ElectricPurple) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_referral_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(36.dp))
                        }
                    } else {
                        CyberButton(
                            text = "CREATE WARRIOR ACCOUNT",
                            onClick = {
                                viewModel.register(fullName, ffName, ffUid, email, password, referralCode)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "reg_submit_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already registered? ",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Login here",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NeonCyan,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.Login) }
                        .testTag("goto_login_button")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: BattleViewModel,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var emailOrUid by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "RESET PASSWORD",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = NeonCyan
            )
            Text(
                text = "Enter your registered Email or Free Fire UID to reset your password",
                fontFamily = RajdhaniFontFamily,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EsportsCard)
                    .border(1.dp, EsportsCardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = emailOrUid,
                        onValueChange = { emailOrUid = it },
                        label = { Text("Registered Email or Free Fire UID") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_identifier_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("forgot_newpass_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("forgot_confirmpass_input"),
                        colors = cyberTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(36.dp))
                        }
                    } else {
                        CyberButton(
                            text = "UPDATE PASSWORD",
                            onClick = {
                                if (newPassword != confirmPassword) {
                                    viewModel.showToast("Passwords do not match!")
                                } else {
                                    viewModel.forgotPassword(emailOrUid, newPassword)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "forgot_submit_button"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Back to Login",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(Screen.Login) }
                                .padding(8.dp)
                                .testTag("forgot_back_to_login")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun cyberTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = com.example.ui.theme.TextWhite,
    unfocusedTextColor = com.example.ui.theme.TextWhite,
    focusedContainerColor = com.example.ui.theme.SecondaryBackground,
    unfocusedContainerColor = com.example.ui.theme.SecondaryBackground,
    focusedBorderColor = com.example.ui.theme.ElectricCyan,
    unfocusedBorderColor = com.example.ui.theme.SurfaceCardBorder,
    focusedLabelColor = com.example.ui.theme.ElectricCyan,
    unfocusedLabelColor = com.example.ui.theme.TextLightGray,
    cursorColor = com.example.ui.theme.ElectricCyan,
    focusedLeadingIconColor = com.example.ui.theme.ElectricCyan,
    unfocusedLeadingIconColor = com.example.ui.theme.TextLightGray
)
