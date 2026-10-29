package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.IskTopAppBar
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchListScreen(
    viewModel: MainViewModel,
    onSelectBooking: (Booking) -> Unit,
    onViewBill: (Booking) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filteredBookings by viewModel.filteredBookings.collectAsState()

    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale("gu", "IN")) }
    val totalPending = remember(filteredBookings) {
        filteredBookings.filter { it.status != "રદ" }.sumOf { it.remainingAmount }
    }

    val filterOptions = listOf("બધા", "આગામી", "બાકી રકમ", "પૂર્ણ")

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = "બુકિંગ ઇતિહાસ & હિસાબ",
                subtitle = "ગ્રાહક, ગામ અથવા બિલ નં. થી શોધો",
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_field"),
                placeholder = { Text("નામ, ગામ, મોબાઇલ કે બિલ નં...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "સાફ કરો", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedContainerColor = NavySurface,
                    unfocusedContainerColor = NavySurface,
                    cursorColor = GoldPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = NavyDeep,
                            containerColor = NavySurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = NavyBorder,
                            selectedBorderColor = GoldPrimary,
                            enabled = true,
                            selected = selectedFilter == filter
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Summary Info Strip
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "મળેલ પરિણામ: ${filteredBookings.size}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    if (totalPending > 0) {
                        Text(
                            text = "કુલ બાકી: ₹${currencyFormat.format(totalPending.toLong())}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WarningOrange,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Bookings
            if (filteredBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "કોઈ મેળ ખાતું બુકિંગ મળ્યું નથી",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "બીજા શબ્દો વડે શોધવા પ્રયાસ કરો",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingItemCardWithActions(
                            booking = booking,
                            onSelect = { onSelectBooking(booking) },
                            onViewBill = { onViewBill(booking) },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${booking.customerMobile}")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemCardWithActions(
    booking: Booking,
    onSelect: () -> Unit,
    onViewBill: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("search_booking_item_${booking.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${booking.billNumber} • ${booking.eventType}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                com.example.ui.components.StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Customer Name
            Text(
                text = booking.customerName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Route & Phone
            Text(
                text = "📍 ${booking.customerVillage} ➔ ${booking.eventVillage} | 📞 ${booking.customerMobile}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )

            // Date
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "📅 ${booking.arrivalDate} (${booking.arrivalTime})",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextGold,
                    fontSize = 12.sp
                )
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = NavyBorder)

            // Bottom Ledger & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "કુલ: ₹${booking.totalAmount.toLong()} | એડવાન્સ: ₹${booking.advanceAmount.toLong()}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                    Text(
                        text = if (booking.remainingAmount > 0) "બાકી: ₹${booking.remainingAmount.toLong()}" else "સંપૂર્ણ ચૂકતે (Paid)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (booking.remainingAmount > 0) WarningOrange else SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = NavySurfaceElevated)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "કૉલ કરો", tint = LaserCyan, modifier = Modifier.size(18.dp))
                    }

                    FilledTonalButton(
                        onClick = onViewBill,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = GoldPrimary, contentColor = NavyDeep)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("બિલ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
