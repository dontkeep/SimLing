package com.doni.simling.helper

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.NumberPicker
import com.doni.simling.R

class MonthYearPickerDialog(
    context: Context,
    private val onDateSet: (month: Int, year: Int) -> Unit
) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_month_year_picker)

        val monthPicker = findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = findViewById<NumberPicker>(R.id.yearPicker)
        val okButton = findViewById<Button>(R.id.okButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        monthPicker?.apply {
            minValue = 0
            maxValue = 11
            displayedValues = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )
        }

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        yearPicker?.apply {
            minValue = 1980
            maxValue = currentYear + 50
        }

        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        monthPicker?.value = currentMonth
        yearPicker?.value = currentYear

        okButton?.setOnClickListener {
            val selectedMonth = monthPicker.value
            val selectedYear = yearPicker?.value ?: currentYear
            onDateSet(selectedMonth, selectedYear)
            dismiss()
        }

        cancelButton?.setOnClickListener {
            dismiss()
        }
    }
}