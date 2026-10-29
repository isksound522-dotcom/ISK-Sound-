package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.MainViewModel
import com.example.ui.components.GoldButton
import com.example.ui.components.IskTopAppBar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    viewModel: MainViewModel,
    existingBooking: Booking? = null,
    initialDate: String? = null,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    val context = LocalContext.current
    val isEditMode = existingBooking != null

    val generatedBillNo by viewModel.nextBillNumber.collectAsState()

    var billNumber by remember {
        mutableStateOf(existingBooking?.billNumber ?: generatedBillNo)
    }

    var customerName by remember { mutableStateOf(existingBooking?.customerName ?: "") }
    var customerMobile by remember { mutableStateOf(existingBooking?.customerMobile ?: "") }
    var customerVillage by remember { mutableStateOf(existingBooking?.customerVillage ?: "") }
    var eventVillage by remember { mutableStateOf(existingBooking?.eventVillage ?: "") }
    var eventType by remember { mutableStateOf(existingBooking?.eventType ?: "લગ્ન પ્રસંગ") }

    // Dates & Times
    val todayDateStr = remember {
        initialDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var arrivalDate by remember { mutableStateOf(existingBooking?.arrivalDate ?: todayDateStr) }
    var arrivalTime by remember { mutableStateOf(existingBooking?.arrivalTime ?: "06:00 PM") }
    var departureDate by remember { mutableStateOf(existingBooking?.departureDate ?: todayDateStr) }
    var departureTime by remember { mutableStateOf(existingBooking?.departureTime ?: "02:00 AM") }

    // Sound Equipment
    var soundSetupDetails by remember {
        mutableStateOf(existingBooking?.soundSetupDetails ?: "૨ બેઝ, ૪ ટોપ, શાર્પી લાઈટ્સ, સ્મોક મશીન, ટ્રસ")
    }

    // Financial
    var totalAmountStr by remember {
        mutableStateOf(if (existingBooking != null) existingBooking.totalAmount.toLong().toString() else "")
    }
    var advanceAmountStr by remember {
        mutableStateOf(if (existingBooking != null) existingBooking.advanceAmount.toLong().toString() else "")
    }

    // Auto-calculated remaining balance
    val totalAmount = totalAmountStr.toDoubleOrNull() ?: 0.0
    val advanceAmount = advanceAmountStr.toDoubleOrNull() ?: 0.0
    val remainingAmount = (totalAmount - advanceAmount).coerceAtLeast(0.0)

    var notes by remember {
        mutableStateOf(existingBooking?.notes ?: "જનરેટર અને લાઈટિંગ પોઈન્ટની સગવડ આયોજકની રહેશે.")
    }
    var status by remember { mutableStateOf(existingBooking?.status ?: "કન્ફર્મ") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Date Picker launcher helpers
    val calendar = Calendar.getInstance()

    fun showDatePicker(currentDateStr: String, onDateSelected: (String) -> Unit) {
        val parts = currentDateStr.split("-")
        val y = if (parts.size == 3) parts[0].toIntOrNull() ?: calendar.get(Calendar.YEAR) else calendar.get(Calendar.YEAR)
        val m = if (parts.size == 3) (parts[1].toIntOrNull() ?: 1) - 1 else calendar.get(Calendar.MONTH)
        val d = if (parts.size == 3) parts[2].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH) else calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val formatted = String.format(Locale.US, "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
            onDateSelected(formatted)
        }, y, m, d).show()
    }

    fun showTimePicker(currentTimeStr: String, onTimeSelected: (String) -> Unit) {
        TimePickerDialog(context, { _, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val displayHour = when {
                hourOfDay == 0 -> 12
                hourOfDay > 12 -> hourOfDay - 12
                else -> hourOfDay
            }
            val formatted = String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm)
            onTimeSelected(formatted)
        }, 18, 0, false).show()
    }

    val eventTypes = listOf("લગ્ન પ્રસંગ", "રાસ ગરબા", "બર્થડે પાર્ટી", "સંગીત સંધ્યા", "શોભાયાત્રા / વરઘોડો", "સામાજિક ઉત્સવ")
    val statusOptions = listOf("કન્ફર્મ", "પૂર્ણ", "રદ")

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = if (isEditMode) "બુકિંગ ફેરફાર કરો" else "નવું DJ બુકિંગ",
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
            // Bill Number & Badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "યુનિક બિલ નંબર (Bill No)",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = billNumber,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    if (!isEditMode) {
                        IconButton(
                            onClick = {
                                viewModel.generateNextBillNumber()
                                billNumber = viewModel.nextBillNumber.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "બિલ નં રિફ્રેશ",
                                tint = LaserCyan
                            )
                        }
                    }
                }
            }

            // Customer Details Section
            Text(
                text = "ગ્રાહકની માહિતી (Customer Details)",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            )

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("ગ્રાહકનું પૂરું નામ *") },
                placeholder = { Text("દા.ત. રમેશભાઈ પટેલ") },
                modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedLabelColor = GoldPrimary,
                    cursorColor = GoldPrimary
                )
            )

            OutlinedTextField(
                value = customerMobile,
                onValueChange = { if (it.length <= 10) customerMobile = it },
                label = { Text("મોબાઇલ નંબર (૧૦ અંક) *") },
                placeholder = { Text("દા.ત. 9876543210") },
                modifier = Modifier.fillMaxWidth().testTag("input_customer_mobile"),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedLabelColor = GoldPrimary,
                    cursorColor = GoldPrimary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = customerVillage,
                    onValueChange = { customerVillage = it },
                    label = { Text("ગ્રાહકનું ગામ *") },
                    placeholder = { Text("દા.ત. ગોંડલ") },
                    modifier = Modifier.weight(1f).testTag("input_customer_village"),
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = LaserCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )

                OutlinedTextField(
                    value = eventVillage,
                    onValueChange = { eventVillage = it },
                    label = { Text("પ્રોગ્રામનું સ્થળ/ગામ *") },
                    placeholder = { Text("દા.ત. રાજકોટ") },
                    modifier = Modifier.weight(1f).testTag("input_event_village"),
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = LaserCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )
            }

            // Event Type selection
            Text(
                text = "પ્રસંગનો પ્રકાર પસંદ કરો:",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                eventTypes.take(3).forEach { type ->
                    FilterChip(
                        selected = eventType == type,
                        onClick = { eventType = type },
                        label = { Text(type, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = NavyDeep,
                            containerColor = NavySurface,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                eventTypes.drop(3).forEach { type ->
                    FilterChip(
                        selected = eventType == type,
                        onClick = { eventType = type },
                        label = { Text(type, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = NavyDeep,
                            containerColor = NavySurface,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            // Schedule & Timing Section
            Divider(color = NavyBorder, thickness = 1.dp)

            Text(
                text = "કાર્યક્રમ સમય & તારીખ (Event Schedule)",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            )

            // Arrival Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker(arrivalDate) { arrivalDate = it } }
                        .testTag("btn_arrival_date"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("પહોંચવાની તારીખ *", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = LaserCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(arrivalDate, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker(arrivalTime) { arrivalTime = it } }
                        .testTag("btn_arrival_time"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("પહોંચવાનો સમય *", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = LaserCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(arrivalTime, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Departure Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker(departureDate) { departureDate = it } }
                        .testTag("btn_departure_date"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("છૂટા થવાની તારીખ *", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(departureDate, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker(departureTime) { departureTime = it } }
                        .testTag("btn_departure_time"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("છૂટા થવાનો સમય *", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(departureTime, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Sound Setup Details
            OutlinedTextField(
                value = soundSetupDetails,
                onValueChange = { soundSetupDetails = it },
                label = { Text("સાઉન્ડ સિસ્ટમ & લાઈટિંગ વિગત") },
                placeholder = { Text("દા.ત. ૨ બેઝ, ૪ ટોપ, શાર્પી, સ્મોક મશીન, ટ્રસ") },
                modifier = Modifier.fillMaxWidth().testTag("input_sound_details"),
                leadingIcon = { Icon(Icons.Default.Speaker, contentDescription = null, tint = GoldPrimary) },
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedLabelColor = GoldPrimary,
                    cursorColor = GoldPrimary
                )
            )

            // Pricing & Automatic Balance calculation
            Divider(color = NavyBorder, thickness = 1.dp)

            Text(
                text = "હિસાબ & રકમ (Pricing & Automatic Balance)",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = { totalAmountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("કુલ રકમ (₹) *") },
                    placeholder = { Text("દા.ત. 25000") },
                    modifier = Modifier.weight(1f).testTag("input_total_amount"),
                    leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium.copy(color = GoldPrimary)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )

                OutlinedTextField(
                    value = advanceAmountStr,
                    onValueChange = { advanceAmountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("એડવાન્સ જમા (₹)") },
                    placeholder = { Text("દા.ત. 5000") },
                    modifier = Modifier.weight(1f).testTag("input_advance_amount"),
                    leadingIcon = { Text("₹", style = MaterialTheme.typography.titleMedium.copy(color = LaserCyan)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedLabelColor = GoldPrimary,
                        cursorColor = GoldPrimary
                    )
                )
            }

            // Live Auto-Calculated Remaining Balance Card
            Card(
                modifier = Modifier.fillMaxWidth().testTag("card_balance_preview"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (remainingAmount > 0) WarningOrange.copy(alpha = 0.12f) else SuccessGreen.copy(alpha = 0.12f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (remainingAmount > 0) WarningOrange.copy(alpha = 0.6f) else SuccessGreen.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "આપોઆપ ગણતરી કરેલ બાકી રકમ (Remaining Balance)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (remainingAmount > 0) "₹${remainingAmount.toLong()} (બાકી)" else "₹0 (ચૂકતે)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = if (remainingAmount > 0) WarningOrange else SuccessGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }

                    Icon(
                        imageVector = if (remainingAmount > 0) Icons.Default.PendingActions else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (remainingAmount > 0) WarningOrange else SuccessGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Notes / Terms
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ખાસ નોંધ / શરતો") },
                placeholder = { Text("દા.ત. જનરેટર ડીઝલ પાર્ટીનું રહેશે") },
                modifier = Modifier.fillMaxWidth().testTag("input_notes"),
                leadingIcon = { Icon(Icons.Default.Note, contentDescription = null, tint = TextSecondary) },
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedLabelColor = GoldPrimary,
                    cursorColor = GoldPrimary
                )
            )

            // Status Selector
            Text(
                text = "બુકિંગ સ્થિતિ (Status):",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusOptions.forEach { opt ->
                    FilterChip(
                        selected = status == opt,
                        onClick = { status = opt },
                        label = { Text(opt, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when(opt) {
                                "કન્ફર્મ" -> LaserCyan
                                "પૂર્ણ" -> SuccessGreen
                                else -> ErrorRed
                            },
                            selectedLabelColor = NavyDeep,
                            containerColor = NavySurface,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            // Error Message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, ErrorRed)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Submit Button
            GoldButton(
                text = if (isEditMode) "બુકિંગ અપડેટ કરો" else "બુકિંગ સેવ કરો અને બિલ બનાવો",
                icon = Icons.Default.Check,
                modifier = Modifier.testTag("btn_save_booking"),
                onClick = {
                    if (customerName.isBlank()) {
                        errorMessage = "કૃપા કરીને ગ્રાહકનું નામ દાખલ કરો."
                        return@GoldButton
                    }
                    if (customerMobile.length < 10) {
                        errorMessage = "કૃપા કરીને ૧૦ અંકનો સાચો મોબાઇલ નંબર દાખલ કરો."
                        return@GoldButton
                    }
                    if (customerVillage.isBlank() || eventVillage.isBlank()) {
                        errorMessage = "ગ્રાહકનું ગામ અને પ્રોગ્રામનું સ્થળ બંને દાખલ કરો."
                        return@GoldButton
                    }
                    if (totalAmount <= 0) {
                        errorMessage = "કૃપા કરીને માન્ય કુલ રકમ દાખલ કરો."
                        return@GoldButton
                    }

                    errorMessage = null

                    val bookingToSave = Booking(
                        id = existingBooking?.id ?: 0,
                        billNumber = billNumber,
                        customerName = customerName.trim(),
                        customerMobile = customerMobile.trim(),
                        customerVillage = customerVillage.trim(),
                        eventVillage = eventVillage.trim(),
                        eventType = eventType,
                        arrivalDate = arrivalDate,
                        arrivalTime = arrivalTime,
                        departureDate = departureDate,
                        departureTime = departureTime,
                        soundSetupDetails = soundSetupDetails.trim(),
                        totalAmount = totalAmount,
                        advanceAmount = advanceAmount,
                        remainingAmount = remainingAmount,
                        notes = notes.trim(),
                        status = status,
                        createdAt = existingBooking?.createdAt ?: System.currentTimeMillis(),
                        isSynced = false
                    )

                    if (isEditMode) {
                        viewModel.updateBooking(bookingToSave) {
                            onSaved(bookingToSave.id)
                        }
                    } else {
                        viewModel.saveBooking(bookingToSave) { savedId ->
                            onSaved(savedId)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
