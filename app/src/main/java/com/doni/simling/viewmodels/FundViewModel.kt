package com.doni.simling.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.AcceptIncomeResponse
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.models.connections.responses.DataItemFunds
import com.doni.simling.models.connections.responses.GetFundIncomeDetailResponse
import com.doni.simling.models.connections.responses.RejectIncomeResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FundViewModel @Inject constructor(
    private val repository: DataRepositories
) : ViewModel() {

    private val _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String> get() = _imageUri

    private val _fundDetail = MutableStateFlow<Resource<GetFundIncomeDetailResponse>>(Resource.Loading())
    val fundDetail: StateFlow<Resource<GetFundIncomeDetailResponse>> = _fundDetail

    private val _acceptState = MutableStateFlow<Resource<AcceptIncomeResponse>>(Resource.Loading())
    val acceptState: StateFlow<Resource<AcceptIncomeResponse>> = _acceptState

    private val _rejectState = MutableStateFlow<Resource<RejectIncomeResponse>>(Resource.Loading())
    val rejectState: StateFlow<Resource<RejectIncomeResponse>> = _rejectState

    fun addFund(
        amount: RequestBody,
        description: RequestBody,
        isIncome: RequestBody,
        status: RequestBody,
        image: MultipartBody.Part,
        block: RequestBody?,
        time: RequestBody
    ): Flow<Resource<CreateFundResponse>> = repository.addFund(
        amount = amount,
        description = description,
        isIncome = isIncome,
        status = status,
        image = image,
        block = block,
        time = time
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

    fun getAllFundsForExport(month: String, year: String): Flow<Resource<List<DataItemFunds>>> {
        return repository.getAllFundsForExport(month, year)
    }

    fun getAllIncomeForExport(month: String, year: String): Flow<Resource<List<DataItemFunds>>> {
        return repository.getAllIncomeForExport(month, year)
    }

    fun getFundIncomeDetail(id: Int) {
        viewModelScope.launch {
            repository.getFundIncomeDetail(id).collect { resource ->
                _fundDetail.value = resource
            }
        }
    }

    fun acceptIncome(id: Int) {
        viewModelScope.launch {
            repository.acceptIncome(id).collect { resource ->
                _acceptState.value = resource
            }
        }
    }

    fun rejectIncome(id: Int) {
        viewModelScope.launch {
            repository.rejectIncome(id).collect { resource ->
                _rejectState.value = resource
            }
        }
    }
}
