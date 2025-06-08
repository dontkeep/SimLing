package com.doni.simling.models.connections.requests

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("phone_no") val phone_no: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("role_id") val role_id: Int,
    @SerializedName("family_members") val family_members: List<String> = emptyList()
)