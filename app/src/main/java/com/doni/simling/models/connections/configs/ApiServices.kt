package com.doni.simling.models.connections.configs

import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.requests.UserRequest
import com.doni.simling.models.connections.responses.CreateUserResponse
import com.doni.simling.models.connections.responses.LoginResponse
import com.doni.simling.models.connections.responses.LogoutResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiServices {
    @POST("/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): LogoutResponse

    @POST("/api/users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body userRequest: UserRequest
    ): CreateUserResponse

}