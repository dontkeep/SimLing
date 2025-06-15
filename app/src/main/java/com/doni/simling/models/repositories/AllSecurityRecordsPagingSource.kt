package com.doni.simling.models.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.doni.simling.models.connections.configs.ApiServices
import com.doni.simling.models.connections.responses.DataItem3

class AllSecurityRecordsPagingSource(
    private val apiService: ApiServices,
    private val token: String?,
    private val month: String,
    private val year: String,
    private val pageSize: Int = 10
): PagingSource<Int, DataItem3>() {
    override fun getRefreshKey(state: PagingState<Int, DataItem3>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem3> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getAllSecurityRecords(
                token = "Bearer $token",
                month = month,
                year = year,
                page = page,
                limit = pageSize
            )
            val data = response.data?.filterNotNull() ?: emptyList()
            val nextKey = if(data.size < pageSize) null else page + 1
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}