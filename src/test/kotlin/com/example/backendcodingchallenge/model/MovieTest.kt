package com.example.backendcodingchallenge.model

import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class MovieTest {

    @Autowired
    lateinit var validator: Validator

    @Test
    fun `valid movie passes validation`() {
        val movie = Movie(
            title = "Metropolis",
            releaseYear = 1927
        )
        val violations = validator.validate(movie)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `id defaults to random uuid`() {
        val m1 = Movie(title = "Film A", releaseYear = 2001)
        val m2 = Movie(title = "Film B", releaseYear = 2002)

        assertThat(m1.id).isNotNull
        assertThat(m2.id).isNotNull
        assertThat(m1.id).isNotEqualTo(m2.id)
    }

    @Test
    fun `title must not be blank`() {
        val movie = Movie(title = "   ", releaseYear = 1999)
        val violations = validator.validate(movie)

        assertThat(violations)
            .anySatisfy { v ->
                assertThat(v.propertyPath.toString()).isEqualTo("title")
                assertThat(v.message).isNotBlank
            }
    }

    @Test
    fun `title length must be between 1 and 200`() {
        val tooShort = Movie(title = "", releaseYear = 2010)
        val tooLong = Movie(title = "x".repeat(201), releaseYear = 2010)
        val okEdge = Movie(title = "x".repeat(200), releaseYear = 2010)

        val vShort = validator.validate(tooShort)
        val vLong = validator.validate(tooLong)
        val vOk = validator.validate(okEdge)

        assertThat(vShort).isNotEmpty
        assertThat(vLong).isNotEmpty
        assertThat(vOk).isEmpty()
    }

    @Test
    fun `releaseYear can be null (bounds ignored when null)`() {
        val movie = Movie(title = "Untitled", releaseYear = null)
        val violations = validator.validate(movie)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `releaseYear must be within 1888 and 2100 inclusive when present`() {
        val tooEarly = Movie(title = "Prehistory", releaseYear = 1887)
        val tooLate = Movie(title = "Far Future", releaseYear = 2101)
        val lowerOk = Movie(title = "First Film", releaseYear = 1888)
        val upperOk = Movie(title = "Sensible Upper", releaseYear = 2100)

        assertThat(validator.validate(tooEarly))
            .anySatisfy { v ->
                assertThat(v.propertyPath.toString()).isEqualTo("releaseYear")
            }
        assertThat(validator.validate(tooLate))
            .anySatisfy { v ->
                assertThat(v.propertyPath.toString()).isEqualTo("releaseYear")
            }
        assertThat(validator.validate(lowerOk)).isEmpty()
        assertThat(validator.validate(upperOk)).isEmpty()
    }

    @Test
    fun `data class equality is by value`() {
        val id = UUID.randomUUID()
        val m1 = Movie(id = id, title = "Same", releaseYear = 2000)
        val m2 = Movie(id = id, title = "Same", releaseYear = 2000)

        assertThat(m1).isEqualTo(m2)
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode())
        assertThat(m1.copy(title = "Different")).isNotEqualTo(m1)
    }
}
