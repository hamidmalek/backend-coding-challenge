package com.example.backendcodingchallenge.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.validation")
data class ValidationProperties(
    val movie: Movie = Movie(),
    val rating: Range = Range()
) {
    data class Movie(
        val title: Title = Title(),
        val year: Range = Range()
    )

    data class Title(
        val maxLength: Int = Int.MAX_VALUE
    )

    data class Range(
        val min: Int = 0,
        val max: Int = Int.MAX_VALUE
    )
}
