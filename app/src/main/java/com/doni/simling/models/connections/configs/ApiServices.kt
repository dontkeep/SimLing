package com.doni.simling.models.connections.configs

import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.requests.UserRequest
import com.doni.simling.models.connections.responses.CreateUserResponse
import com.doni.simling.models.connections.responses.LoginResponse
import com.doni.simling.models.connections.responses.LogoutResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


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

    @GET("/api/users")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): List<CreateUserResponse> //update this to the correct response type

    @PUT("/api/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body userRequest: UserRequest
    ): CreateUserResponse //update this to the correct response type

    @DELETE("/api/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CreateUserResponse //update this to the correct response type

    @GET("/api/funds-income")
    suspend fun getFundsIncome(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): List<CreateUserResponse> //update this to the correct response type

    @GET("/api/funds-expense")
    suspend fun getFundsExpense(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): List<CreateUserResponse> //update this to the correct response type

    @GET("/api/home")
    suspend fun getHomeData(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): CreateUserResponse //update this to the correct response type

    @GET("/api/funds-by-user")
    suspend fun getFundsByUser(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): List<CreateUserResponse> //update this to the correct response type

    @Multipart
    @POST("/api/funds")
    suspend fun createFund(
        @Header("Authorization") token: String,
        @Part("amount") amount: RequestBody,
        @Part("description") description: RequestBody,
        @Part("is_income") isIncome: RequestBody,
        @Part("status") status: RequestBody,
        @Part("block") block: RequestBody,
        @Part images: MultipartBody.Part // for 1 files, wrap with list if you want to upload multiple files
    ): Response<CreateUserResponse>

    @PUT("/api/funds/{id}/reject")
    suspend fun rejectFund(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CreateUserResponse //update this to the correct response type

    @PUT("/api/funds/{id}/accept")
    suspend fun acceptFund(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CreateUserResponse //update this to the correct response type

    @DELETE("/api/funds/{id}")
    suspend fun deleteFund(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CreateUserResponse //update this to the correct response type

    @GET("/api/security-records/by-user")
    suspend fun getSecurityRecordsByUser(
        @Header("Authorization") token: String
    ): List<CreateUserResponse> //update this to the correct response type

    @GET("/api/security-records/by-day")
    suspend fun getSecurityRecordsByDay(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): List<CreateUserResponse> //update this to the correct response type

    @GET("/api/security-records")
    suspend fun getAllSecurityRecords(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): List<CreateUserResponse> //update this to the correct response type

    @POST("/api/security-records/add")
    suspend fun addSecurityRecord(
        @Header("Authorization") token: String,
        @Body securityRecord: CreateUserResponse //update this to the correct request type
    ): CreateUserResponse //update this to the correct response type

    @GET("/api/funds/{id}")
    suspend fun getFundById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): CreateUserResponse //update this to the correct response type

    @GET("/api/funds")
    suspend fun getAllFunds(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String
    ): List<CreateUserResponse> //update this to the correct response type


}