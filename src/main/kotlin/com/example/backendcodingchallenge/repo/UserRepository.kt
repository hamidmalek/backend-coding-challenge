package com.example.backendcodingchallenge.repo

import com.example.backendcodingchallenge.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?
}
