package com.example.backendcodingchallenge.dto

import com.example.backendcodingchallenge.validation.rating.RatingWithinConfig
import jakarta.validation.constraints.NotBlank

data class RatingRequest(
    @field:NotBlank val userId: String,
    @field:NotBlank val movieId: String,
    @field:RatingWithinConfig
    val rating: Int
)
