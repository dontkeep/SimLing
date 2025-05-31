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
}