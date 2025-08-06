package com.example.backendcodingchallenge.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.containsString
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
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MovieControllerTest {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    private val title = "Arrival"
    private val releaseYear = 2016
    private val createBody = """{"title":"$title","releaseYear":$releaseYear}"""

    private val createUrl = "/api/v1/movies"
    private val getUrlTemplate = "/api/v1/movies/%s"
    private val ratingsUrlTemplate = "/api/v1/movies/%s/ratings"

    private lateinit var movieId: String

    @BeforeEach
    fun setup() {

        val mvcResult = mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.releaseYear").value(releaseYear))
            .andReturn()

        val json: String = mvcResult.response.contentAsString
        val node: JsonNode = objectMapper.readTree(json)
        movieId = node.get("id").asText()
    }

    @Test
    fun `should create a new movie`() {
        val body = """{"title":"Inception","releaseYear":2010}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Inception"))
            .andExpect(jsonPath("$.releaseYear").value(2010))
    }

    @Test
    fun `should fail creation with blank title`() {
        val invalid = """{"title":"","releaseYear":$releaseYear}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.error.details.title[0]", containsString("must not be blank")))
    }

    @Test
    fun `should fail creation with title exceeding max length`() {
        val longTitle = "x".repeat(201)
        val invalid = """{"title":"$longTitle","releaseYear":$releaseYear}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.error.details.title[0]", containsString("title is too long")))
    }

    @Test
    fun `should fail creation with year out of range`() {
        val invalidPast = """{"title":"Classic","releaseYear":1800}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPast)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.details.releaseYear[0]", containsString("release year is out of range")))

        val invalidFuture = """{"title":"FutureFilm","releaseYear":3000}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidFuture)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.details.releaseYear[0]", containsString("release year is out of range")))
    }

    @Test
    fun `should not allow duplicate movie`() {
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody)
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `should retrieve existing movie`() {
        mockMvc.perform(
            get(getUrlTemplate.format(movieId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(movieId))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.releaseYear").value(releaseYear))
    }

    @Test
    fun `should return 404 for non-existent movie`() {
        val fakeId = "00000000-0000-0000-0000-000000000000"
        mockMvc.perform(
            get(getUrlTemplate.format(fakeId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return 404 when listing ratings for non-existent movie`() {
        val fakeId = "11111111-2222-3333-4444-555555555555"
        mockMvc.perform(
            get(ratingsUrlTemplate.format(fakeId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should allow creation when releaseYear is omitted`() {
        val noYear = """{"title":"NoYearMovie"}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(noYear)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.releaseYear").doesNotExist())
    }

    @Test
    fun `should reject title of only whitespace`() {
        val wsTitle = """{"title":"   ","releaseYear":$releaseYear}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(wsTitle)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error.details.title[0]", containsString("must not be blank")))
    }
}
