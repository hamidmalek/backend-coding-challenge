package com.example.backendcodingchallenge.dto

data class UserProfileResponse(
    val user: UserResponse,
    val ratings: List<RatingResponse>
)
