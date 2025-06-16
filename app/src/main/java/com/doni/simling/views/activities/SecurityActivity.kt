package com.doni.simling.views.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d("SecurityActivity", "Fine location permission granted")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("SecurityActivity", "Coarse location permission granted")
            }
            else -> {
                Toast.makeText(this, "Izin lokasi diperlukan untuk fitur keamanan", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        checkLocationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController
        setupSmoothBottomMenu()

        val targetFragment = intent.getStringExtra("TARGET_FRAGMENT")
        when (targetFragment) {
            "HomeFragment" -> navController.navigate(R.id.homeFragment)
            "PlanFragment" -> navController.navigate(R.id.securityFragment)
            else -> navController.navigate(R.id.homeFragment)
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("SecurityActivity", "Location permission already granted")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showPermissionRationale()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Izin Lokasi Diperlukan")
            .setMessage("Aplikasi membutuhkan akses lokasi untuk fitur keamanan dan pelacakan")
            .setPositiveButton("OK") { _, _ ->
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton("Tolak") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Beberapa fitur mungkin tidak berfungsi", Toast.LENGTH_LONG).show()
            }
            .create()
            .show()
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