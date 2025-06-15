package com.doni.simling.models.connections.requests

data class SecurityRecordRequest(
    val block: String,
    val longitude: Double,
    val latitude: Double
)
