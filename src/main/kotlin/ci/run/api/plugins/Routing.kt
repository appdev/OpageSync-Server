package ci.run.api.plugins

import ci.run.api.model.PostSnippet
import ci.run.api.model.Snippet
import ci.run.api.model.User
import ci.run.api.model.UserData
import ci.run.api.service.DatabaseFactory
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.request.*
import java.util.*

fun Application.configureRouting() {

    routing {
        get("/") {
            println(call.parameters.toString())

            call.respondText("${DatabaseFactory.database.version}")
        }
        get("/snippets/get") {
            call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
        }
        post("/snippets/post") {
            val post = call.receive<PostSnippet>()
            val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
            snippets += Snippet(principal.name, post.snippet.text)
            call.respond(mapOf("OK" to true))
        }
        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
            }
        }

    }
}

val snippets = Collections.synchronizedList(
    mutableListOf(
        Snippet(user = "test", text = "hello"),
        Snippet(user = "test", text = "world")
    )
)