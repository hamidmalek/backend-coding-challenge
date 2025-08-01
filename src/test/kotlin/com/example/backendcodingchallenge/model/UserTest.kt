package com.example.backendcodingchallenge.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

class UserTest {

    companion object {
        private lateinit var validator: Validator

        @BeforeAll
        @JvmStatic
        fun setUpValidator() {
            validator = Validation.buildDefaultValidatorFactory().validator
        }
    }

    @Test
    fun `valid user passes validation`() {
        val user = User(
            id = UUID.randomUUID(),
            username = "alice",
            password = "secret"
        )

        val violations = validator.validate(user)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `id defaults to random uuid`() {
        val u1 = User(username = "u1", password = "p1")
        val u2 = User(username = "u2", password = "p2")

        assertThat(u1.id).isNotNull
        assertThat(u2.id).isNotNull
        assertThat(u1.id).isNotEqualTo(u2.id)
    }

    @Test
    fun `blank username fails with message`() {
        val user = User(username = "   ", password = "secret")
        val violations = validator.validate(user)

        assertThat(violations)
            .anySatisfy { v ->
                assertThat(v.propertyPath.toString()).isEqualTo("username")
                assertThat(v.message).isEqualTo("Username is required")
            }
    }

    @Test
    fun `blank password fails with message`() {
        val user = User(username = "alice", password = "  ")
        val violations = validator.validate(user)

        assertThat(violations)
            .anySatisfy { v ->
                assertThat(v.propertyPath.toString()).isEqualTo("password")
                assertThat(v.message).isEqualTo("Password is required")
            }
    }

    @Test
    fun `empty strings also fail NotBlank`() {
        val user = User(username = "", password = "")
        val violations = validator.validate(user)

        assertThat(violations.map { it.propertyPath.toString() })
            .containsExactlyInAnyOrder("username", "password")
    }

    @Test
    fun `data class equality is by value`() {
        val id = UUID.randomUUID()
        val u1 = User(id = id, username = "a", password = "b")
        val u2 = User(id = id, username = "a", password = "b")

        assertThat(u1).isEqualTo(u2)
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode())
        assertThat(u1.copy(username = "x")).isNotEqualTo(u1)
    }
}
