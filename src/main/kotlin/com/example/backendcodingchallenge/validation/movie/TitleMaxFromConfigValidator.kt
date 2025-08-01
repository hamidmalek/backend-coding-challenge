package com.example.backendcodingchallenge.validation.movie

import com.example.backendcodingchallenge.config.ValidationProperties
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class TitleMaxFromConfigValidator(
    private val props: ValidationProperties
) : ConstraintValidator<TitleMaxFromConfig, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.length <= props.movie.title.maxLength
    }
}
