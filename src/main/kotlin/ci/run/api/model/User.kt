package ci.run.api.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable(columnName = "user_id") {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
    val userSetting = reference("Settings", UserSettings)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var username by Users.username
    var password by Users.password
    var userSetting by UserSetting referencedOn Users.userSetting

    fun toResult(): UserResult {
        return UserResult(username, id.value, "", userSetting.toResult())
    }
}

@Serializable
data class UserResult(
    val username: String, val userId: Long, val token: String?,
    val userSetting: UserSettingResult
)

@Serializable
data class UserData(val username: String, val password: String)