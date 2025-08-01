package com.example.backendcodingchallenge.validation.movie

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TitleMaxFromConfigValidator::class])
annotation class TitleMaxFromConfig(
    val message: String = "title is too long",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
