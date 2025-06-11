package com.doni.simling.views.activities

import android.R
import android.os.Bundle
import android.util.Log
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
            val name = binding.textFieldName.editText?.text.toString()
            val phoneNo = binding.textFieldPhone.editText?.text.toString()
            val email = binding.textFieldEmail.editText?.text.toString()
            val password = binding.textFieldPassword.editText?.text.toString()
            val confirmPassword = binding.textFieldPasswordVerify.editText?.text.toString()
            val address = binding.textFieldAddress.editText?.text.toString()
            val role = binding.textFieldCategory.editText?.text.toString()

            when {
                name.isEmpty() -> showToast("Nama tidak boleh kosong")
                phoneNo.isEmpty() -> showToast("Nomor HP tidak boleh kosong")
                email.isEmpty() -> showToast("Email tidak boleh kosong")
                password.isEmpty() -> showToast("Password tidak boleh kosong")
                password != confirmPassword -> showToast("Password tidak sama")
                address.isEmpty() -> showToast("Alamat tidak boleh kosong")
                role.isEmpty() -> showToast("Role harus dipilih")
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
            setupObservers()
            Log.d(
                "AddFamilyActivity", "" +
                        "Role: $role" +
                        "Name: $name" +
                        "Phone: $phoneNo" +
                        "Email: $email" +
                        "Password: $password" +
                        "Confirm Password: $confirmPassword" +
                        "Address: $address"
            )
        }
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
                    finish()
                    showToast("Berhasil menambahkan")
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    showToast("Terjadi kesalahan")
                    Log.d("AddFamilyActivity", "Error: ${state.message}")
                }
            }
            binding.saveBtn.isEnabled = true
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}