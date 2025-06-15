package com.doni.simling.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.simling.R
import com.doni.simling.databinding.FragmentHomeBinding
import com.doni.simling.databinding.FragmentSecurityBinding
import com.doni.simling.helper.DateHelper
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.viewmodels.SecurityViewModel
import com.doni.simling.views.adapters.GetAllSecurityRecordAdapter
import com.doni.simling.views.adapters.SecurityRecordByUserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import kotlin.time.Duration

@AndroidEntryPoint
class SecurityFragment : Fragment() {

    @Inject
    lateinit var roleManager: RoleManager

    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!!

    private val securityViewModel: SecurityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentRole = roleManager.getRole()
        Log.d("HomeFragment", "Current Role: $currentRole")

        when (currentRole) {
            RoleManager.ROLE_SECURITY -> {
                binding.roleHeader.visibility = View.GONE

                val adapter = SecurityRecordByUserAdapter()
                binding.rvPresence.adapter = adapter
                binding.rvPresence.setHasFixedSize(true)
                binding.rvPresence.visibility = View.VISIBLE
                binding.rvPresence.layoutManager = LinearLayoutManager(requireContext())

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    securityViewModel.securityRecordsByUser.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                binding.progressIndicator.visibility = View.GONE
                                val records = resource.data?.data
                                Log.d("SecurityFragment", "Records: $records")
                                if (records != null) adapter.submitList(records)
                            }
                            is Resource.Error -> {
                                binding.progressIndicator.visibility = View.GONE
                                Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_LONG).show()
                            }
                            is Resource.Loading -> {
                                binding.progressIndicator.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            RoleManager.ROLE_WARGA -> {
                binding.roleHeader.visibility = View.GONE
                binding.progressIndicator.visibility = View.GONE

                val adapter = SecurityRecordByUserAdapter()
                binding.rvPresence.adapter = adapter
                binding.rvPresence.setHasFixedSize(true)
                binding.rvPresence.visibility = View.VISIBLE
                binding.rvPresence.layoutManager = LinearLayoutManager(requireContext())

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    securityViewModel.securityRecordsByDay(DateHelper.getCurrentDate()).collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                binding.progressIndicator.visibility = View.GONE
                                val records = resource.data?.data
                                if (records != null) adapter.submitList(records)
                            }
                            is Resource.Error -> {
                                binding.progressIndicator.visibility = View.GONE
                                Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_LONG).show()
                            }
                            is Resource.Loading -> {
                                binding.progressIndicator.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            RoleManager.ROLE_ADMIN -> {
                binding.roleHeader.visibility = View.VISIBLE
                binding.progressIndicator.visibility = View.GONE

                val adapter = GetAllSecurityRecordAdapter()
                binding.rvPresence.adapter = adapter
                binding.rvPresence.setHasFixedSize(true)
                binding.rvPresence.visibility = View.VISIBLE
                binding.rvPresence.layoutManager = LinearLayoutManager(requireContext())

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    securityViewModel.getAllSecurityRecords().collectLatest { pagingData ->
                        binding.progressIndicator.visibility = View.GONE
                        adapter.submitData(pagingData)
                    }
                }
            }
            else -> {
                Toast.makeText(requireContext(), "Role tidak dikenali", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}