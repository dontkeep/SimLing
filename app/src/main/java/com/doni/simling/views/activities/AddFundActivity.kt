package com.doni.simling.views.activities

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doni.simling.databinding.ActivityAddFundBinding
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.viewmodels.FundViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.doni.simling.helper.setupCurrencyFormatting
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFundBinding
    private var receiptImagePath: String? = null

    private val viewModel: FundViewModel by viewModels()

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

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun handleSaveButtonClick() {
        lifecycleScope.launch {
            val amountText = binding.textFieldTotal.editText?.text?.toString() ?: ""
            val cleanString = amountText.replace("[Rp,.\\s]".toRegex(), "")

            if (cleanString.isEmpty()) {
                Toast.makeText(
                    this@AddFundActivity,
                    "Jumlah tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val description = binding.textFieldDesc.editText?.text?.toString()
            if (description.isNullOrEmpty()) {
                Toast.makeText(
                    this@AddFundActivity,
                    "Deskripsi tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

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
            val isIncomeRequestBody = createRequestBody("false")
            val statusRequestBody = createRequestBody("Accepted")
            val blockRequestBody = createRequestBody("")
            val time = System.currentTimeMillis().toString()
            val timeRequest = createRequestBody(time)

            receiptImagePath = viewModel.imageUri.value ?: receiptImagePath
            val receiptImagePart = createImagePart("image", receiptImagePath)

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
                block = blockRequestBody,
                time = timeRequest
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
                binding.textFieldDesc.isEnabled = false
                binding.textFieldTotal.isEnabled = false
                binding.root.foreground = "#80000000".toColorInt().toDrawable()
            }

            is Resource.Success -> {
                resetUIState()
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Berhasil menambahkan pengeluaran", Toast.LENGTH_SHORT).show()
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
}