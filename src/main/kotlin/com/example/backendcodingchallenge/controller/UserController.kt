package com.example.backendcodingchallenge.controller

import com.example.backendcodingchallenge.dto.CreateUserRequest
import com.example.backendcodingchallenge.dto.UserProfileResponse
import com.example.backendcodingchallenge.dto.UserResponse
import com.example.backendcodingchallenge.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun registerUser(@Validated @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val response = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        val response = userService.getUser(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/profile")
    fun getUserProfile(@PathVariable id: UUID): ResponseEntity<UserProfileResponse> {
        val response = userService.getUserProfile(id)
        return ResponseEntity.ok(response)
    }
}
