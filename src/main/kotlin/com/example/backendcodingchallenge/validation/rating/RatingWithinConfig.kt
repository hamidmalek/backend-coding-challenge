package com.example.backendcodingchallenge.validation.rating

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [RatingWithinConfigValidator::class])
annotation class RatingWithinConfig(
    val message: String = "rating is out of range",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
