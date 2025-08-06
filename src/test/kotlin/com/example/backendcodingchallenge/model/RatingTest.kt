package com.example.backendcodingchallenge.model

import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.util.*

@SpringBootTest
class RatingTest {
    @Autowired
    lateinit var validator: Validator
    private fun validUser(username: String = "alice") =
        User(username = username, password = "secret")

    private fun validMovie(title: String = "There Will Be Blood", year: Int = 2007) =
        Movie(title = title, releaseYear = year)

    @Test
    fun `valid rating passes validation`() {
        val rating = Rating(
            user = validUser(),
            movie = validMovie(),
            rating = 7
        )
        val violations = validator.validate(rating)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `id defaults to random uuid`() {
        val r1 = Rating(user = validUser("u1"), movie = validMovie("A", 2000), rating = 5)
        val r2 = Rating(user = validUser("u2"), movie = validMovie("B", 2001), rating = 6)
        assertThat(r1.id).isNotNull
        assertThat(r2.id).isNotNull
        assertThat(r1.id).isNotEqualTo(r2.id)
    }

    @Test
    fun `user must not be null`() {
        val violations = validator.validateValue(Rating::class.java, "user", null)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("user")
        }
    }

    @Test
    fun `movie must not be null`() {
        val violations = validator.validateValue(Rating::class.java, "movie", null)
        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("movie")
        }
    }

    @Test
    fun `rating must be between 1 and 10 inclusive`() {
        val tooLow = Rating(user = validUser(), movie = validMovie(), rating = 0)
        val tooHigh = Rating(user = validUser("b"), movie = validMovie("C", 2005), rating = 11)
        val lowOk = Rating(user = validUser("c"), movie = validMovie("D", 2010), rating = 1)
        val highOk = Rating(user = validUser("d"), movie = validMovie("E", 2011), rating = 10)

        assertThat(validator.validate(tooLow)).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("rating")
        }
        assertThat(validator.validate(tooHigh)).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("rating")
        }
        assertThat(validator.validate(lowOk)).isEmpty()
        assertThat(validator.validate(highOk)).isEmpty()
    }

    @Test
    fun `createdAt defaults to now and satisfies PastOrPresent`() {
        val r = Rating(user = validUser(), movie = validMovie(), rating = 8)
        val violations = validator.validate(r)
        assertThat(violations).isEmpty()

        assertThat(r.createdAt).isBeforeOrEqualTo(Instant.now())
    }

    @Test
    fun `createdAt cannot be in the future`() {
        val future = Instant.now().plusSeconds(60)
        val r = Rating(user = validUser(), movie = validMovie(), rating = 6, createdAt = future)
        val violations = validator.validate(r)

        assertThat(violations).anySatisfy {
            assertThat(it.propertyPath.toString()).isEqualTo("createdAt")
        }
    }

    @Test
    fun `data class equality is by value`() {
        val id = UUID.randomUUID()
        val u = validUser()
        val m = validMovie()
        val t = Instant.parse("2024-01-01T00:00:00Z")
        val r1 = Rating(id = id, user = u, movie = m, rating = 9, createdAt = t)
        val r2 = Rating(id = id, user = u, movie = m, rating = 9, createdAt = t)

        assertThat(r1).isEqualTo(r2)
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode())
        assertThat(r1.copy(rating = 7)).isNotEqualTo(r1)
    }
}
