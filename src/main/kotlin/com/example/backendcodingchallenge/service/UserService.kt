package com.example.backendcodingchallenge.service

import com.example.backendcodingchallenge.dto.CreateUserRequest
import com.example.backendcodingchallenge.dto.RatingResponse
import com.example.backendcodingchallenge.dto.UserProfileResponse
import com.example.backendcodingchallenge.dto.UserResponse
import com.example.backendcodingchallenge.model.User
import com.example.backendcodingchallenge.repo.RatingRepository
import com.example.backendcodingchallenge.repo.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val ratingRepository: RatingRepository
) {
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.findByUsername(request.username) != null) throw ResponseStatusException(
            HttpStatus.CONFLICT,
            "User already exists"
        )
        val user = userRepository.save(User(username = request.username, password = request.password))
        return UserResponse(id = user.id, username = request.username)
    }

    fun getUser(id: UUID): UserResponse {
        val user = userRepository.findById(id).orElseThrow {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User already exists"
            )
        }
        return UserResponse(id = user.id, username = user.username)
    }

    fun getUserProfile(id: UUID): UserProfileResponse {
        val user = userRepository.findById(id).orElseThrow {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
            )
        }
        val ratings = ratingRepository.findAllByUserId(id).map {
            RatingResponse(
                id = it.id,
                movieId = it.movie.id,
                rating = it.rating,
                createdAt = it.createdAt
            )
        }
        return UserProfileResponse(
            user = UserResponse(id = user.id, username = user.username),
            ratings = ratings
        )
    }
}
