package ci.run.api.service

import ci.run.api.model.*
import ci.run.api.plugins.UserNotFoundException
import ci.run.api.plugins.createToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.nio.file.attribute.UserPrincipalNotFoundException
import java.sql.SQLException

fun Application.userService() {
    routing {
        post("/login") {
            val post = call.receive<UserData>()
            val setting = createDefaultSetting()
            try {
                val newUser = createOrNewUser(post, setting)
                newUser.token = createToken(newUser.username, newUser.userId)
                call.respond(success(newUser))
            } catch (e: Exception) {
                setting.userSettingRemove()
                if (e is SQLException)
                    if (e.message?.contains("SQLITE_CONSTRAINT_UNIQUE") == true)
                        throw Exception("用户名已存在")
                throw e
            }
        }

        post("/test") {
            val setting = createDefaultSetting()
            call.respond(
                success(
                    setting.toResult()
                )

            )
        }
        authenticate("auth-jwt") {
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                principal?.let {
                    val id = principal.payload.getClaim("id").asLong()
                    val user = getUserById(id)
                    if (user != null)
                        call.respond(success(getUserById(id)))
                    else throw UserNotFoundException()
                }
            }
        }
    }

}