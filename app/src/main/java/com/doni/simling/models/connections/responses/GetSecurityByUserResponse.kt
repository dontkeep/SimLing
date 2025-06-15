package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class GetSecurityByUserResponse(

	@field:SerializedName("data")
	val data: List<DataItemSecurityByUser?>? = null
)

data class DataItemSecurityByUser(

	@field:SerializedName("phone_no")
	val phoneNo: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("block")
	val block: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("security_id")
	val securityId: Int? = null,

	@field:SerializedName("security_name")
	val securityName: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null
)
