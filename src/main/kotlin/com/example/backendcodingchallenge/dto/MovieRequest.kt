package com.example.backendcodingchallenge.dto

import com.example.backendcodingchallenge.validation.movie.TitleMaxFromConfig
import com.example.backendcodingchallenge.validation.movie.YearWithinConfig
import jakarta.validation.constraints.NotBlank

data class MovieRequest(
    @field:NotBlank(message = "Title must not be blank")
    @field:TitleMaxFromConfig
    val title: String,

    @field:YearWithinConfig
    val releaseYear: Int? = null
)
