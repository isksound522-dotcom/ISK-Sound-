package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AuthLockScreen(
    viewModel: MainViewModel,
    onUnlocked: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Lock Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(GoldPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "I. S. K SOUND & LIVE DJ",
            style = MaterialTheme.typography.titleLarge.copy(
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "સુરક્ષા પિન કોડ દાખલ કરો",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PIN Indicator Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                val isFilled = index < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) GoldPrimary else NavyBorder)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = errorMessage ?: "",
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Number Keypad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        if (key.isEmpty()) {
                            Spacer(modifier = Modifier.size(64.dp))
                        } else if (key == "DEL") {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (enteredPin.isNotEmpty()) {
                                            enteredPin = enteredPin.dropLast(1)
                                            errorMessage = null
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Backspace, contentDescription = "ડિલીટ", tint = TextSecondary)
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable {
                                        if (enteredPin.length < 4) {
                                            val newPin = enteredPin + key
                                            enteredPin = newPin
                                            errorMessage = null

                                            if (newPin.length == 4) {
                                                if (viewModel.verifyPin(newPin)) {
                                                    onUnlocked()
                                                } else {
                                                    errorMessage = "ખોટો પિન કોડ! ફરી પ્રયાસ કરો."
                                                    enteredPin = ""
                                                }
                                            }
                                        }
                                    },
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = NavySurface),
                                border = BorderStroke(1.dp, NavyBorder)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
