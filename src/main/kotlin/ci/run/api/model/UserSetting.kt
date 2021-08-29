package ci.run.api.model

import ci.run.api.service.DatabaseFactory
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime


object UserSettings : LongIdTable(columnName = "setting_id") {
    val useBg = bool("use_bg")
    val useToDo = bool("use_todo")
    val useHitokoto = bool("use_hitokoto")
    val createTime = datetime("setting_create_time").defaultExpression(CurrentDateTime())
}

/**
 * [toResult]
 */
class UserSetting(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSetting>(UserSettings)

    var useBg by UserSettings.useBg
    var useToDo by UserSettings.useToDo
    var useHitokoto by UserSettings.useHitokoto
    var createTime by UserSettings.createTime

    fun toResult(): UserSettingResult {
        return UserSettingResult(this.useBg, this.useToDo, this.useHitokoto)
    }
}


@Serializable
data class UserSettingResult(
    var useBg: Boolean, var useToDo: Boolean, var useHitokoto: Boolean
)

suspend fun createDefaultSetting(): UserSetting {
    return DatabaseFactory.dbOperate {
        UserSetting.new {
            useBg = true
            useToDo = true
            useHitokoto = true
        }
    }
}

suspend fun UserSetting.userSettingRemove() {
    return DatabaseFactory.dbOperate {
        this.delete()
    }
}

