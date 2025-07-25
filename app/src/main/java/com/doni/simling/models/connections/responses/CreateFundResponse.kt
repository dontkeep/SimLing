package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class CreateFundResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("user_name")
	val userName: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("block")
	val block: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("is_income")
	val isIncome: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("time_transferred")
	val timeTransferred: String? = null
)
