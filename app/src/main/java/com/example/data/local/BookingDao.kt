package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY arrivalDate DESC, id DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    fun getBookingById(id: Long): Flow<Booking?>

    @Query("SELECT * FROM bookings WHERE arrivalDate = :date OR departureDate = :date ORDER BY id DESC")
    fun getBookingsByDate(date: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE customerName LIKE '%' || :query || '%' OR customerVillage LIKE '%' || :query || '%' OR eventVillage LIKE '%' || :query || '%' OR customerMobile LIKE '%' || :query || '%' OR billNumber LIKE '%' || :query || '%' ORDER BY arrivalDate DESC")
    fun searchBookings(query: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings ORDER BY id DESC LIMIT 1")
    suspend fun getLastBooking(): Booking?

    @Query("SELECT * FROM bookings")
    suspend fun getAllBookingsSnapshot(): List<Booking>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<Booking>)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Long)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
