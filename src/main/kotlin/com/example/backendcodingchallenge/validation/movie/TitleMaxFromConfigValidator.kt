package com.example.backendcodingchallenge.validation.movie

import com.example.backendcodingchallenge.config.ValidationProperties
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class TitleMaxFromConfigValidator : ConstraintValidator<TitleMaxFromConfig, String?> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.length <= ValidationProperties.movie.title.maxLength
    }
}
