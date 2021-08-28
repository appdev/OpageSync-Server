package ci.run.api.plugins

import ci.run.api.model.User
import ci.run.api.model.error
import ci.run.api.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<AuthenticationException> { cause ->
            call.respond(error(403, "Unauthorized"))
        }
        exception<AuthorizationException> { cause ->
            call.respond(error(HttpStatusCode.Forbidden.value, HttpStatusCode.Forbidden.description))
        }
        exception<Throwable> { cause ->
            call.respond(error(500, cause.message))
        }
    }
    routing {

        static("/static") {
            resources("static")
        }
        get("/") {
            call.respondText("Hello World!")
        }
        get("/ui") {
            call.respondText(DatabaseFactory.database.version.toString())
        }
        get("/articles/feed") {
            call.respondText("Hello World!")
        }
//        authenticate("auth-jwt") {
//            get("/hello") {
//                val principal = call.principal<JWTPrincipal>()
//                val username = principal!!.payload.getClaim("username").asString()
//                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
//                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
//            }
//        }
        get("/user") {
            val principal = call.principal<JWTPrincipal>()
            principal?.let {
                val username = principal.payload.getClaim("username").asString()
                val id = principal.payload.getClaim("id").asLong()

                call.respond(User.findById(id)?.toResult() ?: "Empty")
            }
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
