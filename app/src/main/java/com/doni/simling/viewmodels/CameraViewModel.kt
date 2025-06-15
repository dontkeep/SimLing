package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.AddSecurityResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val repositories: DataRepositories): ViewModel() {

    private val _addSecurityRecord = MutableLiveData<Resource<AddSecurityResponse>>()
    val addSecurityRecord: LiveData<Resource<AddSecurityResponse>>
        get() = _addSecurityRecord

    fun addSecurityRecord(
        block: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            repositories.addSecurityRecord(
                block = block,
                latitude = latitude,
                longitude = longitude
            ).collect { response ->
                _addSecurityRecord.value = response
            }
        }
    }
}