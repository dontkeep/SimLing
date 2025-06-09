package com.doni.simling.helper

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    fun getCurrentFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
        val currentDate = Date()
        val formattedDate = dateFormat.format(currentDate)

        return formattedDate.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
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
}