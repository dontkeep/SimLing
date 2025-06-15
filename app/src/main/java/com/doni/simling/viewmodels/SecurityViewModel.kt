package com.doni.simling.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doni.simling.helper.DateHelper
import com.doni.simling.helper.Resource
import com.doni.simling.models.connections.responses.DataItem3
import com.doni.simling.models.connections.responses.GetAllSecurityRecordsResponse
import com.doni.simling.models.connections.responses.GetSecurityByUserResponse
import com.doni.simling.models.repositories.DataRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.Month
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(private val repositories: DataRepositories): ViewModel() {
    val securityRecordsByUser = repositories.getUserSecurityRecordByUser()
    fun securityRecordsByUserByDay(date: String): Flow<Resource<GetSecurityByUserResponse>> {
        return repositories.getUserSecurityRecordByUserByDay(date)
    }

    fun securityRecordsByDay(date: String): Flow<Resource<GetSecurityByUserResponse>> {
        return repositories.getSecurityRecordByDay(date)
    }

    fun getAllSecurityRecords(
        month: String = DateHelper.getCurrentMonth(),
        year: String = DateHelper.getCurrentYear()
    ): Flow<PagingData<DataItem3>> {
        return repositories.getAllSecurityRecords(month, year).cachedIn(viewModelScope)
    }
}