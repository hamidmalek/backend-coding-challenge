package com.example.backendcodingchallenge.controller

import com.example.backendcodingchallenge.dto.MovieRequest
import com.example.backendcodingchallenge.dto.MovieResponse
import com.example.backendcodingchallenge.service.MovieService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/movies")
class MovieController(
    private val movieService: MovieService
) {

    @PostMapping
    fun create(
        @Valid @RequestBody req: MovieRequest
    ): ResponseEntity<MovieResponse> {
        val created = movieService.createMovie(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID
    ): MovieResponse =
        movieService.getMovie(id)

}
