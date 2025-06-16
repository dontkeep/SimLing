package com.doni.simling.models.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingData
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.TokenManager
import com.doni.simling.models.connections.configs.ApiServices
import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.requests.SecurityRecordRequest
import com.doni.simling.models.connections.requests.UserRequest
import com.doni.simling.models.connections.responses.AcceptIncomeResponse
import com.doni.simling.models.connections.responses.AddSecurityResponse
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.models.connections.responses.CreateUserResponse
import com.doni.simling.models.connections.responses.DataItem3
import com.doni.simling.models.connections.responses.DataItemFunds
import com.doni.simling.models.connections.responses.DataItemSecurityByUser
import com.doni.simling.models.connections.responses.DataItemUser
import com.doni.simling.models.connections.responses.GetAllSecurityRecordsResponse
import com.doni.simling.models.connections.responses.DeleteUserResponse
import com.doni.simling.models.connections.responses.EditUserResponse
import com.doni.simling.models.connections.responses.GetFundIncomeDetailResponse
import com.doni.simling.models.connections.responses.GetSecurityByUserResponse
import com.doni.simling.models.connections.responses.HomeResponse
import com.doni.simling.models.connections.responses.RejectIncomeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepositories @Inject constructor(
    private val apiServices: ApiServices,
    private val tokenManager: TokenManager,
    private val roleManager: RoleManager
) {
    fun login(
        phone_no: String,
        password: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiServices.login(
                LoginRequest(
                    Phone_No = phone_no,
                    Password = password
                )
            )
            if (response.token.isNotEmpty()) {
                Log.d("LoginResponse", "Token: ${response.token}")
                Log.d("LoginResponse", "Role: ${response.role}")
                tokenManager.saveToken(response.token)
                roleManager.saveRole(response.role)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Login failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token?.isNotEmpty() == true) {
                val response = apiServices.logout("Bearer $token")
                if (response.message.lowercase().contains("logout success")) {
                    tokenManager.clearToken()
                    roleManager.clearRole()
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Logout failed"))
                }
            } else {
                emit(Resource.Error("No token found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun addFamily(
        name: String,
        phoneNo: String,
        email: String,
        password: String,
        address: String,
        roleId: Int
    ): Flow<Resource<CreateUserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.createUser(
                token = "Bearer $token",
                userRequest = UserRequest(
                    phone_no = phoneNo,
                    email = email,
                    password = password,
                    name = name,
                    address = address,
                    role_id = roleId,
                    family_members = emptyList()
                )
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun addFund(
        amount: RequestBody,
        description: RequestBody,
        isIncome: RequestBody,
        status: RequestBody,
        image: MultipartBody.Part,
        block: RequestBody?,
    ): Flow<Resource<CreateFundResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.createFund(
                token = "Bearer $token",
                amount = amount,
                description = description,
                isIncome = isIncome,
                status = status,
                image = image,
                block = block
            )

            if (response.isSuccessful) {
                response.body()?.let { fundResponse ->
                    emit(Resource.Success(fundResponse))
                } ?: emit(Resource.Error("Response body is null"))
            } else {
                emit(Resource.Error(response.message() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun getIncome(
        month: String,
        year: String
    ): Flow<PagingData<DataItemFunds>> {
        val token = tokenManager.getToken()
        return Pager(
            config = androidx.paging.PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                IncomePagingSource(apiServices, token, month, year, 10)
            }
        ).flow
    }

    fun getFunds(
        month: String,
        year: String
    ): Flow<PagingData<DataItemFunds>> {
        val token = tokenManager.getToken()
        return Pager(
            config = androidx.paging.PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                FundPagingSource(apiServices, token, month, year, 10)
            }
        ).flow
    }

    fun getUsers(): Flow<PagingData<DataItemUser>> {
        val token = tokenManager.getToken()
        return Pager(
            config = androidx.paging.PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                UserPagingSource(apiServices, token, 10)
            }
        ).flow
    }

    fun getHome(): Flow<Resource<HomeResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.getHomeData(
                token = "Bearer $token"
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun getFundIncomeDetail(id: Int): Flow<Resource<GetFundIncomeDetailResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.getFundById(
                token = "Bearer $token",
                id = id
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun acceptIncome(id: Int): Flow<Resource<AcceptIncomeResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.acceptIncome(
                token = "Bearer $token",
                id = id
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun rejectIncome(id: Int): Flow<Resource<RejectIncomeResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.rejectIncome(
                token = "Bearer $token",
                id = id
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun addSecurityRecord(block: String, longitude: Double, latitude: Double): Flow<Resource<AddSecurityResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val request = SecurityRecordRequest(
                block = block,
                longitude = longitude,
                latitude = latitude
            )

            val response = apiServices.addSecurityRecord(
                token = "Bearer $token",
                securityRecord = request
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun getUserSecurityRecordByUser(): Flow<Resource<GetSecurityByUserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.getSecurityRecordsByUser(
                token = "Bearer $token"
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun getUserSecurityRecordByUserByDay(date: String): Flow<Resource<GetSecurityByUserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.getSecurityRecordsByUserByDay(
                token = "Bearer $token",
                date = date
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun getSecurityRecordByDay(date: String): Flow<Resource<GetSecurityByUserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.getSecurityRecordsByDay(
                token = "Bearer $token",
                date = date
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun getAllSecurityRecords(
        month: String,
        year: String
    ): Flow<PagingData<DataItem3>> {
        val token = tokenManager.getToken()
        return Pager(
            config = androidx.paging.PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                AllSecurityRecordsPagingSource(apiServices, token, month = month, year = year)
            }
        ).flow
    }

    fun deleteUser(id: Int): Flow<Resource<DeleteUserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.deleteUser(
                token = "Bearer $token",
                id = id
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
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
    ): Flow<Resource<EditUserResponse>> = flow {
        emit(Resource.Loading())
        try {
            val token = tokenManager.getToken()
            if (token.isNullOrEmpty()) {
                emit(Resource.Error("No token found"))
                return@flow
            }

            val response = apiServices.updateUser(
                token = "Bearer $token",
                id = id,
                userRequest = UserRequest(
                    phone_no = phoneNo,
                    email = email,
                    password = password,
                    name = name,
                    address = address,
                    role_id = roleId,
                    family_members = emptyList()
                )
            )
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}