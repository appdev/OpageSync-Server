package ci.run.api.plugins

import ci.run.api.service.toDoService
import ci.run.api.service.userService
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

    userService()
    toDoService()

    routing {

        static("/static") {
            resources("static")
        }
        get("/") {
            call.respondText("Hello World!")
        }


    }
}


