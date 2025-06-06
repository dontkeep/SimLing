package com.doni.simling.models.repositories

import android.util.Log
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.TokenManager
import com.doni.simling.models.connections.configs.ApiServices
import com.doni.simling.models.connections.requests.FamilyMembers
import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.requests.UserRequest
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.models.connections.responses.CreateUserResponse
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

}