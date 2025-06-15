package com.doni.simling.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doni.simling.models.connections.responses.DataItemUser
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: DataRepositories
) : ViewModel() {
    fun getUsers(): Flow<PagingData<DataItemUser>> = repository.getFunds().cachedIn(viewModelScope)
}
