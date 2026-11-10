package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FireOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.SecondaryBackground
import com.example.ui.theme.TextLightGray

@Composable
fun OfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    if (isOffline) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF3B1010))
                .border(BorderStroke(1.dp, NeonRed))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = FireOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NO INTERNET CONNECTION • OFFLINE MODE (Cached Data)",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OfflineErrorDialog(
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SecondaryBackground,
        icon = {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = NeonRed,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "CONNECTION ERROR",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        },
        text = {
            Column {
                Text(
                    text = "Unable to connect to the tournament servers. Please check your WiFi or mobile data connection.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 13.sp,
                    color = TextLightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your offline match details and profile are safely cached on device.",
                    fontFamily = RajdhaniFontFamily,
                    fontSize = 11.sp,
                    color = ElectricCyan
                )
            }
        },
        confirmButton = {
            CyberButton(
                text = "RETRY CONNECTION",
                onClick = onRetry,
                icon = Icons.Default.Refresh,
                modifier = Modifier.testTag("offline_retry_button")
            )
        },
        dismissButton = {
            CyberOutlinedButton(
                text = "CONTINUE OFFLINE",
                onClick = onDismiss,
                borderColor = Color.Gray,
                textColor = TextLightGray
            )
        }
    )
}
