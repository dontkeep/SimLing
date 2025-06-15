package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.DataItemUser
import com.doni.simling.models.connections.responses.DeleteUserResponse
import com.doni.simling.models.connections.responses.EditUserResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: DataRepositories
) : ViewModel() {

    fun getUsers(): Flow<PagingData<DataItemUser>> = repository.getUsers().cachedIn(viewModelScope)

    private val _deleteUser = MutableLiveData<Resource<DeleteUserResponse>>()
    val deleteUser: MutableLiveData<Resource<DeleteUserResponse>> = _deleteUser


    private val _editUser = MutableLiveData<Resource<EditUserResponse>>()
    val editUser: LiveData<Resource<EditUserResponse>> get() = _editUser

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id).collect { resource ->
                _deleteUser.value = resource
            }
        }
    }

    fun editUser(
        id: Int,
        name: String,
        phoneNo: String,
        email: String,
        password: String,
        address: String,
        roleId: Int
    ) {
        viewModelScope.launch {
            repository.editUser(
                id = id,
                name = name,
                phoneNo = phoneNo,
                email = email,
                password = password,
                address = address,
                roleId = roleId
            ).collect { resource ->
                _editUser.postValue(resource)
            }
        }
    }
}
