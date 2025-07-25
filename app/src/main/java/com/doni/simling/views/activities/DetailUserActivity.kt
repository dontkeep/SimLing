package com.doni.simling.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doni.simling.databinding.ActivityDetailUserBinding
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.models.connections.responses.DataItemUser
import com.doni.simling.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    @Inject
    lateinit var roleManager: RoleManager

    private lateinit var binding: ActivityDetailUserBinding
    private val usersViewModel: UserViewModel by viewModels()
    private var userId: Int = -1
    private var userRole: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.progressBar.visibility = View.GONE

        val currentRole = roleManager.getRole()
        if (currentRole == RoleManager.ROLE_ADMIN) {
            binding.icEdit.visibility = View.VISIBLE
        } else {
            binding.icEdit.visibility = View.GONE
        }

        val userData = intent.getParcelableExtra<DataItemUser>("USER_DATA")
        Log.d("DetailUserActivity", "User data: $userData")

        userRole = userData?.roleId ?: -1
        userId = userData?.id ?: -1
        Log.d("DetailUserActivity", "User ID: $userId")

        if (userRole == 1) {
            binding.icEdit.visibility = View.GONE
        }

        if (userId == -1) {
            finish()
            return
        }

        // Populate user data
        userData?.let { user ->
            binding.tvName.text = user.name ?: "N/A"
            binding.tvHp.text = user.phoneNo ?: "N/A"
            binding.tvAddress.text = user.address ?: "N/A"
            binding.tvStatus.text = user.status ?: "N/A"
        }
        binding.icEdit.setOnClickListener {
            userData?.let { user ->
                val intent = Intent(this, EditUserActivity::class.java).apply {
                    putExtra("USER_DATA", user)
                }
                startActivity(intent)
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        observeDeleteState()
    }

    private fun observeDeleteState() {
        usersViewModel.deleteUser.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        resource.data?.message ?: "User deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        resource.message ?: "Failed to delete user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}