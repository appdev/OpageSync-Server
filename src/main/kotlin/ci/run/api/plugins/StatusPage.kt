package ci.run.api.plugins

import ci.run.api.model.error
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*


fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<AuthenticationException> {
            call.respond(error(403, "Unauthorized"))
        }
        exception<AuthorizationException> {
            call.respond(error(HttpStatusCode.Forbidden.value, HttpStatusCode.Forbidden.description))
        }
        exception<UserNotFoundException> {
            call.respond(error(HttpStatusCode.Unauthorized.value, "用户认证失败，请重新登录"))
        }
        exception<RequestParametersException> {
            call.respond(error(HttpStatusCode.Unauthorized.value, "请求参数异常，请重试"))
        }
        val dev = environment.config.property("ktor.development").getString().toBoolean()
        if (!dev) {
            exception<Throwable> { cause ->
                call.respond(error(500, cause.message))
            }
            status(HttpStatusCode.NotFound) {
                call.respond(error(HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description))
            }
        }

        status(HttpStatusCode.Unauthorized) {
            call.respond(error(HttpStatusCode.Unauthorized.value, "用户认证失败，请重新登录"))
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
class UserExistsException : RuntimeException()
class UserDoesNotExistsException : RuntimeException()
class UserNotFoundException : RuntimeException()
class RequestParametersException : RuntimeException()