package com.example.backendcodingchallenge.service

import com.example.backendcodingchallenge.dto.MovieRequest
import com.example.backendcodingchallenge.dto.MovieResponse
import com.example.backendcodingchallenge.model.Movie
import com.example.backendcodingchallenge.repo.MovieRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class MovieService(
    private val movieRepo: MovieRepository,
) {

    fun createMovie(req: MovieRequest): MovieResponse {
        if (movieRepo.existsByTitle(req.title)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Movie with title='${req.title}' already exists"
            )
        }
        val movie = movieRepo.save(Movie(title = req.title, releaseYear = req.releaseYear))
        return MovieResponse(movie.id, movie.title, movie.releaseYear)
    }

    fun getMovie(id: UUID): MovieResponse {
        val movie = movieRepo.findById(id)
            .orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Movie not found: $id"
                )
            }
        return MovieResponse(movie.id, movie.title, movie.releaseYear)
    }
}
