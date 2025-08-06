package com.example.backendcodingchallenge.model

import com.example.backendcodingchallenge.validation.movie.TitleMaxFromConfig
import com.example.backendcodingchallenge.validation.movie.YearWithinConfig
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import java.util.*

@Entity
@Table(name = "movies")
data class Movie(
    @Id
    val id: UUID = UUID.randomUUID(),

    @field:NotBlank(message = "Title cannot be blank")
    @field:TitleMaxFromConfig
    @Column(nullable = false, length = 200)
    val title: String = "",

    @field:YearWithinConfig
    @Column(name = "release_year")
    val releaseYear: Int? = 2000
)
