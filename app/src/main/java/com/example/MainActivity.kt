package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Booking
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppScreen {
    HOME,
    CALENDAR,
    SEARCH,
    CREATE_BOOKING,
    BOOKING_DETAIL,
    BILL_RECEIPT,
    BACKUP_SYNC
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DjAppRoot()
            }
        }
    }
}

@Composable
fun DjAppRoot(viewModel: MainViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedBookingForDetail by remember { mutableStateOf<Booking?>(null) }
    var selectedBookingForBill by remember { mutableStateOf<Booking?>(null) }
    var bookingToEdit by remember { mutableStateOf<Booking?>(null) }
    var initialDateForCreate by remember { mutableStateOf<String?>(null) }

    val isAppLocked by viewModel.isAppLocked.collectAsState()

    // Back handling
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        when (currentScreen) {
            AppScreen.BILL_RECEIPT -> {
                if (selectedBookingForDetail != null) {
                    currentScreen = AppScreen.BOOKING_DETAIL
                } else {
                    currentScreen = AppScreen.HOME
                }
            }
            AppScreen.BOOKING_DETAIL -> {
                currentScreen = AppScreen.HOME
            }
            AppScreen.CREATE_BOOKING -> {
                if (selectedBookingForDetail != null) {
                    currentScreen = AppScreen.BOOKING_DETAIL
                } else {
                    currentScreen = AppScreen.HOME
                }
            }
            else -> {
                currentScreen = AppScreen.HOME
            }
        }
    }

    if (isAppLocked) {
        AuthLockScreen(
            viewModel = viewModel,
            onUnlocked = { /* app unlocked */ }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen in listOf(AppScreen.HOME, AppScreen.CALENDAR, AppScreen.SEARCH, AppScreen.BACKUP_SYNC)) {
                NavigationBar(
                    containerColor = NavyDeep,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        Triple(AppScreen.HOME, "હોમ", Icons.Default.Home),
                        Triple(AppScreen.CALENDAR, "કૅલેન્ડર", Icons.Default.CalendarMonth),
                        Triple(AppScreen.CREATE_BOOKING, "નવું બુકિંગ", Icons.Default.AddCircle),
                        Triple(AppScreen.SEARCH, "હિસાબ", Icons.Default.Search),
                        Triple(AppScreen.BACKUP_SYNC, "બેકઅપ", Icons.Default.CloudSync)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (screen == AppScreen.CREATE_BOOKING) {
                                    bookingToEdit = null
                                    initialDateForCreate = null
                                    currentScreen = AppScreen.CREATE_BOOKING
                                } else {
                                    currentScreen = screen
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) GoldPrimary else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isSelected) GoldPrimary else TextSecondary
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = NavySurfaceElevated
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        },
        containerColor = NavyBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCreate = {
                            bookingToEdit = null
                            initialDateForCreate = null
                            currentScreen = AppScreen.CREATE_BOOKING
                        },
                        onNavigateToCalendar = { currentScreen = AppScreen.CALENDAR },
                        onNavigateToSearch = { currentScreen = AppScreen.SEARCH },
                        onNavigateToBackup = { currentScreen = AppScreen.BACKUP_SYNC },
                        onSelectBooking = { booking ->
                            selectedBookingForDetail = booking
                            currentScreen = AppScreen.BOOKING_DETAIL
                        },
                        onViewBill = { booking ->
                            selectedBookingForBill = booking
                            currentScreen = AppScreen.BILL_RECEIPT
                        }
                    )
                }

                AppScreen.CALENDAR -> {
                    CalendarScreen(
                        viewModel = viewModel,
                        onNavigateToCreateWithDate = { dateStr ->
                            bookingToEdit = null
                            initialDateForCreate = dateStr
                            currentScreen = AppScreen.CREATE_BOOKING
                        },
                        onSelectBooking = { booking ->
                            selectedBookingForDetail = booking
                            currentScreen = AppScreen.BOOKING_DETAIL
                        },
                        onViewBill = { booking ->
                            selectedBookingForBill = booking
                            currentScreen = AppScreen.BILL_RECEIPT
                        },
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.SEARCH -> {
                    SearchListScreen(
                        viewModel = viewModel,
                        onSelectBooking = { booking ->
                            selectedBookingForDetail = booking
                            currentScreen = AppScreen.BOOKING_DETAIL
                        },
                        onViewBill = { booking ->
                            selectedBookingForBill = booking
                            currentScreen = AppScreen.BILL_RECEIPT
                        },
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.CREATE_BOOKING -> {
                    BookingFormScreen(
                        viewModel = viewModel,
                        existingBooking = bookingToEdit,
                        initialDate = initialDateForCreate,
                        onBack = {
                            if (selectedBookingForDetail != null) {
                                currentScreen = AppScreen.BOOKING_DETAIL
                            } else {
                                currentScreen = AppScreen.HOME
                            }
                        },
                        onSaved = { savedId ->
                            viewModel.allBookings.value.find { it.id == savedId }?.let { saved ->
                                selectedBookingForBill = saved
                                currentScreen = AppScreen.BILL_RECEIPT
                            } ?: run {
                                currentScreen = AppScreen.HOME
                            }
                        }
                    )
                }

                AppScreen.BOOKING_DETAIL -> {
                    val booking = selectedBookingForDetail
                    if (booking != null) {
                        BookingDetailScreen(
                            viewModel = viewModel,
                            booking = booking,
                            onEdit = {
                                bookingToEdit = booking
                                currentScreen = AppScreen.CREATE_BOOKING
                            },
                            onViewBill = {
                                selectedBookingForBill = booking
                                currentScreen = AppScreen.BILL_RECEIPT
                            },
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    } else {
                        currentScreen = AppScreen.HOME
                    }
                }

                AppScreen.BILL_RECEIPT -> {
                    val booking = selectedBookingForBill
                    if (booking != null) {
                        BillReceiptScreen(
                            booking = booking,
                            onBack = {
                                if (selectedBookingForDetail != null) {
                                    currentScreen = AppScreen.BOOKING_DETAIL
                                } else {
                                    currentScreen = AppScreen.HOME
                                }
                            }
                        )
                    } else {
                        currentScreen = AppScreen.HOME
                    }
                }

                AppScreen.BACKUP_SYNC -> {
                    BackupSyncScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }
            }
        }
    }
}
