package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class DeleteUserResponse(

	@field:SerializedName("message")
	val message: String? = null
)
