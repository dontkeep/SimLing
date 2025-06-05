package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.CreateUserResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFamilyViewModel @Inject constructor(
    private val repository: DataRepositories
) : ViewModel() {

    private val _createUserResponse = MutableLiveData<Resource<CreateUserResponse>>()
    val createUserResponse: LiveData<Resource<CreateUserResponse>> = _createUserResponse

    fun addFamily(
        name: String,
        phoneNo: String,
        email: String,
        password: String,
        address: String,
        roleId: Int
    ) {
        viewModelScope.launch {
            repository.addFamily(
                name = name,
                phoneNo = phoneNo,
                email = email,
                password = password,
                address = address,
                roleId = roleId
            ).collect { resource ->
                _createUserResponse.value = resource
            }
        }
    }
}