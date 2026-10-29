package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.MainViewModel
import com.example.ui.components.IskTopAppBar
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onSelectBooking: (Booking) -> Unit,
    onViewBill: (Booking) -> Unit
) {
    val allBookings by viewModel.allBookings.collectAsState()
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Derived statistics
    val totalCount = allBookings.size
    val upcomingCount = allBookings.count { it.arrivalDate >= todayStr && it.status != "રદ" }
    val totalPendingAmount = allBookings.filter { it.status != "રદ" }.sumOf { it.remainingAmount }
    val todayBookings = allBookings.filter { it.arrivalDate == todayStr }

    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale("gu", "IN")) }

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = "I. S. K SOUND & LIVE DJ",
                subtitle = "સંપૂર્ણ ડીજે ઇવેન્ટ & બિલિંગ મેનેજમેન્ટ",
                actions = {
                    IconButton(
                        onClick = onNavigateToBackup,
                        modifier = Modifier.testTag("backup_icon_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "ક્લાઉડ બેકઅપ",
                            tint = GoldPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                modifier = Modifier.testTag("add_booking_fab"),
                containerColor = GoldPrimary,
                contentColor = NavyDeep,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        "નવું બુકિંગ ઉમેરો",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            )
        },
        containerColor = NavyBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Hero Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        NavySurfaceElevated,
                                        NavySurface
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "જય માતાજી • નમસ્તે",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = LaserCyan,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "I. S. K Sound & Live DJ",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (todayBookings.isNotEmpty()) {
                                        "🔔 આજે ${todayBookings.size} ડીજે કાર્યક્રમ શેડ્યૂલ છે!"
                                    } else {
                                        "🎶 આજે કોઈ કાર્યક્રમ શેડ્યૂલ નથી. આગામી બુકિંગ તપાસો."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Equalizer,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Stats Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "કુલ બુકિંગ",
                            value = "$totalCount",
                            subtitle = "કુલ નોંધાયેલ",
                            icon = Icons.Default.BookOnline,
                            iconTint = GoldPrimary,
                            modifier = Modifier.weight(1f).testTag("stat_total_bookings"),
                            onClick = onNavigateToSearch
                        )
                        StatCard(
                            title = "આગામી પ્રોગ્રામ",
                            value = "$upcomingCount",
                            subtitle = "આગળના કાર્યક્રમ",
                            icon = Icons.Default.Event,
                            iconTint = LaserCyan,
                            modifier = Modifier.weight(1f).testTag("stat_upcoming"),
                            onClick = onNavigateToCalendar
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "બાકી રકમ",
                            value = "₹${currencyFormat.format(totalPendingAmount.toLong())}",
                            subtitle = "કુલ લેવાની રકમ",
                            icon = Icons.Default.AccountBalanceWallet,
                            iconTint = WarningOrange,
                            modifier = Modifier.weight(1f).testTag("stat_pending_balance"),
                            onClick = onNavigateToSearch
                        )
                        StatCard(
                            title = "આજનું શેડ્યૂલ",
                            value = "${todayBookings.size}",
                            subtitle = "આજના ઓર્ડર",
                            icon = Icons.Default.MusicNote,
                            iconTint = LaserPurple,
                            modifier = Modifier.weight(1f).testTag("stat_today"),
                            onClick = onNavigateToCalendar
                        )
                    }
                }
            }

            // Quick Navigation Shortcuts
            item {
                Text(
                    text = "ઝડપી મેનુ (Quick Actions)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ShortcutButton(
                        title = "બુકિંગ કૅલેન્ડર",
                        icon = Icons.Default.CalendarMonth,
                        iconTint = LaserCyan,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCalendar
                    )
                    ShortcutButton(
                        title = "શોધો & હિસાબ",
                        icon = Icons.Default.Search,
                        iconTint = GoldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSearch
                    )
                    ShortcutButton(
                        title = "બેકઅપ સિંક",
                        icon = Icons.Default.CloudSync,
                        iconTint = SuccessGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBackup
                    )
                }
            }

            // Recent Bookings Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "તાજેતરના બુકિંગ્સ (Recent Bookings)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    TextButton(onClick = onNavigateToSearch) {
                        Text(
                            text = "બધા જુઓ >",
                            color = LaserCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Recent Bookings List
            if (allBookings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueueMusic,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "હજી સુધી કોઈ બુકિંગ નોંધાયેલ નથી",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "નીચે આપેલ બટન દબાવી પહેલું DJ બુકિંગ ઉમેરો",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToCreate,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDeep),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ પહેલું બુકિંગ ઉમેરો", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(allBookings.take(8)) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onClick = { onSelectBooking(booking) },
                        onViewBill = { onViewBill(booking) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}

@Composable
fun ShortcutButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onClick: () -> Unit,
    onViewBill: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("booking_card_${booking.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Bill Number & Status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = booking.billNumber,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${booking.eventType}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
                StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Name & Mobile
            Text(
                text = booking.customerName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Village to Destination
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = LaserCyan,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${booking.customerVillage} ➔ ${booking.eventVillage}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Event arrival date & time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = GoldLight,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "પહોંચવું: ${booking.arrivalDate} (${booking.arrivalTime})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextGold,
                        fontSize = 12.sp
                    )
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = NavyBorder,
                thickness = 0.8.dp
            )

            // Financial Summary & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "કુલ: ₹${booking.totalAmount.toLong()} | એડવાન્સ: ₹${booking.advanceAmount.toLong()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = if (booking.remainingAmount > 0) {
                            "બાકી: ₹${booking.remainingAmount.toLong()}"
                        } else {
                            "સંપૂર્ણ ચૂકતે (Paid)"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (booking.remainingAmount > 0) WarningOrange else SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                FilledTonalButton(
                    onClick = onViewBill,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NavySurfaceElevated,
                        contentColor = GoldPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "બિલ",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "બિલ જુઓ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
