package com.doni.simling.models.connections.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class CreateUserResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null
) : Parcelable

@Parcelize
data class User(

	@field:SerializedName("phone_no")
	val phoneNo: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
) : Parcelable
