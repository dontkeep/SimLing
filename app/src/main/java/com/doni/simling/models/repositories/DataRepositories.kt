package com.doni.simling.models.repositories

import android.util.Log
import com.doni.simling.helper.Resource
import com.doni.simling.helper.manager.RoleManager
import com.doni.simling.helper.manager.TokenManager
import com.doni.simling.models.connections.configs.ApiServices
import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.responses.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
                if (response.message == "Logout successful") {
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
}