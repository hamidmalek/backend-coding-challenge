package com.example.backendcodingchallenge.dto

import com.example.backendcodingchallenge.model.Rating
import java.time.Instant
import java.util.*

data class RatingResponse(
    val id: UUID,
    val movieId: UUID,
    val rating: Int,
    val createdAt: Instant
){
    companion object {
        fun from(rating: Rating): RatingResponse = RatingResponse(
            id = rating.id,
            movieId = rating.movie.id,
            rating = rating.rating,
            createdAt = rating.createdAt
        )
    }
}
