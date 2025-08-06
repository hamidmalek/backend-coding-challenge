package com.example.backendcodingchallenge.dto

import java.util.*

data class MovieResponse(
    val id: UUID,
    val title: String,
    val releaseYear: Int?
)
