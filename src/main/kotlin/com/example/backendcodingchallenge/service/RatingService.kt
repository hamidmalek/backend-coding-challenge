package com.example.backendcodingchallenge.service

import com.example.backendcodingchallenge.dto.RatingRequest
import com.example.backendcodingchallenge.dto.RatingResponse
import com.example.backendcodingchallenge.dto.RatingsResponse
import com.example.backendcodingchallenge.model.Rating
import com.example.backendcodingchallenge.repo.MovieRepository
import com.example.backendcodingchallenge.repo.RatingRepository
import com.example.backendcodingchallenge.repo.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@Service
class RatingService(
    private val userRepo: UserRepository,
    private val movieRepo: MovieRepository,
    private val ratingRepo: RatingRepository
) {

    data class UpsertResult(val status: HttpStatus, val body: RatingResponse)

    fun upsertRating(req: RatingRequest): UpsertResult {
        val userId = parseUUID(req.userId, "userId")
        val movieId = parseUUID(req.movieId, "movieId")

        val user = userRepo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: $userId")
        }

        val movie = movieRepo.findById(movieId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: $movieId")
        }

        val existing = ratingRepo.findByUserIdAndMovieId(userId, movieId)

        val (saved, status) = if (existing != null) {
            val updated = existing.copy(
                rating = req.rating,
                createdAt = Instant.now()
            )
            ratingRepo.save(updated) to HttpStatus.OK
        } else {
            val newRating = Rating(
                user = user,
                movie = movie,
                rating = req.rating
            )
            ratingRepo.save(newRating) to HttpStatus.CREATED
        }

        return UpsertResult(status, RatingResponse.from(saved))
    }

    fun getRatingsForMovie(movieId: UUID): RatingsResponse {
        movieRepo.findById(movieId).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: $movieId")
        }

        val ratings = ratingRepo.findAllByMovieId(movieId)
        return RatingsResponse(
            ratings = ratings.map { RatingResponse.from(it) }
        )
    }

    private fun parseUUID(value: String, field: String): UUID {
        return try {
            UUID.fromString(value)
        } catch (_: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid $field format")
        }
    }
}
