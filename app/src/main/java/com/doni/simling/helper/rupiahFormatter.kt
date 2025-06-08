package com.doni.simling.helper

import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

private var currentText = ""

fun setupCurrencyFormatting(editText: TextInputEditText) {
    editText.addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: android.text.Editable?) {
            if (s.toString() != currentText) {
                editText.removeTextChangedListener(this)

                val cleanString = s.toString().replace("[Rp,.\\s]".toRegex(), "")
                if (cleanString.isNotEmpty()) {
                    val parsed = cleanString.toDouble()
                    val formatted = formatRupiah(parsed)

                    currentText = formatted
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                }

                editText.addTextChangedListener(this)
            }
        }
    })
}

private fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    return numberFormat.format(amount).replace("Rp", "Rp ").replace(",00", "")
}

