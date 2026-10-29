package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.ui.components.GoldButton
import com.example.ui.components.IskTopAppBar
import com.example.ui.components.OutlinedGoldButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun BillReceiptScreen(
    booking: Booking,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val billText = remember(booking) {
        generateGujaratiShareableText(booking)
    }

    Scaffold(
        topBar = {
            IskTopAppBar(
                title = "ડીજે બિલ & રસીદ",
                subtitle = "બિલ નં: ${booking.billNumber}",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(billText))
                            Toast.makeText(context, "બિલ ક્લિપબોર્ડમાં કોપી થઈ ગયું!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "કોપી કરો", tint = GoldPrimary)
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
            // Printable & Shareable Gujarati Receipt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bill_receipt_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36)),
                border = BorderStroke(2.dp, GoldPrimary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Top Sacred Heading & DJ Name
                    Text(
                        text = "॥ શ્રી ગણેશાય નમઃ ॥",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "I. S. K SOUND & LIVE DJ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp,
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "હાઇટેક સાઉન્ડ સિસ્ટમ, શાર્પી લાઈટ્સ અને લાઈવ ડીજે ઇવેન્ટ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = GoldPrimary.copy(alpha = 0.5f),
                        thickness = 1.5.dp
                    )

                    // Bill Details Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "બિલ નં: ${booking.billNumber}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = LaserCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "પ્રસંગ: ${booking.eventType}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                        StatusBadge(status = booking.status)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Customer Details Table
                    ReceiptSectionTitle(title = "ગ્રાહક અને સ્થળ વિગત")
                    ReceiptRow(label = "ગ્રાહકનું નામ:", value = booking.customerName)
                    ReceiptRow(label = "મોબાઇલ નંબર:", value = booking.customerMobile)
                    ReceiptRow(label = "ગ્રાહકનું ગામ:", value = booking.customerVillage)
                    ReceiptRow(label = "પ્રોગ્રામ સ્થળ/ગામ:", value = booking.eventVillage)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Schedule Table
                    ReceiptSectionTitle(title = "સમય અને તારીખ વિગત")
                    ReceiptRow(label = "પહોંચવાની તારીખ:", value = "${booking.arrivalDate} (${booking.arrivalTime})")
                    ReceiptRow(label = "છૂટા થવાની તારીખ:", value = "${booking.departureDate} (${booking.departureTime})")
                    if (booking.soundSetupDetails.isNotBlank()) {
                        ReceiptRow(label = "સાઉન્ડ સેટઅપ:", value = booking.soundSetupDetails)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Financial ledger Box
                    ReceiptSectionTitle(title = "હિસાબ પત્રક (Payment Ledger)")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            ReceiptRow(label = "કુલ નક્કી કરેલ રકમ:", value = "₹ ${booking.totalAmount.toLong()}", isBold = true, textColor = TextPrimary)
                            ReceiptRow(label = "એડવાન્સ જમા રકમ:", value = "₹ ${booking.advanceAmount.toLong()}", isBold = true, textColor = LaserCyan)
                            Divider(modifier = Modifier.padding(vertical = 6.dp), color = NavyBorder)
                            ReceiptRow(
                                label = "ચોખ્ખી બાકી રકમ:",
                                value = if (booking.remainingAmount > 0) "₹ ${booking.remainingAmount.toLong()} (બાકી)" else "₹ 0 (ચૂકતે)",
                                isBold = true,
                                textColor = if (booking.remainingAmount > 0) WarningOrange else SuccessGreen
                            )
                        }
                    }

                    // Special Notes
                    if (booking.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "ખાસ શરતો / નોંધ: ${booking.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Signatures Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "____________________",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ગ્રાહકની સહી",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "I. S. K SOUND & DJ",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ઓથોરાઇઝ્ડ સહી / સિક્કો",
                                style = MaterialTheme.typography.labelSmall.copy(color = GoldLight)
                            )
                        }
                    }
                }
            }

            // Share via WhatsApp Button
            GoldButton(
                text = "WhatsApp પર ગુજરાતી બિલ શેર કરો",
                icon = Icons.Default.Share,
                modifier = Modifier.testTag("btn_share_whatsapp"),
                onClick = {
                    shareToWhatsApp(context, booking.customerMobile, billText)
                }
            )

            // Print / Save PDF Button
            OutlinedGoldButton(
                text = "બિલ પ્રિન્ટ કરો / PDF તરીકે સેવ કરો",
                icon = Icons.Default.Print,
                modifier = Modifier.testTag("btn_print_bill"),
                onClick = {
                    printBillHtml(context, booking)
                }
            )

            // General System Share
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "I. S. K Sound & Live DJ - બિલ #${booking.billNumber}")
                        putExtra(Intent.EXTRA_TEXT, billText)
                    }
                    context.startActivity(Intent.createChooser(intent, "બિલ શેર કરો"))
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NavyBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = LaserCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("અન્ય એપ્લિકેશનમાં શેર કરો")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ReceiptSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            color = GoldLight,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        ),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    textColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 13.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = textColor,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        )
    }
}

