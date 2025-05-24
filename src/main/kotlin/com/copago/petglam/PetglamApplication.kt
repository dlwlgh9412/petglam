package com.copago.petglam

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title = "Petglam API",
    version = "1.0",
    description = "Petglam API"
))
class PetglamApplication

fun main(args: Array<String>) {
    runApplication<PetglamApplication>(*args)
}
