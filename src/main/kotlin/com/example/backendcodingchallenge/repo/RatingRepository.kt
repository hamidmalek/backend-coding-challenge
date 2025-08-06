package com.example.backendcodingchallenge.repo

import com.example.backendcodingchallenge.model.Rating
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RatingRepository : JpaRepository<Rating, UUID> {
    fun findByUserIdAndMovieId(userId: UUID, movieId: UUID): Rating?
    fun findAllByMovieId(movieId: UUID): List<Rating>
    fun findAllByUserId(userId: UUID): List<Rating>
}
