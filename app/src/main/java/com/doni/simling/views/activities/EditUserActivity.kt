package com.doni.simling.views.activities

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.simling.databinding.ActivityEditUserBinding
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.DataItemUser
import com.doni.simling.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditUserBinding
    private val usersViewModel: UserViewModel by viewModels()
    private var userData: DataItemUser? = null
    private val status = listOf("Aktif", "Pindah", "Wafat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get user data from intent
        userData = intent.getParcelableExtra("USER_DATA")

        // Populate fields with existing user data
        userData?.let { user ->
            binding.textFieldName.editText?.setText(user.name ?: "")
            binding.textFieldPhone.editText?.setText(user.phoneNo ?: "")
            // Note: Password fields are left empty intentionally for security
        }

        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, status)
        (binding.textFieldStatus.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.saveBtn.setOnClickListener {
            updateUser()
        }

        observeEditState()
    }

    private fun updateUser() {

        val name = binding.textFieldName.editText?.text.toString().trim()
        val phoneNo = binding.textFieldPhone.editText?.text.toString().trim()
        val password = binding.textFieldPassword.editText?.text.toString().trim()
        val confirmPassword = binding.textFieldPasswordVerify.editText?.text.toString().trim()
        val address = userData?.address ?: ""
        val status = binding.textFieldStatus.editText?.text.toString().trim()
        val roleId = userData?.roleId ?: 2 // Default to regular user role if not specified

        // Basic validation
        if (name.isEmpty()) {
            binding.textFieldName.error = "Nama tidak boleh kosong"
            return
        } else {
            binding.textFieldName.error = null
        }

        if (phoneNo.isEmpty()) {
            binding.textFieldPhone.error = "Nomor HP tidak boleh kosong"
            return
        } else {
            binding.textFieldPhone.error = null
        }

        // Only validate password if it's not empty (optional change)
        if (password.isNotEmpty() && password != confirmPassword) {
            binding.textFieldPasswordVerify.error = "Password tidak sama"
            return
        } else {
            binding.textFieldPasswordVerify.error = null
        }

        userData?.id?.let { userId ->
            usersViewModel.editUser(
                id = userId,
                name = name,
                phoneNo = phoneNo,
                email = userData?.email ?: "", // Keep original email
                password = if (password.isNotEmpty()) password else "", // Only send if changed
                address = address,
                status = status,
                roleId = roleId
            )
        }
    }

    private fun observeEditState() {
        usersViewModel.editUser.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.saveBtn.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    Toast.makeText(
                        this,
                        resource.data?.message ?: "Berhasil memperbarui data warga",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.saveBtn.isEnabled = true
                    Toast.makeText(
                        this,
                        resource.message ?: "Gagal memperbarui data warga",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}