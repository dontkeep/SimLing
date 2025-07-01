package com.doni.simling.models.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.doni.simling.models.connections.configs.ApiServices
import com.doni.simling.models.connections.responses.DataItemUser

class UserPagingSource(
    private val apiService: ApiServices,
    private val token: String?,
    private val pageSize: Int = 10,
    private val query: String? = null
) : PagingSource<Int, DataItemUser>() {
    var onDataLoaded: ((List<DataItemUser>) -> Unit)? = null

    override fun getRefreshKey(state: PagingState<Int, DataItemUser>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItemUser> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getUsers(
                token = "Bearer $token",
                page = page,
                limit = pageSize,
                name = query
            )
            val data = response.data?.filterNotNull() ?: emptyList()
            onDataLoaded?.invoke(data)
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