package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class EditUserResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: UserDataEdit? = null
)

data class UserDataEdit(

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
)
