package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.MainViewModel
import com.example.ui.components.GoldButton
import com.example.ui.components.IskTopAppBar
import com.example.ui.components.OutlinedGoldButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun BookingDetailScreen(
    viewModel: MainViewModel,
    booking: Booking,
    onEdit: () -> Unit,
    onViewBill: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCollectBalanceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = "બુકિંગ વિગત",
                subtitle = booking.billNumber,
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "ફેરફાર કરો", tint = GoldPrimary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "ડિલીટ કરો", tint = ErrorRed)
                    }
                }
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
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = booking.billNumber,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        StatusBadge(status = booking.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = booking.customerName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "પ્રસંગ: ${booking.eventType}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = LaserCyan)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${booking.customerMobile}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f).testTag("btn_call_customer"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceElevated, contentColor = LaserCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("કૉલ કરો", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                openWhatsApp(context, booking.customerMobile)
                            },
                            modifier = Modifier.weight(1f).testTag("btn_whatsapp_customer"),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen.copy(alpha = 0.2f), contentColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Location & Schedule Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "સ્થળ અને સમય વિગત",
                        style = MaterialTheme.typography.titleSmall.copy(color = GoldLight, fontWeight = FontWeight.Bold)
                    )

                    DetailRow(icon = Icons.Default.Home, label = "ગ્રાહકનું ગામ", value = booking.customerVillage)
                    DetailRow(icon = Icons.Default.Place, label = "પ્રોગ્રામ સ્થળ/ગામ", value = booking.eventVillage)
                    DetailRow(icon = Icons.Default.Schedule, label = "પહોંચવાનો સમય", value = "${booking.arrivalDate} (${booking.arrivalTime})")
                    DetailRow(icon = Icons.Default.Schedule, label = "છૂટા થવાનો સમય", value = "${booking.departureDate} (${booking.departureTime})")
                    DetailRow(icon = Icons.Default.Speaker, label = "સાઉન્ડ સેટઅપ", value = booking.soundSetupDetails)
                }
            }

            // Financial Ledger Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "હિસાબ પત્રક (Financial Ledger)",
                        style = MaterialTheme.typography.titleSmall.copy(color = GoldLight, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("કુલ રકમ:", color = TextSecondary)
                        Text("₹ ${booking.totalAmount.toLong()}", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("એડવાન્સ જમા રકમ:", color = TextSecondary)
                        Text("₹ ${booking.advanceAmount.toLong()}", color = LaserCyan, fontWeight = FontWeight.Bold)
                    }

                    Divider(color = NavyBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("બાકી રકમ:", color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (booking.remainingAmount > 0) "₹ ${booking.remainingAmount.toLong()}" else "₹ 0 (ચૂકતે)",
                            color = if (booking.remainingAmount > 0) WarningOrange else SuccessGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }

                    if (booking.remainingAmount > 0) {
                        OutlinedButton(
                            onClick = { showCollectBalanceDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, SuccessGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen)
                        ) {
                            Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("બાકી રકમ જમા લો (ચૂકતે કરો)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // View Printable Bill Button
            GoldButton(
                text = "ગુજરાતી બિલ જુઓ & શેર કરો",
                icon = Icons.Default.Receipt,
                onClick = onViewBill
            )

            // Status Update Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (booking.status != "પૂર્ણ") {
                    OutlinedGoldButton(
                        text = "પ્રોગ્રામ પૂર્ણ તરીકે માર્ક કરો",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.updateBooking(booking.copy(status = "પૂર્ણ")) {
                                Toast.makeText(context, "સ્ટેટસ અપડેટ થઈ ગયું!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.updateBooking(booking.copy(status = "કન્ફર્મ")) {
                                Toast.makeText(context, "સ્ટેટસ કન્ફર્મ થઈ ગયું!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, LaserCyan),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LaserCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ફરી કન્ફર્મ કરો")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("બુકિંગ ડિલીટ કરવું છે?", color = GoldPrimary) },
            text = { Text("શું તમે ખરેખર ${booking.customerName} નું બુકિંગ ડિલીટ કરવા માંગો છો?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteBooking(booking) {
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("હા, ડિલીટ કરો")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ના", color = TextSecondary)
                }
            },
            containerColor = NavySurface
        )
    }

    // Collect Balance Dialog
    if (showCollectBalanceDialog) {
        AlertDialog(
            onDismissRequest = { showCollectBalanceDialog = false },
            title = { Text("બાકી રકમ જમા લો", color = GoldPrimary) },
            text = {
                Text("શું ₹ ${booking.remainingAmount.toLong()} ની પૂરેપૂરી બાકી રકમ જમા થઈ ગઈ છે? આ બુકિંગ સંપૂર્ણ ચૂકતે થઈ જશે.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCollectBalanceDialog = false
                        val updated = booking.copy(
                            advanceAmount = booking.totalAmount,
                            remainingAmount = 0.0
                        )
                        viewModel.updateBooking(updated) {
                            Toast.makeText(context, "હિસાબ સફળતાપૂર્વક ચૂકતે થયો!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = NavyDeep)
                ) {
                    Text("હા, પૂરેપૂરું ચૂકતે કરો", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCollectBalanceDialog = false }) {
                    Text("રદ કરો", color = TextSecondary)
                }
            },
            containerColor = NavySurface
        )
    }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = LaserCyan, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 13.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        )
    }
}

private fun openWhatsApp(context: Context, mobile: String) {
    try {
        val cleanNumber = mobile.replace("+", "").replace(" ", "").replace("-", "")
        val formattedNumber = if (cleanNumber.length == 10) "91$cleanNumber" else cleanNumber
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp ઉપલબ્ધ નથી", Toast.LENGTH_SHORT).show()
    }
}
