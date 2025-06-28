package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class GetUserDetailResponse(

	@field:SerializedName("data")
	val data: DataUserDetail? = null
)

data class Role(

	@field:SerializedName("CreatedAt")
	val createdAt: String? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("DeletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("Role_Name")
	val roleName: String? = null,

	@field:SerializedName("UpdatedAt")
	val updatedAt: String? = null
)

data class DataUserDetail(

	@field:SerializedName("Role")
	val role: Role? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("Address")
	val address: String? = null,

	@field:SerializedName("CreatedAt")
	val createdAt: String? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("DeletedAt")
	val deletedAt: Any? = null,

	@field:SerializedName("UpdatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("Role_Id")
	val roleId: Int? = null,

	@field:SerializedName("Phone_No")
	val phoneNo: String? = null,

	@field:SerializedName("Password")
	val password: String? = null,

	@field:SerializedName("Name")
	val name: String? = null,

	@field:SerializedName("state")
	val status: String? = null
)
