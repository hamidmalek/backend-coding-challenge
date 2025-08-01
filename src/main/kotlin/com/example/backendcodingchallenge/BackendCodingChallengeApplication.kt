package com.example.backendcodingchallenge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BackendCodingChallengeApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<BackendCodingChallengeApplication>(*args)
}
