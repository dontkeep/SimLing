package com.doni.simling.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doni.simling.R
import com.doni.simling.databinding.ActivitySplashScreenBinding
import com.doni.simling.helper.manager.RoleManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    @Inject
    lateinit var roleManager: RoleManager

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            delay(1.seconds)
            val role = roleManager.getRole()

            val intent = when (role) {
                RoleManager.ROLE_ADMIN, RoleManager.ROLE_WARGA -> {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }

                RoleManager.ROLE_SECURITY -> {
                    startActivity(Intent(this@SplashScreen, SecurityActivity::class.java))
                    finish()
                }

                else -> {
                    startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                    finish()
                }
            }
        }

    }
}