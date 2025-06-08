package com.doni.simling.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import com.doni.simling.databinding.FragmentHomeBinding
import com.doni.simling.helper.DateHelper
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.TokenManager
import com.doni.simling.viewmodels.LogoutViewModel
import com.doni.simling.views.activities.AddFamilyActivity
import com.doni.simling.views.activities.CameraActivity
import com.doni.simling.views.activities.FundsActivity
import com.doni.simling.views.activities.IncomeActivity
import com.doni.simling.views.activities.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var roleManager: RoleManager

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogoutViewModel by viewModels()

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
            viewModel.logout()
            observeLogoutState()
        }

        binding.btnScan.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
