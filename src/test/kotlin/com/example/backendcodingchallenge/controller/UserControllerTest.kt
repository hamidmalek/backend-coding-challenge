package com.example.backendcodingchallenge.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
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
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val username = "testuser"
    private val password = "pass1234"
    private val body = """{"username":"$username","password":"$password"}"""

    private val createUrl = "/api/v1/users"
    private val getUrlTemplate = "/api/v1/users/%s"
    private val profileUrlTemplate = "/api/v1/users/%s/profile"

    private lateinit var userId: String

    @BeforeEach
    fun setup() {
        val mvcResult = mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").value(username))
            .andReturn()

        val json = mvcResult.response.contentAsString
        val node = objectMapper.readTree(json)
        userId = node.get("id").asText()
    }

    @Test
    fun `should register a different new user`() {
        val newBody = """{"username":"user2","password":"newpass456"}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").value("user2"))
    }

    @Test
    fun `should not allow duplicate registration`() {
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `should fail to register with missing fields`() {
        val invalidBody = """{"username":"onlyUser"}"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail to register with malformed JSON`() {
        val malformed = """{"username":"bad","password":123"""
        mockMvc.perform(
            post(createUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformed)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should retrieve existing user`() {
        mockMvc.perform(
            get(getUrlTemplate.format(userId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.username").value(username))
    }

    @Test
    fun `should return 404 for non-existent user`() {
        val fakeId = "00000000-0000-0000-0000-000000000000"
        mockMvc.perform(
            get(getUrlTemplate.format(fakeId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should retrieve empty profile for user with no ratings`() {
        mockMvc.perform(
            get(profileUrlTemplate.format(userId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.user.id").value(userId))
            .andExpect(jsonPath("$.user.username").value(username))
            .andExpect(jsonPath("$.ratings", hasSize<Any>(0)))
    }

    @Test
    fun `should return 404 for profile of non-existent user`() {
        val fakeId = "11111111-2222-3333-4444-555555555555"
        mockMvc.perform(
            get(profileUrlTemplate.format(fakeId))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }
}
