package com.example.backendcodingchallenge.model

import com.example.backendcodingchallenge.validation.rating.RatingWithinConfig
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "ratings")
data class Rating(

    @Id
    val id: UUID = UUID.randomUUID(),

    @field:NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User = User(),

    @field:NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    val movie: Movie = Movie(),

    @field:RatingWithinConfig
    @Column(nullable = false)
    val rating: Int = 1,

    @field:PastOrPresent
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
