package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class GetAllUserResponse(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItemUser?>? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class DataItemUser(

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
