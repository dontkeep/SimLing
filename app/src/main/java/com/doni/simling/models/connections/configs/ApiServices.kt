package com.doni.simling.models.connections.configs

import com.doni.simling.models.connections.responses.LoginResponse
import retrofit2.http.Body


interface ApiServices {
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse
}