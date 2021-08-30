package ci.run.api.service

import ci.run.api.model.*
import ci.run.api.plugins.RequestParametersException
import ci.run.api.plugins.UserExistsException
import ci.run.api.plugins.UserNotFoundException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

fun Application.toDoService() {
    routing {
        authenticate("auth-jwt") {
            post("/todo/update") {
                updateOrNewToDo()
            }
            post("/todo/add") {
                updateOrNewToDo()
            }
            post("/todo/delete") {
                val formParameters = call.receiveParameters()
                val id = formParameters["todoId"]?.toLong()
                if (id != null && id != 0L) {
                    toDoRemove(id)
                } else throw RequestParametersException()
            }
            get("/todo/all") {
                val id = getUserID()
                val user = getUserById(id) ?: throw UserExistsException()
                findToDoByUserId(
                    id, user.userSetting?.deleteExpired == true,
                    user.userSetting?.expired ?: 30
                )
            }
        }
    }

}

private suspend fun PipelineContext<Unit, ApplicationCall>.updateOrNewToDo() {
    val id = getUserID()
    val todo = call.receive<ToDoListData>()
    todo.userId = id
    createOrUpdateToDo(todo)
    call.respond(success())
}

private fun PipelineContext<Unit, ApplicationCall>.getUserID(): Long {
    val principal = call.principal<JWTPrincipal>()
    val id = principal?.payload?.getClaim("id")?.asLong()
    if (id == null || id == 0L)
        throw  UserNotFoundException()
    return id
}