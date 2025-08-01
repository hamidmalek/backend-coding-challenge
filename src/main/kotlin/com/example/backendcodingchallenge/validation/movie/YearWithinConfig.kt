package com.example.backendcodingchallenge.validation.movie

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [YearWithinConfigValidator::class])
annotation class YearWithinConfig(
    val message: String = "release year is out of range",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
