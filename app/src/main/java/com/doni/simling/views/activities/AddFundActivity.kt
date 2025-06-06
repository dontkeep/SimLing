package com.doni.simling.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doni.simling.R
import com.doni.simling.databinding.ActivityAddFundBinding
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.viewmodels.AddFundViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.doni.simling.helper.setupCurrencyFormatting
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFundBinding
    private var receiptImagePath: String? = null

    private val viewModel: AddFundViewModel by viewModels()

//    private val requestPermissionsLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            val intent = Intent(this, CameraActivity::class.java)
//            startActivity(intent)
//        } else {
//            Toast.makeText(this, R.string.permission_request_denied, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun allPermissionsGranted(): Boolean {
//        val cameraGranted = ContextCompat.checkSelfPermission(
//            this, REQUIRED_CAMERA_PERMISSION
//        ) == PackageManager.PERMISSION_GRANTED
//
//        return cameraGranted
//    }

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
        binding = ActivityAddFundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupCurrencyFormatting(binding.textFieldTotal.editText as TextInputEditText)

        binding.cardImage.setOnClickListener {
            openGallery()
            Log.d("AddFundActivity", "Image Clicked")
        }

        binding.saveBtn.setOnClickListener {
            handleSaveButtonClick()
        }
    }

    private fun handleSaveButtonClick() {
        lifecycleScope.launch {
            // Get the text from the EditText, not the TextInputLayout
            val amountText = binding.textFieldTotal.editText?.text?.toString() ?: ""
            val cleanString = amountText.replace("[Rp,.\\s]".toRegex(), "")

            // Check if the string is not empty before converting to Long
            if (cleanString.isEmpty()) {
                Toast.makeText(
                    this@AddFundActivity,
                    "Jumlah tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            // Validate description
            val description = binding.textFieldDesc.editText?.text?.toString()
            if (description.isNullOrEmpty()) {
                Toast.makeText(
                    this@AddFundActivity,
                    "Deskripsi tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            // Validate image (if required)
            if (receiptImagePath == null) {
                Toast.makeText(
                    this@AddFundActivity,
                    "Harap pilih gambar struk",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val amountRequestBody = createRequestBody(cleanString)
            val descriptionRequestBody = createRequestBody(description)
            val isIncomeRequestBody = createRequestBody("true")
            val statusRequestBody = createRequestBody("Pending")
            val blockRequestBody = createRequestBody("")

            receiptImagePath = viewModel.imageUri.value ?: receiptImagePath
            val receiptImagePart = createImagePart("receipt_image", receiptImagePath)

            // Check if image part was created successfully
            if (receiptImagePart == null) {
                Toast.makeText(
                    this@AddFundActivity,
                    "Gagal memproses gambar",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            viewModel.addFund(
                amount = amountRequestBody,
                description = descriptionRequestBody,
                isIncome = isIncomeRequestBody,
                status = statusRequestBody,
                image = receiptImagePart,
                block = blockRequestBody
            ).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun processSelectedImage(uri: Uri) {
        binding.imageView.setImageURI(uri)
        receiptImagePath = uri.toString()
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun createRequestBody(value: String?): okhttp3.RequestBody =
        value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createImagePart(partName: String, uriPath: String?): MultipartBody.Part? {
        Log.d("CreateListActivity", "Image Path: $uriPath")
        uriPath?.let { path ->
            val uri = Uri.parse(path)
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image.jpeg")
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestBody)
        }
        return null
    }

    private fun handleResource(resource: Resource<CreateFundResponse>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.saveBtn.isEnabled = false
                binding.textFieldDesc.isEnabled = false
                binding.textFieldTotal.isEnabled = false
                binding.root.foreground = "#80000000".toColorInt().toDrawable()
            }

            is Resource.Success -> {
                resetUIState()
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Berhasil menambahkan pengeluaran", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }

            is Resource.Error -> {
                resetUIState()
                binding.progressBar.visibility = View.GONE
                Log.d("CreateListActivity", "Error: ${resource.message}")
                Toast.makeText(this, "Error ${resource.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetUIState() {
        binding.progressBar.visibility = View.GONE
        binding.saveBtn.isEnabled = true
        binding.textFieldDesc.isEnabled = true
        binding.textFieldTotal.isEnabled = true
        binding.root.foreground = null
    }

//    companion object {
//        private const val REQUIRED_CAMERA_PERMISSION = Manifest.permission.CAMERA
//    }
}