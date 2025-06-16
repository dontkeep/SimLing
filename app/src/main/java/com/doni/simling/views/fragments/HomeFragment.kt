package com.doni.simling.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.simling.databinding.FragmentHomeBinding
import com.doni.simling.helper.DateHelper
import com.doni.simling.helper.Resource
import com.doni.simling.helper.formatCurrency
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.models.connections.responses.HomeResponse
import com.doni.simling.viewmodels.HomeViewModel
import com.doni.simling.viewmodels.LogoutViewModel
import com.doni.simling.viewmodels.SecurityViewModel
import com.doni.simling.views.activities.AddFamilyActivity
import com.doni.simling.views.activities.CameraActivity
import com.doni.simling.views.activities.FundsActivity
import com.doni.simling.views.activities.IncomeActivity
import com.doni.simling.views.activities.LoginActivity
import com.doni.simling.views.adapters.SecurityRecordByUserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var roleManager: RoleManager

    @Inject
    lateinit var locationManager: LocationManager

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogoutViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val securityViewModel: SecurityViewModel by viewModels()


    private val gpsRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (isGpsEnabled()) {
            startCameraActivity()
        } else {
            Toast.makeText(requireContext(), "GPS masih dimatikan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDate.text = DateHelper.getCurrentFormattedDate()

        homeViewModel.getHomeData()
        observeHomeData()

        val currentRole = roleManager.getRole()
        Log.d("HomeFragment", "Current Role: $currentRole")

        when (currentRole) {
            RoleManager.ROLE_WARGA -> {
                binding.roleHeader.visibility = View.GONE
                binding.addFamilyBtn.visibility = View.GONE
            }

            RoleManager.ROLE_SECURITY -> {
                binding.roleHeader.visibility = View.GONE
                binding.addFamilyBtn.visibility = View.GONE
                binding.listFundBtn.visibility = View.GONE
                binding.addFundBtn.visibility = View.GONE
                binding.cardWarga.visibility = View.GONE
                binding.cardSummary.visibility = View.GONE
                binding.cardMenuSecurity.visibility = View.VISIBLE
                val securityAdapter = rvSecuritySetup()
                observeSecurityData(securityAdapter)
            }
        }

        binding.addFamilyBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AddFamilyActivity::class.java))
        }

        binding.listFundBtn.setOnClickListener {
            startActivity(Intent(requireContext(), IncomeActivity::class.java))
        }

        binding.addFundBtn.setOnClickListener {
            startActivity(Intent(requireContext(), FundsActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.btnScan.setOnClickListener {
            checkGpsAndOpenCamera()
        }
    }

    private fun checkGpsAndOpenCamera() {
        try {
            if (isGpsEnabled()) {
                startCameraActivity()
            } else {
                showEnableGpsDialog()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal memeriksa status GPS", Toast.LENGTH_SHORT).show()
            Log.e("HomeFragment", "Error checking GPS status", e)
        }
    }

    private fun isGpsEnabled(): Boolean {
        return try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error checking GPS provider", e)
            false
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                viewModel.logout()
                observeLogoutState()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun showEnableGpsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("GPS Dimatikan")
            .setMessage("Aplikasi membutuhkan GPS untuk fitur scanning. Aktifkan GPS sekarang?")
            .setPositiveButton("Ya") { _, _ ->
                enableGps()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Fitur scanning membutuhkan GPS", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun enableGps() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        try {
            gpsRequestLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat membuka pengaturan GPS", Toast.LENGTH_SHORT).show()
            Log.e("HomeFragment", "Error opening GPS settings", e)
        }
    }

    private fun startCameraActivity() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        startActivity(intent)
    }

    private fun rvSecuritySetup(): SecurityRecordByUserAdapter {
        val adapter = SecurityRecordByUserAdapter { item ->
            openLocationInMap(item.latitude, item.longitude)
        }
        binding.rvTodayRecords.adapter = adapter
        binding.rvTodayRecords.layoutManager = LinearLayoutManager(requireContext())
        return adapter
    }

    private fun observeSecurityData(adapter: SecurityRecordByUserAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            securityViewModel.securityRecordsByUserByDay(DateHelper.getCurrentDate())
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            adapter.submitList(resource.data?.data)
                        }
                        is Resource.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun observeLogoutState() {
        viewModel.logoutStateLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    binding.logoutBtn.isEnabled = false
                    binding.addFamilyBtn.isEnabled = false
                    binding.listFundBtn.isEnabled = false
                    binding.addFundBtn.isEnabled = false
                    binding.cardWarga.isEnabled = false
                    binding.cardSummary.isEnabled = false
                    binding.cardMenuSecurity.isEnabled = false
                    binding.root.foreground = "#80000000".toColorInt().toDrawable()
                }

                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }

                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeHomeData() {
        homeViewModel.homeState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    val data = resource.data
                    binding.progressIndicator.visibility = View.GONE
                    updateUI(data)
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(data: HomeResponse?) {
        data?.let {
            binding.tvCurrentAmount.text = formatCurrency(it.currentCredit ?: 0)
            binding.tvIncomePerMonth.text = formatCurrency(it.totalIncome ?: 0)
            binding.tvFundsPerMonth.text = formatCurrency(it.totalExpense ?: 0)
            binding.tvSecurityAmount.text = "${it.totalSecurity} Petugas"
            binding.tvMemberAmount.text = "${it.totalUsers} Kepala Keluarga"
            binding.tvNewMember.text = "${it.usersAddedThisMonth} Keluarga"
        }
    }

    private fun openLocationInMap(latitude: String?, longitude: String?) {
        if (latitude == null || longitude == null) {
            Toast.makeText(requireContext(), "Koordinat tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Jika Google Maps tidak terinstall, buka browser
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal membuka peta", Toast.LENGTH_SHORT).show()
            Log.e("SecurityFragment", "Error opening map", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
