package com.example.backendcodingchallenge.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RatingControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var userId: String
    private lateinit var movieId: String

    private val username = "rater"
    private val password = "rat3rPass!"
    private val movieTitle = "Test Movie"
    private val releaseYear = 2020

    @BeforeEach
    fun setup() {
        val userJson = """{"username":"$username","password":"$password"}"""
        val userResult = mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andReturn()
        val userNode: JsonNode = objectMapper.readTree(userResult.response.contentAsString)
        userId = userNode.get("id").asText()

        val movieJson = """{"title":"$movieTitle","releaseYear":$releaseYear}"""
        val movieResult = mockMvc.perform(
            post("/api/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(movieJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andReturn()
        val movieNode: JsonNode = objectMapper.readTree(movieResult.response.contentAsString)
        movieId = movieNode.get("id").asText()
    }

    @Test
    fun `should create a rating for a movie`() {
        val ratingJson = """{"userId":"$userId","movieId":"$movieId","rating":8}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.movieId").value(movieId))
            .andExpect(jsonPath("$.rating").value(8))
            .andExpect(jsonPath("$.createdAt").exists())
    }

    @Test
    fun `should update existing rating`() {
        val ratingJson = """{"userId":"$userId","movieId":"$movieId","rating":7}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingJson)
        )
            .andExpect(status().isCreated)

        val updatedJson = """{"userId":"$userId","movieId":"$movieId","rating":5}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.rating").value(5))
    }

    @Test
    fun `should not create rating with invalid value`() {
        val tooLow = """{"userId":"$userId","movieId":"$movieId","rating":0}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tooLow)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.error.details.rating[0]", containsString("between")))

        val tooHigh = """{"userId":"$userId","movieId":"$movieId","rating":11}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tooHigh)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.details.rating[0]", containsString("between")))
    }

    @Test
    fun `should not create rating for non-existent user or movie`() {
        val fakeUser = """{"userId":"00000000-0000-0000-0000-000000000000","movieId":"$movieId","rating":5}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakeUser)
        )
            .andExpect(status().isNotFound)

        val fakeMovie = """{"userId":"$userId","movieId":"00000000-0000-0000-0000-000000000000","rating":5}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(fakeMovie)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should list ratings for a movie`() {
        val secondUserJson = """{"username":"other","password":"oth3rPass"}"""
        val secondUserResult = mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondUserJson)
        )
            .andExpect(status().isCreated)
            .andReturn()
        val secondUserId = objectMapper.readTree(secondUserResult.response.contentAsString).get("id").asText()

        listOf(userId, secondUserId).forEach { uid ->
            val rj = """{"userId":"$uid","movieId":"$movieId","rating":6}"""
            mockMvc.perform(
                post("/api/v1/ratings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(rj)
            ).andExpect(status().isCreated)
        }

        mockMvc.perform(
            get("/api/v1/movies/$movieId/ratings")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.movie.id").value(movieId))
            .andExpect(jsonPath("$.data", hasSize<Any>(2)))
            .andExpect(jsonPath("$.data[0].rating").value(6))
    }

    @Test
    fun `should return 404 when listing ratings for non-existent movie`() {
        val fakeId = "22222222-3333-4444-5555-666666666666"
        mockMvc.perform(
            get("/api/v1/movies/$fakeId/ratings")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should fail to create rating when userId or movieId missing`() {
        val noIds = """{"rating":5}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noIds)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail to create rating when userId or movieId malformed`() {
        val badUser = """{"userId":"not-uuid","movieId":"$movieId","rating":5}"""
        val badMovie = """{"userId":"$userId","movieId":"nope","rating":5}"""

        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badUser)
        )
            .andExpect(status().isBadRequest)

        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badMovie)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `duplicate rating upsert returns 200 on second call`() {
        val rj = """{"userId":"$userId","movieId":"$movieId","rating":6}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rj)
        )
            .andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rj)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should reject out-of-range rating on update`() {
        val valid = """{"userId":"$userId","movieId":"$movieId","rating":5}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valid)
        )
            .andExpect(status().isCreated)

        val invalid = """{"userId":"$userId","movieId":"$movieId","rating":100}"""
        mockMvc.perform(
            post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.details.rating[0]", containsString("between")))
    }
}
