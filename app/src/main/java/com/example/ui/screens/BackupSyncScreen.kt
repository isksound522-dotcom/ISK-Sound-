package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GoldButton
import com.example.ui.components.IskTopAppBar
import com.example.ui.components.OutlinedGoldButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BackupSyncScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncStatusMsg by viewModel.syncStatusMessage.collectAsState()
    val isSecurityPinEnabled by viewModel.isSecurityPinEnabled.collectAsState()
    val securityPin by viewModel.securityPin.collectAsState()

    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf<String?>(null) }

    var showPinChangeDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = "ક્લાઉડ સિંક & સુરક્ષિત બેકઅપ",
                subtitle = "I. S. K SOUND & LIVE DJ",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBack
            )
        },
        containerColor = NavyBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cloud Sync Section
            Card(
                modifier = Modifier.fillMaxWidth().testTag("card_cloud_sync"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Firebase Cloud Firestore સિંક",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ડેટા આપોઆપ ફોનમાં સેવ થાય છે અને નેટ મળ્યે ક્લાઉડ સિંક થાય છે",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSyncing) Icons.Default.Sync else Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = if (isSyncing) LaserCyan else SuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = syncStatusMsg,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    GoldButton(
                        text = if (isSyncing) "સિંક થઈ રહ્યું છે..." else "હમણાં જ ક્લાઉડ સિંક કરો",
                        icon = Icons.Default.CloudUpload,
                        enabled = !isSyncing,
                        modifier = Modifier.testTag("btn_trigger_sync"),
                        onClick = {
                            viewModel.triggerCloudSync { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }

            // Safe Backup & Restore Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "સુરક્ષિત ડેટા બેકઅપ & રિસ્ટોર",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "તમામ બુકિંગ, હિસાબ અને ગ્રાહક ડેટા સુરક્ષિત ફાઇલ તરીકે સેવ કરો જેથી ક્યારેય ડેટા ખોવાય નહીં.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    // Export / Share Backup Button
                    OutlinedGoldButton(
                        text = "સુરક્ષિત બેકઅપ ફાઇલ શેર કરો / સેવ કરો",
                        icon = Icons.Default.FileDownload,
                        modifier = Modifier.testTag("btn_export_backup"),
                        onClick = {
                            coroutineScope.launch {
                                val json = viewModel.exportBackupJson()
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_SUBJECT, "I. S. K Sound & Live DJ - Backup Data")
                                    putExtra(Intent.EXTRA_TEXT, json)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "બેકઅપ ફાઇલ સેવ કરો / શેર કરો"))
                            }
                        }
                    )

                    // Restore Button
                    OutlinedButton(
                        onClick = {
                            restoreError = null
                            restoreJsonText = ""
                            showRestoreDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_restore_backup"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LaserCyan),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LaserCyan)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("બેકઅપમાંથી ડેટા રિસ્ટોર કરો", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // App Security & PIN Lock
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "એપ સુરક્ષા પિન લોક (Passcode)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "હિસાબ અને દરો ગુપ્ત રાખવા પિન કોડ સેટ કરો",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Switch(
                            checked = isSecurityPinEnabled,
                            onCheckedChange = { checked ->
                                viewModel.toggleSecurityPin(checked)
                                if (checked) {
                                    Toast.makeText(context, "પિન લોક સક્રિય થયો (ડિફોલ્ટ પિન: 1234)", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NavyDeep,
                                checkedTrackColor = GoldPrimary
                            )
                        )
                    }

                    if (isSecurityPinEnabled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "વર્તમાન પિન: $securityPin",
                                style = MaterialTheme.typography.bodyMedium.copy(color = LaserCyan, fontWeight = FontWeight.Bold)
                            )

                            TextButton(onClick = {
                                newPinInput = ""
                                showPinChangeDialog = true
                            }) {
                                Text("પિન બદલો", color = GoldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Firebase & APK Installation Guide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = LaserCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ફાયરબેઝ સેટઅપ અને APK ઇન્સ્ટોલેશન",
                            style = MaterialTheme.typography.titleSmall.copy(color = GoldLight, fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "૧. લોકલ ડેટાબેઝ: એપ્લિકેશન Room Database નો ઉપયોગ કરે છે, જેથી ઈન્ટરનેટ વગર પણ તમામ બુકિંગ અને બિલ તાત્કાલિક સાચવી શકાય છે.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp)
                    )
                    Text(
                        text = "૨. ફાયરબેઝ ક્લાઉડ સિંક: Firebase Firestore ની સુરક્ષિત સિંક્રનાઇઝેશન સુવિધા ઉપલબ્ધ છે. ઉપર આપેલ 'ક્લાઉડ સિંક' બટન વડે ડેટા ક્લાઉડમાં બેકઅપ થાય છે.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp)
                    )
                    Text(
                        text = "૩. APK ડાઉનલોડ & ઇન્સ્ટોલ: AI Studio માં ઉપર જમણી બાજુના Settings મેનુમાંથી 'Export APK' અથવા 'Download ZIP' પસંદ કરી તમારા એન્ડ્રોઇડ ફોનમાં સીધું ઇન્સ્ટોલ કરી શકાય છે.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Restore Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("બેકઅપ ડેટા રિસ્ટોર કરો", color = GoldPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "તમારી પાસે રહેલ JSON બેકઅપ કોડ નીચે પેસ્ટ કરો:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    OutlinedTextField(
                        value = restoreJsonText,
                        onValueChange = { restoreJsonText = it },
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        placeholder = { Text("JSON કોડ અહીં પેસ્ટ કરો...") },
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = NavyBorder,
                            cursorColor = GoldPrimary
                        )
                    )
                    if (restoreError != null) {
                        Text(restoreError ?: "", color = ErrorRed, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreJsonText.isBlank()) {
                            restoreError = "કૃપા કરીને માન્ય બેકઅપ ટેક્સ્ટ પેસ્ટ કરો."
                            return@Button
                        }
                        coroutineScope.launch {
                            val res = viewModel.restoreFromJson(restoreJsonText)
                            if (res.isSuccess) {
                                val count = res.getOrDefault(0)
                                Toast.makeText(context, "સફળતાપૂર્વક $count બુકિંગ રિસ્ટોર થયા!", Toast.LENGTH_LONG).show()
                                showRestoreDialog = false
                            } else {
                                restoreError = "બેકઅપ ફોર્મેટ અમાન્ય છે: ${res.exceptionOrNull()?.message}"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDeep)
                ) {
                    Text("રિસ્ટોર કરો", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("રદ કરો", color = TextSecondary)
                }
            },
            containerColor = NavySurface
        )
    }

    // Change PIN Dialog
    if (showPinChangeDialog) {
        AlertDialog(
            onDismissRequest = { showPinChangeDialog = false },
            title = { Text("નવો પિન કોડ દાખલ કરો", color = GoldPrimary) },
            text = {
                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = { if (it.length <= 6) newPinInput = it },
                    label = { Text("૪ અથવા ૬ અંકનો પિન") },
                    placeholder = { Text("દા.ત. 1234") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        cursorColor = GoldPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length >= 4) {
                            viewModel.toggleSecurityPin(true, newPinInput)
                            showPinChangeDialog = false
                            Toast.makeText(context, "નવો પિન સેટ થઈ ગયો!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "ઓછામાં ઓછા ૪ અંક દાખલ કરો", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDeep)
                ) {
                    Text("સેવ કરો", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinChangeDialog = false }) {
                    Text("રદ કરો", color = TextSecondary)
                }
            },
            containerColor = NavySurface
        )
    }
}
