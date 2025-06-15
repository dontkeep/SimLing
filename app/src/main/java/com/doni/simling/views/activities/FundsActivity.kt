package com.doni.simling.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.simling.databinding.ActivityFundsBinding
import com.doni.simling.helper.DateHelper.getCurrentMonth
import com.doni.simling.helper.DateHelper.getCurrentYear
import com.doni.simling.helper.MonthYearPickerDialog
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.RoleManager.Companion.ROLE_ADMIN
import com.doni.simling.helper.manager.RoleManager.Companion.ROLE_WARGA
import com.doni.simling.viewmodels.FundViewModel
import com.doni.simling.views.adapters.FundAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class FundsActivity : AppCompatActivity() {

    @Inject
    lateinit var roleManager: RoleManager

    private lateinit var binding: ActivityFundsBinding
    private val fundViewModel: FundViewModel by viewModels()
    private lateinit var fundAdapter: FundAdapter

    private var selectedMonth: String = getCurrentMonth()
    private var selectedYear: String = getCurrentYear()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFundsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupRoleSpecificUI()

        binding.icCalendar.setOnClickListener {
            showMonthYearPickerDialog()
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddFundActivity::class.java)
            startActivity(intent)
        }

        observeViewModel(getCurrentMonth(), getCurrentYear())

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        fundAdapter = FundAdapter { fundItem ->
            val intent = Intent(this, DetailFundActivity::class.java).apply {
                putExtra("FUND_ID", fundItem.id)
            }
            startActivity(intent)
        }

        binding.rvFunds.apply {
            adapter = fundAdapter
            layoutManager = LinearLayoutManager(this@FundsActivity)
        }
    }

    private fun observeViewModel(month: String, year: String) {
        lifecycleScope.launch {
            fundViewModel.getAllFundPaging(month, year).collect {
                fundAdapter.submitData(it)
            }
        }
    }

    private fun setupRoleSpecificUI() {
        val role = roleManager.getRole()
        when (role) {
            ROLE_ADMIN -> {
                binding.floatingActionButton.visibility = View.VISIBLE
            }
            ROLE_WARGA-> {
                binding.floatingActionButton.visibility = View.GONE
                binding.tvRole.visibility = View.GONE
            }
        }
    }

    private fun showMonthYearPickerDialog() {
        val dialog = MonthYearPickerDialog(this) { month, year ->
            selectedMonth = String.format("%02d", month + 1)
            selectedYear = year.toString()

            observeViewModel(selectedMonth, selectedYear)
        }

        dialog.show()
    }
}