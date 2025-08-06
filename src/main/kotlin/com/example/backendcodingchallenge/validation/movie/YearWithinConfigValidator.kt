package com.example.backendcodingchallenge.validation.movie

import com.example.backendcodingchallenge.config.ValidationProperties
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class YearWithinConfigValidator : ConstraintValidator<YearWithinConfig, Int?> {

    override fun isValid(value: Int?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        val r = ValidationProperties.movie.year
        return value in r.min..r.max
    }
}
