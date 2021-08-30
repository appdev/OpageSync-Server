package ci.run.api.service

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.settingService() {
    routing {
        authenticate("auth-jwt") {}
    }
}