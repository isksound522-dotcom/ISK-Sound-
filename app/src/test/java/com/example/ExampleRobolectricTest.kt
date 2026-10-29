package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Booking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("I. S. K Sound & Live DJ", appName)
  }

  @Test
  fun `verify remaining amount calculation`() {
    val totalAmount = 25000.0
    val advanceAmount = 5000.0
    val remaining = totalAmount - advanceAmount
    val booking = Booking(
      id = 1,
      billNumber = "ISK-2026-001",
      customerName = "પટેલ રમેશભાઈ",
      customerMobile = "9876543210",
      customerVillage = "ગોંડલ",
      eventVillage = "રાજકોટ",
      eventType = "લગ્ન પ્રસંગ",
      arrivalDate = "2026-11-15",
      arrivalTime = "06:00 PM",
      departureDate = "2026-11-16",
      departureTime = "02:00 AM",
      soundSetupDetails = "૨ બેઝ + ૪ ટોપ + શાર્પી લાઈટ્સ",
      totalAmount = totalAmount,
      advanceAmount = advanceAmount,
      remainingAmount = remaining,
      status = "કન્ફર્મ"
    )

    assertEquals(20000.0, booking.remainingAmount, 0.01)
    assertEquals("ISK-2026-001", booking.billNumber)
  }
}
