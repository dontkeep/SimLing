package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class GetAllSecurityRecordsResponse(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItem3?>? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("totalPages")
	val totalPages: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class DataItem3(

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
