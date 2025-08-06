package com.example.backendcodingchallenge.dto

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val username: String
)
