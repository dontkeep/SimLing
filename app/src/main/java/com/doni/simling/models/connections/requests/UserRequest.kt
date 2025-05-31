package com.doni.simling.models.connections.requests

data class UserRequest (
    val phone_no: String,
    val email: String,
    val password: String,
    val name: String,
    val address: String,
    val role_id: Int,
    val family_members: List<FamilyMembers>
)