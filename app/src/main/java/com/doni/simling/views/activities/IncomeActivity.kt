package com.doni.simling.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.simling.R
import com.doni.simling.databinding.ActivityIncomeBinding
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.RoleManager.Companion.ROLE_ADMIN
import com.doni.simling.helper.manager.RoleManager.Companion.ROLE_WARGA
import com.doni.simling.viewmodels.FundViewModel
import com.doni.simling.views.adapters.FundsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IncomeActivity : AppCompatActivity() {

    @Inject
    lateinit var roleManager: RoleManager

    private lateinit var binding: ActivityIncomeBinding
    private val fundViewModel: FundViewModel by viewModels()
    private lateinit var fundAdapter: FundsAdapter

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupRoleSpecificUI()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddIncomeActivity::class.java)
            startActivity(intent)
        }

        val calendar = Calendar.getInstance()
        val currentMonth = String.format("%02d", calendar.get(Calendar.MONTH) + 1) // MONTH is 0-indexed
        val currentYear = calendar.get(Calendar.YEAR).toString()

        observeViewModel(currentMonth, currentYear)
    }

    private fun setupRecyclerView() {
        fundAdapter = FundsAdapter()
        binding.rvFunds.apply {
            adapter = fundAdapter
            layoutManager = LinearLayoutManager(this@IncomeActivity)
        }
    }

    private fun observeViewModel(month: String, year: String) {
        lifecycleScope.launch {
            fundViewModel.getAllIncomePaging(month, year).collect {
                fundAdapter.submitData(it)
            }
        }
    }

    private fun setupRoleSpecificUI() {
        val role = roleManager.getRole()
        when (role) {
            ROLE_ADMIN -> {
                binding.floatingActionButton.visibility = View.GONE
            }
            ROLE_WARGA -> {
                binding.floatingActionButton.visibility = View.VISIBLE
                binding.tvRole.visibility = View.GONE
            }
        }
    }
}