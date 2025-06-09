package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.HomeResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DataRepositories
): ViewModel() {

    private val _homeState = MutableLiveData<Resource<HomeResponse>>(Resource.Loading())
    val homeState: LiveData<Resource<HomeResponse>> = _homeState

    fun getHomeData() {
        viewModelScope.launch {
            repository.getHome().collect { resource ->
                _homeState.value = resource
            }
        }
    }
}