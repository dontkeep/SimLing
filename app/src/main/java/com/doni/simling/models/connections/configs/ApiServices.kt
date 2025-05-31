package com.doni.simling.models.connections.configs

import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.requests.UserRequest
import com.doni.simling.models.connections.responses.CreateUserResponse
import com.doni.simling.models.connections.responses.LoginResponse
import com.doni.simling.models.connections.responses.LogoutResponse
import retrofit2.http.Body
import retrofit2.http.Header


interface ApiServices {
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    suspend fun logout(
        @Header("Authorization") token: String
    ): LogoutResponse

    suspend fun createUser(
        @Body userRequest: UserRequest
    ): CreateUserResponse

}