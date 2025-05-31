package com.doni.simling.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.simling.R
import com.doni.simling.databinding.ActivityDebugMenuBinding
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.TokenManager
import com.doni.simling.views.activities.LoginActivity
import com.doni.simling.views.activities.MainActivity
import com.doni.simling.views.activities.SecurityActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DebugMENU : AppCompatActivity() {

    private lateinit var binding: ActivityDebugMenuBinding

    @Inject
    lateinit var roleManager: RoleManager
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDebugMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButtonListeners()
    }

    private fun setupButtonListeners() {  // Fixed typo in method name (was "setupButtonListeners")
        binding.btnLogin.setOnClickListener {
            roleManager.clearRole() // Clear role when going to login
            tokenManager.clearToken()
            navigateToLoginActivity()
        }

        binding.btnAdmin.setOnClickListener {
            roleManager.saveRole(RoleManager.ROLE_ADMIN)
            navigateToMainActivity()
        }

        binding.btnWarga.setOnClickListener {
            roleManager.saveRole(RoleManager.ROLE_WARGA)
            navigateToMainActivity()
        }

        binding.btnSecurity.setOnClickListener {
            roleManager.saveRole(RoleManager.ROLE_SECURITY)
            navigateToSecurityActivity()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToSecurityActivity() {
        val intent = Intent(this, SecurityActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}