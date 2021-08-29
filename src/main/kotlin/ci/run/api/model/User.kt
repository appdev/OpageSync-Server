package ci.run.api.model

import ci.run.api.plugins.createToken
import ci.run.api.service.DatabaseFactory
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Users : LongIdTable(columnName = "user_id") {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
    val createTime = datetime("user_create_time").defaultExpression(CurrentDateTime())
    val updateTime = datetime("user_update_time").defaultExpression(CurrentDateTime())
    val userSetting = reference("settings", UserSettings)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var username by Users.username
    var password by Users.password
    var updateTime by Users.updateTime
    val createTime by Users.createTime
    var userSetting by UserSetting referencedOn Users.userSetting

    fun toResult(): UserResult {
        return UserResult(username, id.value, "", userSetting.toResult())
    }
}

suspend fun createOrNewUser(post: UserData, setting: UserSetting): UserResult {
    return DatabaseFactory.dbOperate {
        val user = User.find { Users.username eq post.username }.firstOrNull()
        val selectUser = if (user != null) {
            if (user.password == post.password) {
                user.updateTime = DateTime.now()
                user
            } else throw Exception("用户名或密码错误")
        } else User.new {
            username = post.username
            password = post.password
            userSetting = setting
        }
        selectUser.toResult()
    }
}

suspend fun getUserById(id: Long): UserResult? {
    return DatabaseFactory.dbOperate {
        return@dbOperate User.findById(id)?.toResult()
    }
}

@Serializable
data class UserResult(
    val username: String, val userId: Long, var token: String?,
    val userSetting: UserSettingResult?
)

@Serializable
data class UserData(val username: String, val password: String)