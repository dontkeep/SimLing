package com.doni.simling.views.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.doni.simling.R
import com.doni.simling.databinding.ActivityDetailIncomeBinding
import com.doni.simling.helper.DateHelper.formatDate
import com.doni.simling.helper.Resource
import com.doni.simling.helper.formatCurrency
import com.doni.simling.viewmodels.FundViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class DetailIncomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailIncomeBinding
    private val fundViewModel: FundViewModel by viewModels()
    private var fundId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Dapatkan ID dari intent
        fundId = intent.getIntExtra("FUND_ID", -1)
        if (fundId == -1) {
            finish()
            return
        }

        setupObservers(fundId)
//        setupListeners()

        // Load data
        fundViewModel.getFundIncomeDetail(fundId)
    }

    private fun setupObservers(fundId: Int) {
        lifecycleScope.launch {
            fundViewModel.fundDetail.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.data?.let { fund ->
                            binding.tvDetailName.text = fund.userName ?: "N/A"
                            binding.tvAddress.text = fund.block ?: "N/A"
                            binding.tvAmount.text = formatCurrency(fund.amount ?: 0)
                            binding.tvDetailDate.text = formatDate(fund.createdAt)

                            fund.image?.let { imagePath ->
                                Glide.with(this@DetailIncomeActivity)
                                    .load(imagePath)
                                    .into(binding.ivDetailPhoto)
                            }
                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DetailIncomeActivity,
                            resource.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

//    private fun setupListeners() {
//        binding.acceptBtn.setOnClickListener {
//            fundViewModel.acceptFund(fundId)
//        }
//
//        binding.rejectBtn.setOnClickListener {
//            fundViewModel.rejectFund(fundId)
//        }
//
//        // Observers untuk accept/reject
//        fundViewModel.acceptState.observe(this) { resource ->
//            when (resource) {
//                is Resource.Success -> {
//                    Toast.makeText(this, "Pembayaran diterima", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                is Resource.Error -> {
//                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
//                }
//                else -> {}
//            }
//        }
//
//        fundViewModel.rejectState.observe(this) { resource ->
//            when (resource) {
//                is Resource.Success -> {
//                    Toast.makeText(this, "Pembayaran ditolak", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                is Resource.Error -> {
//                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
//                }
//                else -> {}
//            }
//        }
//    }
}