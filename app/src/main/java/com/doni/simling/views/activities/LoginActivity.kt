package com.doni.simling.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.simling.databinding.ActivityLoginBinding
import com.doni.simling.helper.Resource
import com.doni.simling.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        loginViewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginButton.isEnabled = false
                    binding.edPhoneNo.isEnabled = false
                    binding.edPassword.isEnabled = false
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                    val role = loginViewModel.getRole()
                    val intent = when (role) {
                        3 -> Intent(this, SecurityActivity::class.java)
                        else -> Intent(this, MainActivity::class.java)
                    }

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loginButton.isEnabled = true
                    binding.edPhoneNo.isEnabled = true
                    binding.edPassword.isEnabled = true
                    Snackbar.make(
                        binding.root,
                        "Akun tidak ditemukan",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val phoneNo = binding.edPhoneNo.editText?.text.toString()
            val password = binding.edPassword.editText?.text.toString()
            if (phoneNo.isNotBlank() && password.isNotBlank()) {
                loginViewModel.login(phoneNo, password)
            } else {
                Snackbar.make(
                    binding.root,
                    "Isi semua field terlebih dahulu",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.privacyBtn.setOnClickListener {
            val privacyUrl = "https://www.privacypolicies.com/live/dbae5141-ed03-47a1-bb1b-01adf13227e9"
            val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
            startActivity(intent)
        }
    }
}