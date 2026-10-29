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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.MainViewModel
import com.example.ui.components.IskTopAppBar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    onNavigateToCreateWithDate: (String) -> Unit,
    onSelectBooking: (Booking) -> Unit,
    onViewBill: (Booking) -> Unit,
    onBack: () -> Unit
) {
    val selectedDate by viewModel.selectedCalendarDate.collectAsState()
    val bookingsForDate by viewModel.bookingsForSelectedDate.collectAsState()
    val bookedDateSet by viewModel.bookedDateSet.collectAsState()

    var calendarMonth by remember {
        val cal = Calendar.getInstance()
        mutableStateOf(cal)
    }

    val gujaratiMonths = listOf(
        "જાન્યુઆરી", "ફેબ્રુઆરી", "માર્ચ", "એપ્રિલ", "મે", "જૂન",
        "જુલાઈ", "ઓગસ્ટ", "સપ્ટેમ્બર", "ઓક્ટોબર", "નવેમ્બર", "ડિસેમ્બર"
    )
    val gujaratiDays = listOf("રવિ", "સોમ", "મંગળ", "બુધ", "ગુરુ", "શુક્ર", "શનિ")

    val currentMonthName = gujaratiMonths[calendarMonth.get(Calendar.MONTH)]
    val currentYear = calendarMonth.get(Calendar.YEAR)

    // Calculate days for the month grid
    val daysInMonth = remember(calendarMonth.timeInMillis) {
        val temp = calendarMonth.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
        val maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        Pair(firstDayOfWeek, maxDays)
    }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = "ગુજરાતી બુકિંગ કૅલેન્ડર",
                subtitle = "$currentMonthName $currentYear - તારીખ મુજબ બુકિંગ",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToCreateWithDate(selectedDate) },
                containerColor = GoldPrimary,
                contentColor = NavyDeep,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("આ તારીખે બુકિંગ ઉમેરો", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = NavyBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Month Header Controls
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newCal = calendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            calendarMonth = newCal
                        }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "પાછલો મહિનો", tint = GoldPrimary)
                        }

                        Text(
                            text = "$currentMonthName $currentYear",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )

                        IconButton(onClick = {
                            val newCal = calendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            calendarMonth = newCal
                        }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "આગલો મહિનો", tint = GoldPrimary)
                        }
                    }
                }
            }

            // Month Grid
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Day of week header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            gujaratiDays.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (dayName == "રવિ") ErrorRed else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Calendar Matrix (Up to 6 rows)
                        val (firstDayOffset, totalDays) = daysInMonth
                        val totalCells = ((firstDayOffset + totalDays + 6) / 7) * 7

                        for (weekIndex in 0 until totalCells / 7) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (dayOfWeek in 0 until 7) {
                                    val cellIndex = weekIndex * 7 + dayOfWeek
                                    val dayNumber = cellIndex - firstDayOffset + 1

                                    if (dayNumber in 1..totalDays) {
                                        val dateString = String.format(
                                            Locale.US,
                                            "%04d-%02d-%02d",
                                            calendarMonth.get(Calendar.YEAR),
                                            calendarMonth.get(Calendar.MONTH) + 1,
                                            dayNumber
                                        )

                                        val isSelected = dateString == selectedDate
                                        val isToday = dateString == todayStr
                                        val hasBooking = bookedDateSet.contains(dateString)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelected -> GoldPrimary
                                                        hasBooking -> NavySurfaceElevated
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .then(
                                                    if (isToday && !isSelected) {
                                                        Modifier.background(
                                                            color = LaserCyan.copy(alpha = 0.2f),
                                                            shape = CircleShape
                                                        )
                                                    } else Modifier
                                                )
                                                .clickable {
                                                    viewModel.selectCalendarDate(dateString)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "$dayNumber",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = when {
                                                            isSelected -> NavyDeep
                                                            isToday -> LaserCyan
                                                            hasBooking -> GoldLight
                                                            else -> TextPrimary
                                                        },
                                                        fontWeight = if (isSelected || isToday || hasBooking) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 13.sp
                                                    )
                                                )

                                                if (hasBooking) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) NavyDeep else GoldPrimary)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        // Empty cell for alignment
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Date Summary Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "તારીખ: $selectedDate ના DJ બુકિંગ્સ (${bookingsForDate.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Bookings List for Selected Date
            if (bookingsForDate.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "આ તારીખે કોઈ DJ બુકિંગ નથી (તારીખ ખાલી છે)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { onNavigateToCreateWithDate(selectedDate) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                border = BorderStroke(1.dp, GoldPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ આ તારીખ માટે બુકિંગ ઉમેરો")
                            }
                        }
                    }
                }
            } else {
                items(bookingsForDate) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onClick = { onSelectBooking(booking) },
                        onViewBill = { onViewBill(booking) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
