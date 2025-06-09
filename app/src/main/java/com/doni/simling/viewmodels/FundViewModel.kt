package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.models.connections.responses.DataItemFunds
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FundViewModel @Inject constructor(
    private val repository: DataRepositories
) : ViewModel() {

    private val _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String> get() = _imageUri

    fun addFund(
        amount: RequestBody,
        description: RequestBody,
        isIncome: RequestBody,
        status: RequestBody,
        image: MultipartBody.Part,
        block: RequestBody?,
    ): Flow<Resource<CreateFundResponse>> = repository.addFund(
        amount = amount,
        description = description,
        isIncome = isIncome,
        status = status,
        image = image,
        block = block,
    )

    fun setImageUri(path: String) {
        _imageUri.value = path
    }

    fun getAllIncomePaging(month: String, year: String): Flow<PagingData<DataItemFunds>> {
        return repository.getIncome(month, year).cachedIn(viewModelScope)
    }

    fun getAllFundPaging(month: String, year: String): Flow<PagingData<DataItemFunds>> {
        return repository.getFunds(month, year).cachedIn(viewModelScope)
    }
}
