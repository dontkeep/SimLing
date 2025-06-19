package com.doni.simling.helper

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.format

object DateHelper {

    fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
        val currentDate = Date()
        val formattedDate = dateFormat.format(currentDate)

        return formattedDate.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "-"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = parser.parse(dateString)
            if (date != null) formatter.format(date) else "-"
        } catch (e: Exception) {
            "-"
        }
    }

    fun getCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        val currentMonth = String.format("%02d", calendar.get(Calendar.MONTH) + 1)

        return currentMonth
    }

    fun getCurrentYear(): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        return currentYear.toString()

    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            Locale.getDefault(),
            "%02d%02d%02d%02d%02d%02d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR) % 100,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }
}