private fun generateGujaratiShareableText(b: Booking): String {
    return """
        *॥ શ્રી ગણેશાય નમઃ ॥*
        🎶 *I. S. K SOUND & LIVE DJ* 🎶
        _હાઇટેક સાઉન્ડ સિસ્ટમ અને લાઈવ ડીજે ઇવેન્ટ_
        ------------------------------------------
        📄 *બિલ નંબર:* ${b.billNumber}
        🎉 *પ્રસંગ:* ${b.eventType}
        👤 *ગ્રાહકનું નામ:* ${b.customerName}
        📞 *મોબાઇલ નંબર:* ${b.customerMobile}
        🏠 *ગામ:* ${b.customerVillage}
        📍 *પ્રોગ્રામ સ્થળ:* ${b.eventVillage}
        ------------------------------------------
        🕒 *પહોંચવાનો સમય:* ${b.arrivalDate} (${b.arrivalTime})
        🏁 *છૂટા થવાનો સમય:* ${b.departureDate} (${b.departureTime})
        🔊 *સેટઅપ વિગત:* ${b.soundSetupDetails}
        ------------------------------------------
        💰 *કુલ રકમ:* ₹ ${b.totalAmount.toLong()}
        💵 *એડવાન્સ જમા:* ₹ ${b.advanceAmount.toLong()}
        ⚠️ *બાકી રકમ:* ₹ ${b.remainingAmount.toLong()} (${if (b.remainingAmount <= 0) "ચૂકતે (Paid)" else "બાકી"})
        ------------------------------------------
        📝 *ખાસ નોંધ:* ${if (b.notes.isNotBlank()) b.notes else "લાઈટ/જનરેટર આયોજકનું રહેશે."}
        
        *આભાર! I. S. K SOUND & LIVE DJ*
    """.trimIndent()
}

private fun shareToWhatsApp(context: Context, mobile: String, text: String) {
    try {
        val cleanNumber = mobile.replace("+", "").replace(" ", "").replace("-", "")
        val formattedNumber = if (cleanNumber.length == 10) "91$cleanNumber" else cleanNumber
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=" + android.net.Uri.encode(text))
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general share
        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "WhatsApp પર શેર કરો"))
    }
}

private fun printBillHtml(context: Context, b: Booking) {
    try {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlDocument = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: sans-serif; padding: 24px; color: #111; }
                    .header { text-align: center; border-bottom: 2px solid #D4AF37; padding-bottom: 12px; margin-bottom: 16px; }
                    .title { font-size: 24px; font-weight: bold; color: #0A192F; }
                    .subtitle { font-size: 13px; color: #666; }
                    .meta { display: flex; justify-content: space-between; margin-bottom: 14px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 10px; margin-bottom: 16px; }
                    th, td { border: 1px solid #ddd; padding: 8px 10px; font-size: 13px; }
                    th { background-color: #f5f5f5; text-align: left; }
                    .total-box { background: #fafafa; border: 2px solid #D4AF37; padding: 12px; margin-top: 14px; }
                    .footer { margin-top: 40px; display: flex; justify-content: space-between; }
                    .sig { text-align: center; width: 200px; border-top: 1px solid #444; padding-top: 6px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <p style="margin:0; font-weight:bold; color:#B8860B;">॥ શ્રી ગણેશાય નમઃ ॥</p>
                    <div class="title">I. S. K SOUND & LIVE DJ</div>
                    <div class="subtitle">હાઇટેક સાઉન્ડ સિસ્ટમ, શાર્પી લાઈટ્સ અને લાઈવ ડીજે ઇવેન્ટ</div>
                </div>
                
                <div class="meta">
                    <div><strong>બિલ નંબર:</strong> ${b.billNumber}</div>
                    <div><strong>પ્રસંગ:</strong> ${b.eventType}</div>
                </div>

                <table>
                    <tr><th colspan="2">ગ્રાહક અને સ્થળ વિગત</th></tr>
                    <tr><td>ગ્રાહકનું નામ</td><td><strong>${b.customerName}</strong></td></tr>
                    <tr><td>મોબાઇલ નંબર</td><td>${b.customerMobile}</td></tr>
                    <tr><td>ગ્રાહકનું ગામ</td><td>${b.customerVillage}</td></tr>
                    <tr><td>પ્રોગ્રામ સ્થળ / ગામ</td><td>${b.eventVillage}</td></tr>
                    <tr><th colspan="2">કાર્યક્રમ શેડ્યૂલ</th></tr>
                    <tr><td>પહોંચવાનો સમય</td><td>${b.arrivalDate} (${b.arrivalTime})</td></tr>
                    <tr><td>છૂટા થવાનો સમય</td><td>${b.departureDate} (${b.departureTime})</td></tr>
                    <tr><td>સાઉન્ડ સેટઅપ</td><td>${b.soundSetupDetails}</td></tr>
                </table>

                <div class="total-box">
                    <table>
                        <tr><td><strong>કુલ નક્કી કરેલ રકમ:</strong></td><td><strong>₹ ${b.totalAmount.toLong()}</strong></td></tr>
                        <tr><td><strong>એડવાન્સ જમા રકમ:</strong></td><td><strong>₹ ${b.advanceAmount.toLong()}</strong></td></tr>
                        <tr style="background:#fff3cd;"><td><strong>ચોખ્ખી બાકી રકમ:</strong></td><td><strong style="color:#d9534f;">₹ ${b.remainingAmount.toLong()}</strong></td></tr>
                    </table>
                </div>

                <p style="font-size:12px; color:#555; margin-top:14px;"><strong>શરતો:</strong> ${if (b.notes.isNotBlank()) b.notes else "લાઈટિંગ પોઈન્ટ અને જનરેટરની જવાબદારી આયોજકની રહેશે."}</p>

                <div class="footer">
                    <div class="sig">ગ્રાહકની સહી</div>
                    <div class="sig">I. S. K SOUND & DJ વતી</div>
                </div>
            </body>
            </html>
        """.trimIndent()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false

            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("ISK_DJ_Bill_${b.billNumber}")
                printManager.print(
                    "ISK_DJ_Bill_${b.billNumber}",
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)
    } catch (e: Exception) {
        Toast.makeText(context, "પ્રિન્ટિંગ સુવિધા ઉપલબ્ધ નથી", Toast.LENGTH_SHORT).show()
    }
}
