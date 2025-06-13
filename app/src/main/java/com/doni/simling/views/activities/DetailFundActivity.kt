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
import com.doni.simling.databinding.ActivityDetailFundBinding
import com.doni.simling.helper.DateHelper.formatDate
import com.doni.simling.helper.Resource
import com.doni.simling.helper.formatCurrency
import com.doni.simling.viewmodels.FundViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DetailFundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFundBinding
    private val fundViewModel: FundViewModel by viewModels()
    private var fundId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailFundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fundId = intent.getIntExtra("FUND_ID", -1)
        if (fundId == -1) {
            finish()
            return
        }

        setupObservers(fundId)

        fundViewModel.getFundIncomeDetail(fundId)

        binding.backBtn.setOnClickListener {
            finish()
        }
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
                            binding.tvAmount.text = formatCurrency(fund.amount ?: 0)
                            binding.tvDate.text = formatDate(fund.createdAt)
                            binding.tvDesc.text = fund.description ?: "N/A"

                            fund.image?.let { imagePath ->
                                Glide.with(this@DetailFundActivity)
                                    .load(imagePath)
                                    .into(binding.ivDetailPhoto)
                            }

                        }
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DetailFundActivity,
                            resource.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}