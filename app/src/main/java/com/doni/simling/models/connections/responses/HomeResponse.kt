package com.doni.simling.models.connections.responses

import com.google.gson.annotations.SerializedName

data class HomeResponse(

	@field:SerializedName("total_expense")
	val totalExpense: Int? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("total_security")
	val totalSecurity: Int? = null,

	@field:SerializedName("month")
	val month: Int? = null,

	@field:SerializedName("year")
	val year: Int? = null,

	@field:SerializedName("current_credit")
	val currentCredit: Int? = null,

	@field:SerializedName("users_added_this_month")
	val usersAddedThisMonth: Int? = null,

	@field:SerializedName("total_users")
	val totalUsers: Int? = null
)
