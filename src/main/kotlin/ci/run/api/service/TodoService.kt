package ci.run.api.service

import ci.run.api.model.*
import ci.run.api.plugins.UserNotFoundException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.toDoService() {
    routing {
        authenticate("auth-jwt") {
            post("/todo/update") {

            }
            post("/todo/add") {
                val principal = call.principal<JWTPrincipal>()
                val id = principal?.payload?.getClaim("id")?.asLong()
                if (id == null || id == 0L)
                    throw  UserNotFoundException()
                val todo = call.receive<ToDoListData>()
                todo.userId = id
                createOrUpdateToDo(todo)
                call.respond(success())


            }
            post("/todo/delete") {

            }
        }
    }

}