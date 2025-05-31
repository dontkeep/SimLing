package com.doni.simling.views.activities

import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.doni.simling.R
import com.doni.simling.databinding.ActivitySecurityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecurityBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController
        setupSmoothBottomMenu()

        val targetFragment = intent.getStringExtra("TARGET_FRAGMENT")
        when (targetFragment) {
            "HomeFragment" -> navController.navigate(R.id.homeFragment)
            "PlanFragment" -> navController.navigate(R.id.securityFragment)
            else -> navController.navigate(R.id.homeFragment)
        }
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.menu_security)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
        binding.bottomBar.itemIconTintActive = getColor(R.color.navIconColor)
        binding.bottomBar.barIndicatorColor = getColor(R.color.navIndicator)
        binding.bottomBar.setOnItemSelectedListener { position ->
            when (position) {
                0 -> navController.navigate(R.id.homeFragment)
                else -> navController.navigate(R.id.securityFragment)
            }
        }
    }
}