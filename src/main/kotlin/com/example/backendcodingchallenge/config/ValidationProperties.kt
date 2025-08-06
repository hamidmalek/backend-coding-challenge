package com.example.backendcodingchallenge.config

data object ValidationProperties {
    val movie: Movie = Movie()
    val rating: Range = Range(1, 10)

    data class Movie(
        val title: Title = Title(200),
        val year: Range = Range(1888, 2100)
    )

    data class Title(
        val maxLength: Long
    )

    data class Range(
        val min: Long,
        val max: Long
    )
}
