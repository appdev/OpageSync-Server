package ci.run.api.plugins


import ci.run.api.model.User
import ci.run.api.model.UserData
import ci.run.api.model.UserResult
import ci.run.api.model.createDefaultSetting
import ci.run.api.service.DatabaseFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    val secret = environment.config.property("jwt.secret").getString()
    install(ContentNegotiation) {
//        gson {
//            setDateFormat(DateFormat.LONG)
//            setPrettyPrinting()
//        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                coerceInputValues = true
                allowStructuredMapKeys = true
            })
        }

    }

    routing {
        post("/login") {
            val post = call.receive<UserData>()
            val setting = createDefaultSetting()
            val newUser = DatabaseFactory.dbOperate {
                User.new {
                    username = post.username
                    password = post.password
                    userSetting = setting
                }
            }
            call.respond(
                UserResult(
                    newUser.username, newUser.id.value,
                    getToken(newUser.username, newUser.id.value, secret),
                    setting.toResult()
                )
            )
        }

    }
}