package com.example.backendcodingchallenge

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = mutableMapOf<String, List<String>>()

        ex.bindingResult.fieldErrors.forEach {
            val field = it.field
            val message = it.defaultMessage ?: "Invalid"
            errors.compute(field) { _, messages ->
                (messages ?: listOf()) + message
            }
        }

        val errorResponse = mapOf(
            "error" to mapOf(
                "code" to "VALIDATION_ERROR",
                "details" to errors
            )
        )

        return ResponseEntity.badRequest().body(errorResponse)
    }
}
