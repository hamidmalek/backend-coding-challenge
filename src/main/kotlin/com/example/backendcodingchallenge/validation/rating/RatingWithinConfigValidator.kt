package com.example.backendcodingchallenge.validation.rating

import com.example.backendcodingchallenge.config.ValidationProperties
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class RatingWithinConfigValidator(
    private val props: ValidationProperties
) : ConstraintValidator<RatingWithinConfig, Int> {
    override fun isValid(value: Int, context: ConstraintValidatorContext) =
        value in props.rating.min..props.rating.max
}
