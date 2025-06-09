package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: DataRepositories,
    private val roleManager: RoleManager
): ViewModel() {
    private val _loginState = MutableLiveData<Resource<Boolean>>()
    val loginState: LiveData<Resource<Boolean>> get() = _loginState

    fun login(phoneNo: String, password: String) {
        viewModelScope.launch {
            try {
                repository.login(phoneNo, password).collect {
                    when (it) {
                        is Resource.Loading -> {
                            _loginState.value = Resource.Loading()
                        }
                        is Resource.Success -> {
                            _loginState.value = Resource.Success(true)
                        }
                        is Resource.Error -> {
                            _loginState.value = Resource.Error(it.message ?: "Unknown error")
                        }
                    }
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getRole(): Int? {
        return roleManager.getRole()
    }
}