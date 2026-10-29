package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billNumber: String,          // યુનિક બિલ નંબર (દા.ત. ISK-2026-001)
    val customerName: String,        // ગ્રાહકનું નામ
    val customerMobile: String,      // મોબાઇલ નંબર
    val customerVillage: String,     // ગ્રાહકનું ગામ
    val eventVillage: String,        // પ્રોગ્રામનું સ્થળ/ગામ
    val eventType: String,           // પ્રસંગ પ્રકાર (લગ્ન, રાસ-ગરબા, વગેરે)
    val arrivalDate: String,         // પહોંચવાની તારીખ (YYYY-MM-DD)
    val arrivalTime: String,         // પહોંચવાનો સમય (દા.ત. 05:00 PM)
    val departureDate: String,       // છૂટા થવાની તારીખ (YYYY-MM-DD)
    val departureTime: String,       // છૂટા થવાનો સમય (દા.ત. 02:00 AM)
    val soundSetupDetails: String,   // સાઉન્ડ અને લાઈટ સિસ્ટમ વિગત
    val totalAmount: Double,         // કુલ રકમ
    val advanceAmount: Double,       // એડવાન્સ જમા રકમ
    val remainingAmount: Double,     // બાકી રકમ (totalAmount - advanceAmount)
    val notes: String = "",          // વિશેષ નોંધ / નિયમો
    val status: String = "કન્ફર્મ",    // કન્ફર્મ, પૂર્ણ, રદ
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false    // ક્લાઉડ સિંક સ્ટેટસ
)
