package com.doni.simling.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.doni.simling.databinding.FragmentUsersBinding
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.viewmodels.UserViewModel
import com.doni.simling.views.activities.DetailUserActivity
import com.doni.simling.views.adapters.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.paging.PagingData

@AndroidEntryPoint
class UsersFragment : Fragment() {

    @Inject
    lateinit var roleManager: RoleManager

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupSearchView()
        observeUsers()
        checkUserRole()
    }

    private fun setupAdapter() {
        adapter = UsersAdapter { user ->
            val intent = Intent(requireContext(), DetailUserActivity::class.java).apply {
                putExtra("USER_DATA", user)
            }
            startActivity(intent)
        }
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMembers.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchView.hide()
            binding.cardParent.visibility = View.VISIBLE
            filterUsers(binding.searchView.text.toString())
            true
        }

        binding.searchView.editText.doAfterTextChanged { editable ->
            editable?.let {
                binding.cardParent.visibility = View.GONE
                filterUsers(it.toString())
            }
        }
    }

    private fun observeUsers() {
        lifecycleScope.launch {
            viewModel.getUsers().collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun filterUsers(query: String) {
        val filteredList = if (query.isEmpty()) {
            viewModel.allUsers
        } else {
            viewModel.allUsers.filter { user ->
                user.name?.contains(query, ignoreCase = true) == true ||
                        user.email?.contains(query, ignoreCase = true) == true ||
                        user.phoneNo?.contains(query, ignoreCase = true) == true
            }
        }

        lifecycleScope.launch {
            adapter.submitData(PagingData.from(filteredList))
        }
    }

    private fun checkUserRole() {
        val currentRole = roleManager.getRole()
        when (currentRole) {
            RoleManager.ROLE_WARGA -> binding.roleHeader.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
