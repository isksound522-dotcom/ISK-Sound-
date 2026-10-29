package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.model.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingRepository(private val bookingDao: BookingDao) {

    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()

    fun searchBookings(query: String): Flow<List<Booking>> {
        return bookingDao.searchBookings(query.trim())
    }

    fun getBookingsByDate(date: String): Flow<List<Booking>> {
        return bookingDao.getBookingsByDate(date)
    }

    fun getBookingById(id: Long): Flow<Booking?> {
        return bookingDao.getBookingById(id)
    }

    suspend fun generateNextBillNumber(): String = withContext(Dispatchers.IO) {
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val lastBooking = bookingDao.getLastBooking()
        val nextNumber = if (lastBooking != null) {
            val parts = lastBooking.billNumber.split("-")
            if (parts.size >= 3) {
                (parts.last().toIntOrNull() ?: 0) + 1
            } else {
                (lastBooking.id.toInt()) + 1
            }
        } else {
            1
        }
        String.format(Locale.US, "ISK-%s-%03d", currentYear, nextNumber)
    }

    suspend fun insertBooking(booking: Booking): Long = withContext(Dispatchers.IO) {
        bookingDao.insertBooking(booking)
    }

    suspend fun updateBooking(booking: Booking) = withContext(Dispatchers.IO) {
        bookingDao.updateBooking(booking)
    }

    suspend fun deleteBooking(booking: Booking) = withContext(Dispatchers.IO) {
        bookingDao.deleteBooking(booking)
    }

    suspend fun deleteBookingById(id: Long) = withContext(Dispatchers.IO) {
        bookingDao.deleteBookingById(id)
    }

    /**
     * Safe JSON Backup Generator for I. S. K Sound & Live DJ
     */
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val bookings = bookingDao.getAllBookingsSnapshot()
        val root = JSONObject()
        root.put("appName", "I. S. K Sound & Live DJ")
        root.put("version", "1.0")
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("totalRecords", bookings.size)

        val array = JSONArray()
        for (b in bookings) {
            val item = JSONObject()
            item.put("id", b.id)
            item.put("billNumber", b.billNumber)
            item.put("customerName", b.customerName)
            item.put("customerMobile", b.customerMobile)
            item.put("customerVillage", b.customerVillage)
            item.put("eventVillage", b.eventVillage)
            item.put("eventType", b.eventType)
            item.put("arrivalDate", b.arrivalDate)
            item.put("arrivalTime", b.arrivalTime)
            item.put("departureDate", b.departureDate)
            item.put("departureTime", b.departureTime)
            item.put("soundSetupDetails", b.soundSetupDetails)
            item.put("totalAmount", b.totalAmount)
            item.put("advanceAmount", b.advanceAmount)
            item.put("remainingAmount", b.remainingAmount)
            item.put("notes", b.notes)
            item.put("status", b.status)
            item.put("createdAt", b.createdAt)
            item.put("isSynced", b.isSynced)
            array.put(item)
        }
        root.put("bookings", array)
        root.toString(2)
    }

    /**
     * Safe Restore from JSON backup file
     */
    suspend fun restoreFromJson(jsonContent: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonContent)
            val array = root.getJSONArray("bookings")
            val list = mutableListOf<Booking>()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val booking = Booking(
                    id = 0, // autoGenerate new ID or let Room assign to prevent collision
                    billNumber = item.optString("billNumber", "ISK-${System.currentTimeMillis()}"),
                    customerName = item.optString("customerName", ""),
                    customerMobile = item.optString("customerMobile", ""),
                    customerVillage = item.optString("customerVillage", ""),
                    eventVillage = item.optString("eventVillage", ""),
                    eventType = item.optString("eventType", "લગ્ન પ્રસંગ"),
                    arrivalDate = item.optString("arrivalDate", ""),
                    arrivalTime = item.optString("arrivalTime", ""),
                    departureDate = item.optString("departureDate", ""),
                    departureTime = item.optString("departureTime", ""),
                    soundSetupDetails = item.optString("soundSetupDetails", ""),
                    totalAmount = item.optDouble("totalAmount", 0.0),
                    advanceAmount = item.optDouble("advanceAmount", 0.0),
                    remainingAmount = item.optDouble("remainingAmount", 0.0),
                    notes = item.optString("notes", ""),
                    status = item.optString("status", "કન્ફર્મ"),
                    createdAt = item.optLong("createdAt", System.currentTimeMillis()),
                    isSynced = true
                )
                list.add(booking)
            }
            if (list.isNotEmpty()) {
                bookingDao.insertAll(list)
            }
            Result.success(list.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllAsSynced() = withContext(Dispatchers.IO) {
        val list = bookingDao.getAllBookingsSnapshot()
        for (b in list) {
            bookingDao.updateBooking(b.copy(isSynced = true))
        }
    }
}
