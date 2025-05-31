package com.doni.simling.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.doni.simling.databinding.FragmentHomeBinding
import com.doni.simling.helper.DateHelper
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.views.activities.AddFamilyActivity
import com.doni.simling.views.activities.AddFundActivity
import com.doni.simling.views.activities.FundsActivity
import com.doni.simling.views.activities.IncomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var roleManager: RoleManager

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
