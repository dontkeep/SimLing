package com.doni.simling.views.activities

import android.R
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.simling.databinding.ActivityAddFamilyBinding
import com.doni.simling.helper.Resource
import com.doni.simling.viewmodels.AddFamilyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFamilyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFamilyBinding
    private val viewModel: AddFamilyViewModel by viewModels()
    private val roles = listOf("Admin", "Warga", "Security")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupViews() {
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, roles)
        (binding.textFieldCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.saveBtn.setOnClickListener {
            val name = binding.textFieldName.editText?.text.toString().trim()
            val phoneNo = binding.textFieldPhone.editText?.text.toString().trim()
            val email = binding.textFieldEmail.editText?.text.toString().trim()
            val password = binding.textFieldPassword.editText?.text.toString().trim()
            val confirmPassword = binding.textFieldPasswordVerify.editText?.text.toString().trim()
            val address = binding.textFieldAddress.editText?.text.toString().trim()
            val role = binding.textFieldCategory.editText?.text.toString().trim()

            // Reset errors
            binding.textFieldName.error = null
            binding.textFieldPhone.error = null
            binding.textFieldEmail.error = null
            binding.textFieldPassword.error = null
            binding.textFieldPasswordVerify.error = null
            binding.textFieldAddress.error = null
            binding.textFieldCategory.error = null

            when {
                name.isEmpty() -> {
                    binding.textFieldName.error = "Nama tidak boleh kosong"
                    binding.textFieldName.requestFocus()
                }
                phoneNo.isEmpty() -> {
                    binding.textFieldPhone.error = "Nomor HP tidak boleh kosong"
                    binding.textFieldPhone.requestFocus()
                }
                phoneNo.length < 10 -> {
                    binding.textFieldPhone.error = "Nomor HP minimal 10 digit"
                    binding.textFieldPhone.requestFocus()
                }
                email.isEmpty() -> {
                    binding.textFieldEmail.error = "Email tidak boleh kosong"
                    binding.textFieldEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.textFieldEmail.error = "Format email tidak valid"
                    binding.textFieldEmail.requestFocus()
                }
                password.isEmpty() -> {
                    binding.textFieldPassword.error = "Password tidak boleh kosong"
                    binding.textFieldPassword.requestFocus()
                }
                password.length < 6 -> {
                    binding.textFieldPassword.error = "Password minimal 6 karakter"
                    binding.textFieldPassword.requestFocus()
                }
                password != confirmPassword -> {
                    binding.textFieldPasswordVerify.error = "Password tidak sama"
                    binding.textFieldPasswordVerify.requestFocus()
                }
                address.isEmpty() -> {
                    binding.textFieldAddress.error = "Alamat tidak boleh kosong"
                    binding.textFieldAddress.requestFocus()
                }
                role.isEmpty() -> {
                    binding.textFieldCategory.error = "Role harus dipilih"
                    binding.textFieldCategory.requestFocus()
                }
                else -> {
                    viewModel.addFamily(
                        name = name,
                        phoneNo = phoneNo,
                        email = email,
                        password = password,
                        address = address,
                        roleId = when (role) {
                            "Admin" -> 1
                            "Warga" -> 2
                            "Security" -> 3
                            else -> 0
                        }
                    )
                }
            }
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.createUserResponse.observe(this) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.saveBtn.isEnabled = false
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Berhasil menambahkan")
                    finish()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    showToast(state.message ?: "Terjadi kesalahan")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}