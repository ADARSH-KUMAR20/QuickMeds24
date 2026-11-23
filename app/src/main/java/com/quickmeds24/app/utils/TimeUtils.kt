package com.quickmeds24.app.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * TimeUtils
 * Computes and formats delivery ETA in Indian Standard Time (IST - Asia/Kolkata / Delhi timing)
 * e.g., "11:24 PM"
 */
object TimeUtils {

    fun formatIstDeliveryTime(etaMinutes: Int): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        cal.add(Calendar.MINUTE, etaMinutes)
        val sdf = SimpleDateFormat("hh:mm a", Locale("en", "IN"))
        sdf.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return sdf.format(cal.time).uppercase()
    }

    fun formatCurrentIstTime(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        val sdf = SimpleDateFormat("hh:mm a", Locale("en", "IN"))
        sdf.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        return sdf.format(cal.time).uppercase()
    }
}

