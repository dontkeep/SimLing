package com.doni.simling.views.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.doni.simling.databinding.ActivityDetailIncomeBinding
import com.doni.simling.helper.DateHelper.formatDate
import com.doni.simling.helper.Resource
import com.doni.simling.helper.formatCurrency
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.viewmodels.FundViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailIncomeActivity : AppCompatActivity() {

    @Inject
    lateinit var roleManager: RoleManager

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
        binding.acceptBtn.visibility = View.GONE
        binding.rejectBtn.visibility = View.GONE

        fundId = intent.getIntExtra("FUND_ID", -1)
        if (fundId == -1) {
            finish()
            return
        }

        setupObservers(fundId)
        setupListeners()
        observeActions()

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
                            binding.tvDetailName.text = fund.userName ?: "N/A"
                            binding.tvAddress.text = fund.block ?: "N/A"
                            binding.tvAmount.text = formatCurrency(fund.amount ?: 0)
                            binding.tvDetailDate.text = formatDate(fund.createdAt)

                            fund.image?.let { imagePath ->
                                Glide.with(this@DetailIncomeActivity)
                                    .load(imagePath)
                                    .into(binding.ivDetailPhoto)
                            }

                            if (roleManager.getRole() == RoleManager.ROLE_ADMIN) {
                                when (fund.status) {
                                    "Pending" -> {
                                        binding.acceptBtn.visibility = View.VISIBLE
                                        binding.rejectBtn.visibility = View.VISIBLE
                                        binding.tvPending.visibility = View.VISIBLE
                                    }
                                    "Accepted" -> {
                                        binding.tvAccepted.visibility = View.VISIBLE
                                    }
                                    "Rejected" -> {
                                        binding.tvRejected.visibility = View.VISIBLE
                                    }
                                }
                            } else {
                                when (fund.status) {
                                    "Pending" -> binding.tvPending.visibility = View.VISIBLE
                                    "Accepted" -> binding.tvAccepted.visibility = View.VISIBLE
                                    "Rejected" -> binding.tvRejected.visibility = View.VISIBLE
                                }
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

    private fun setupListeners() {
        binding.acceptBtn.setOnClickListener {
            showConfirmationDialog(
                title = "Konfirmasi Penerimaan",
                message = "Apakah Anda yakin ingin menerima pembayaran ini?",
                positiveAction = { fundViewModel.acceptIncome(fundId) }
            )
        }

        binding.rejectBtn.setOnClickListener {
            showConfirmationDialog(
                title = "Konfirmasi Penolakan",
                message = "Apakah Anda yakin ingin menolak pembayaran ini?",
                positiveAction = { fundViewModel.rejectIncome(fundId) }
            )
        }
    }

    private fun observeActions() {
        lifecycleScope.launch {
            launch {
                fundViewModel.acceptState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@DetailIncomeActivity,
                                "Pembayaran diterima",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
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

            launch {
                fundViewModel.rejectState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@DetailIncomeActivity,
                                "Pembayaran ditolak",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
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
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        positiveAction: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ya") { _, _ ->
                positiveAction()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}