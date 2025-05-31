package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doni.simling.helper.Resource
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val repositories: DataRepositories
) : ViewModel() {
    private val logoutState = MutableLiveData<Resource<Boolean>>()
    val logoutStateLiveData: LiveData<Resource<Boolean>> get() = logoutState

    fun logout() {
        viewModelScope.launch {
            try {
                repositories.logout().collect {
                    when (it) {
                        is Resource.Success -> {
                            logoutState.value = Resource.Success(true)
                        }
                        is Resource.Error -> {
                            logoutState.value = Resource.Error(it.message ?: "Logout failed")
                        }
                        is Resource.Loading -> {
                            logoutState.value = Resource.Loading()
                        }
                    }
                }
            } catch (e: Exception) {
                logoutState.value = Resource.Error(e.message ?: "Logout failed")
            }
        }
    }
}