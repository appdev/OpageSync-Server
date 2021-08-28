//package ci.run.api
//
//import ci.run.api.plugins.configureMonitoring
//import com.auth0.jwt.*
//import com.auth0.jwt.algorithms.*
//import io.ktor.application.*
//import io.ktor.auth.*
//import io.ktor.auth.jwt.*
//import io.ktor.features.*
//import io.ktor.gson.*
//import io.ktor.http.*
//import io.ktor.request.*
//import io.ktor.response.*
//import io.ktor.routing.*
//import java.text.DateFormat
//import java.util.*
//
//fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
//
//fun Application.module2() {
//    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
//    install(CORS) {
//        method(HttpMethod.Options)
//        method(HttpMethod.Get)
//        method(HttpMethod.Post)
//        method(HttpMethod.Put)
//        method(HttpMethod.Delete)
//        method(HttpMethod.Patch)
//        header(HttpHeaders.Authorization)
//        allowCredentials = true
//        anyHost()
//    }
//    configureMonitoring()
//    install(StatusPages) {
//        exception<InvalidCredentialsException> { exception ->
//            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
//        }
//    }
//    install(Authentication) {
//        jwt {
//            verifier(simpleJwt.verifier)
//            validate {
//                UserIdPrincipal(it.payload.getClaim("name").asString())
//            }
//        }
//    }
//    install(ContentNegotiation) {
//        gson {
//            setDateFormat(DateFormat.LONG)
//            setPrettyPrinting()
//        }
//    }
//    routing {
//        post("/login-register") {
//            val post = call.receive<LoginRegister>()
//            val user = users.getOrPut(post.user) { User(post.user, post.password) }
//            if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
//            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
//        }
//        route("/snippets") {
//            get {
//                call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
//            }
//            authenticate {
//                post {
//                    val post = call.receive<PostSnippet>()
//                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
//                    snippets += Snippet(principal.name, post.snippet.text)
//                    call.respond(mapOf("OK" to true))
//                }
//            }
//        }
//    }
//}
//
//data class PostSnippet(val snippet: PostSnippet.Text) {
//    data class Text(val text: String)
//}
//
//data class Snippet(val user: String, val text: String)
//
//val snippets = Collections.synchronizedList(
//    mutableListOf(
//        Snippet(user = "test", text = "hello"),
//        Snippet(user = "test", text = "world")
//    )
//)
//
//open class SimpleJWT(val secret: String) {
//    private val algorithm = Algorithm.HMAC256(secret)
//    val verifier = JWT.require(algorithm).build()
//    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
//}
//
//class User(val name: String, val password: String)
//
//val users = Collections.synchronizedMap(
//    listOf(User("test", "test"))
//        .associateBy { it.name }
//        .toMutableMap()
//)
//
//class InvalidCredentialsException(message: String) : RuntimeException(message)
//
//class LoginRegister(val user: String, val password: String)