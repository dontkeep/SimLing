package com.doni.simling.views.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doni.simling.R
import com.doni.simling.databinding.ActivityAddIncomeBinding
import com.doni.simling.helper.Resource
import com.doni.simling.helper.setupCurrencyFormatting
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.viewmodels.FundViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddIncomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddIncomeBinding

    private var receiptImagePath: String? = null
    private val viewModel: FundViewModel by viewModels()

    private var selectedDateTime: Calendar = Calendar.getInstance()

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            processSelectedImage(uri)
        } else {
            Snackbar.make(binding.root, "Tidak ada gambar yang dipilih", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupCurrencyFormatting(binding.textFieldTotal.editText as TextInputEditText)

        binding.cardImage.setOnClickListener {
            openGallery()
        }

        binding.etDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.saveBtn.setOnClickListener {
            handleSaveButtonClick()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun showDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDateTime.set(year, month, day)
                showTimePickerWithSeconds()
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerWithSeconds() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_time_picker, null)

        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hour_picker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.minute_picker)
        val secondPicker = dialogView.findViewById<NumberPicker>(R.id.second_picker)

        // Configure hour picker
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.value = selectedDateTime.get(Calendar.HOUR_OF_DAY)

        // Configure minute picker
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = selectedDateTime.get(Calendar.MINUTE)

        // Configure second picker
        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.value = selectedDateTime.get(Calendar.SECOND)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourPicker.value)
                selectedDateTime.set(Calendar.MINUTE, minutePicker.value)
                selectedDateTime.set(Calendar.SECOND, secondPicker.value)
                updateDateTimeDisplay()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateDateTimeDisplay() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())

        binding.etDate.setText(dateFormat.format(selectedDateTime.time))
        binding.tvName.text = displayDateFormat.format(selectedDateTime.time)
    }

    private fun handleSaveButtonClick() {
        lifecycleScope.launch {
            val amountText = binding.textFieldTotal.editText?.text?.toString() ?: ""

            val cleanString = amountText.replace("[Rp,.\\s]".toRegex(), "")
            if (cleanString.isEmpty()) {
                Toast.makeText(
                    this@AddIncomeActivity,
                    "Jumlah tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val address = binding.textFieldAddress.editText?.text?.toString()
            if (address.isNullOrEmpty()) {
                Toast.makeText(
                    this@AddIncomeActivity,
                    "Blok tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val date = binding.etDate.text?.toString()
            if (date.isNullOrEmpty()) {
                Toast.makeText(
                    this@AddIncomeActivity,
                    "Tanggal tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            if (receiptImagePath == null) {
                Toast.makeText(
                    this@AddIncomeActivity,
                    "Harap pilih gambar struk",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            // Convert the display date (dd/MM/yyyy HH:mm:ss) to API format (yyyy-MM-dd HH:mm:ss)
            val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val displayDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val apiDateString = try {
                val dateObj = displayDateFormat.parse(date)
                apiDateFormat.format(dateObj!!)
            } catch (e: Exception) {
                date
            }

            val amountRequestBody = createRequestBody(cleanString)
            val descriptionRequestBody = createRequestBody("")
            val isIncomeRequestBody = createRequestBody("true")
            val statusRequestBody = createRequestBody("Pending")
            val blockRequestBody = createRequestBody(address)
            val dateRequestBody = createRequestBody(apiDateString)

            // Log all request data
            Log.d("AddIncomeActivity", "===== REQUEST DATA ======")
            Log.d("AddIncomeActivity", "Amount: $cleanString")
            Log.d("AddIncomeActivity", "Description: (empty)")
            Log.d("AddIncomeActivity", "Is Income: true")
            Log.d("AddIncomeActivity", "Status: Pending")
            Log.d("AddIncomeActivity", "Block: $address")
            Log.d("AddIncomeActivity", "Date (display): $date")
            Log.d("AddIncomeActivity", "Date (API format): $apiDateString")
            Log.d("AddIncomeActivity", "Image Path: $receiptImagePath")

            receiptImagePath = viewModel.imageUri.value ?: receiptImagePath
            val receiptImagePart = createImagePart("image", receiptImagePath)

            if (receiptImagePart == null) {
                Toast.makeText(
                    this@AddIncomeActivity,
                    "Gagal memproses gambar",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            Log.d("AddIncomeActivity", "Image Part: ${receiptImagePart.body?.contentType()}")

            viewModel.addFund(
                amount = amountRequestBody,
                description = descriptionRequestBody,
                isIncome = isIncomeRequestBody,
                status = statusRequestBody,
                image = receiptImagePart,
                block = blockRequestBody,
                time = dateRequestBody
            ).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun processSelectedImage(uri: Uri) {
        val file = uriToFile(uri)
        receiptImagePath = file.absolutePath
        viewModel.setImageUri(receiptImagePath!!)
        binding.imageView.setImageURI(uri)
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun createRequestBody(value: String?): okhttp3.RequestBody =
        value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createImagePart(partName: String, uriPath: String?): MultipartBody.Part? {
        Log.d("CreateListActivity", "Image Path: $uriPath")
        uriPath?.let { path ->
            val file = File(path)
            if (!file.exists()) {
                Log.e("CreateListActivity", "File not found at path: $path")
                return null
            }

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestBody)
        }
        return null
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("receipt_", ".jpg", cacheDir)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun handleResource(resource: Resource<CreateFundResponse>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.saveBtn.isEnabled = false
                binding.textFieldTotal.isEnabled = false
                binding.textFieldAddress.isEnabled = false
                binding.root.foreground = "#80000000".toColorInt().toDrawable()
            }

            is Resource.Success -> {
                resetUIState()
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Berhasil Membayar Kas", Toast.LENGTH_SHORT).show()
                finish()
            }

            is Resource.Error -> {
                resetUIState()
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error ${resource.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetUIState() {
        binding.progressBar.visibility = View.GONE
        binding.textFieldTotal.isEnabled = true
        binding.textFieldAddress.isEnabled = true
        binding.root.foreground = null
    }
}