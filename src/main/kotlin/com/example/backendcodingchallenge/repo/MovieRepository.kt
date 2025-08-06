package com.example.backendcodingchallenge.repo

import com.example.backendcodingchallenge.model.Movie
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MovieRepository : JpaRepository<Movie, UUID> {
    fun existsByTitle(title: String): Boolean
}
