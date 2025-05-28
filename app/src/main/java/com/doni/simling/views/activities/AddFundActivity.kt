package com.doni.simling.views.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.simling.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_fund)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .build()

        val etDate = findViewById<TextInputEditText>(R.id.etDate)

        etDate.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Format tanggal jika perlu
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selection))
            etDate.setText(formattedDate)
        }

    }
}