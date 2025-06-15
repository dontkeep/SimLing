package com.doni.simling.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.DataItemUser
import com.doni.simling.models.connections.responses.DeleteUserResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: DataRepositories
) : ViewModel() {

    fun getUsers(): Flow<PagingData<DataItemUser>> = repository.getUsers().cachedIn(viewModelScope)

    private val _deleteUser = MutableLiveData<Resource<DeleteUserResponse>>()
    val deleteUser: MutableLiveData<Resource<DeleteUserResponse>> = _deleteUser

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id).collect { resource ->
                _deleteUser.value = resource
            }
        }
    }
}
