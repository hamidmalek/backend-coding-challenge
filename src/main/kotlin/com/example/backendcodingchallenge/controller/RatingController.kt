package com.example.backendcodingchallenge.controller

import com.example.backendcodingchallenge.dto.RatingRequest
import com.example.backendcodingchallenge.dto.RatingResponse
import com.example.backendcodingchallenge.dto.RatingsResponse
import com.example.backendcodingchallenge.service.RatingService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1")
class RatingController(
    private val ratingService: RatingService
) {

    @PostMapping("/ratings")
    fun rate(@Valid @RequestBody req: RatingRequest): ResponseEntity<RatingResponse> {
        val result = ratingService.upsertRating(req)
        return ResponseEntity.status(result.status).body(result.body)
    }

    @GetMapping("/movies/{id}/ratings")
    fun list(@PathVariable id: UUID): RatingsResponse =
        ratingService.getRatingsForMovie(id)
}
