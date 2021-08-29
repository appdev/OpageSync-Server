package ci.run.api.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                coerceInputValues = true
                allowStructuredMapKeys = true
            })
        }

    }
}