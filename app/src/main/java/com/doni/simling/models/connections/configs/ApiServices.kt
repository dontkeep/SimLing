package com.doni.simling.models.connections.configs

import com.doni.simling.models.connections.requests.LoginRequest
import com.doni.simling.models.connections.requests.SecurityRecordRequest
import com.doni.simling.models.connections.requests.UserRequest
import com.doni.simling.models.connections.responses.AcceptIncomeResponse
import com.doni.simling.models.connections.responses.AddSecurityResponse
import com.doni.simling.models.connections.responses.CreateFundResponse
import com.doni.simling.models.connections.responses.CreateUserResponse
import com.doni.simling.models.connections.responses.DataItemFunds
import com.doni.simling.models.connections.responses.DeleteUserResponse
import com.doni.simling.models.connections.responses.EditUserResponse
import com.doni.simling.models.connections.responses.DataItemSecurityByUser
import com.doni.simling.models.connections.responses.GetAllFundsResponse
import com.doni.simling.models.connections.responses.GetAllSecurityRecordsResponse
import com.doni.simling.models.connections.responses.GetAllUserResponse
import com.doni.simling.models.connections.responses.GetFundIncomeDetailResponse
import com.doni.simling.models.connections.responses.GetUserDetailResponse
import com.doni.simling.models.connections.responses.GetSecurityByUserResponse
import com.doni.simling.models.connections.responses.HomeResponse
import com.doni.simling.models.connections.responses.LoginResponse
import com.doni.simling.models.connections.responses.LogoutResponse
import com.doni.simling.models.connections.responses.RejectIncomeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @POST("/api/logout")
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
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): GetAllUserResponse

    @GET("/api/users/{id}")
    suspend fun detailUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): GetUserDetailResponse //update this to the correct response type

    @PUT("/api/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body userRequest: UserRequest
    ): EditUserResponse

    @DELETE("/api/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DeleteUserResponse

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
        @Query("year") year: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): List<CreateUserResponse> //update this to the correct response type

    @GET("/api/home")
    suspend fun getHomeData(
        @Header("Authorization") token: String
    ): HomeResponse

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
        @Part image: MultipartBody.Part,
        @Part("block") block: RequestBody?
    ): Response<CreateFundResponse>

    @PUT("/api/funds/{id}/reject")
    suspend fun rejectIncome(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): RejectIncomeResponse

    @PUT("/api/funds/{id}/accept")
    suspend fun acceptIncome(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): AcceptIncomeResponse

    @DELETE("/api/funds/{id}")
    suspend fun deleteFund(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): CreateUserResponse //update this to the correct response type

    @GET("/api/security-records/by-user")
    suspend fun getSecurityRecordsByUser(
        @Header("Authorization") token: String
    ): GetSecurityByUserResponse

    @GET("/api/security-records/by-user-by-day")
    suspend fun getSecurityRecordsByUserByDay(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): GetSecurityByUserResponse

    @GET("/api/security-records/by-day")
    suspend fun getSecurityRecordsByDay(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): GetSecurityByUserResponse

    @GET("/api/security-records")
    suspend fun getAllSecurityRecords(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): GetAllSecurityRecordsResponse

    @POST("/api/security-records/add")
    suspend fun addSecurityRecord(
        @Header("Authorization") token: String,
        @Body securityRecord: SecurityRecordRequest //update this to the correct request type
    ): AddSecurityResponse //update this to the correct response type

    @GET("/api/funds/{id}")
    suspend fun getFundById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): GetFundIncomeDetailResponse

    @GET("/api/funds-expense")
    suspend fun getAllFunds(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): GetAllFundsResponse //update this to the correct response type

    @GET("/api/funds-income")
    suspend fun getAllIncome(
        @Header("Authorization") token: String,
        @Query("month") month: String,
        @Query("year") year: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): GetAllFundsResponse
}