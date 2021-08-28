//package ci.run.api.model
//
//import kotlinx.serialization.Serializable
//import org.jetbrains.exposed.dao.LongEntity
//import org.jetbrains.exposed.dao.LongEntityClass
//import org.jetbrains.exposed.dao.id.EntityID
//import org.jetbrains.exposed.dao.id.LongIdTable
//import java.util.*
//
//object Users : LongIdTable(columnName = "user_id") {
//    val username = varchar("username", 255).uniqueIndex()
//    val password = varchar("password", 255)
//    val userSetting = reference("Settings", UserSettings)
//}
//
//class User(id: EntityID<Long>) : LongEntity(id) {
//    companion object : LongEntityClass<User>(Users)
//
//    var username by Users.username
//    var password by Users.password
//    var userSetting by UserSetting referencedOn Users.userSetting
//}
//
//@Serializable
//data class UserResponse(val user: UserResponse.UserRes) {
//    @Serializable
//    data class UserRes(
//        val username: String, val token: String,
//        val userSetting: UserSetting
//    )
//
//    companion object {
//        fun fromUser(user: User, token: String = "",userSetting: UserSetting): UserResponse = UserResponse(
//            user = UserRes(
//                token = token,
//                username = user.username,
//                userSetting = user.userSetting,
//            )
//        )
//    }
//}
//data class Snippet(val user: String, val text: String)
//
//@Serializable
//data class UserData(val username: String, val password: String)
//
//val users = Collections.synchronizedMap(
//    listOf(UserData("test", "test"))
//        .associateBy { it.username }
//        .toMutableMap()
//)
//
//class InvalidCredentialsException(message: String) : RuntimeException(message)
//
//class LoginRegister(val user: String, val password: String)