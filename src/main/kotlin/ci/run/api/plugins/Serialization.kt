package ci.run.api.plugins

import ci.run.api.model.User
import ci.run.api.model.UserData
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.text.DateFormat

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
        post("/login") {
//            val postParameters: Parameters = call.receiveParameters()

            val helloWorld = call.receive<UserData>()
            println(helloWorld.toString())
//            println(postParameters.get("name"))
//            call.respond(helloWorld)
        }
    }
}
