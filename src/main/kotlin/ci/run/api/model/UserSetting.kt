package ci.run.api.model

import ci.run.api.service.DatabaseFactory.dbOperate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserSettings : LongIdTable(columnName = "setting_id") {
    val useBg = bool("useBg")
    val useToDo = bool("useToDo")
    val useHitokoto = bool("useHitokoto")
}

class UserSetting(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserSetting>(UserSettings)

    var useBg by UserSettings.useBg
    var useToDo by UserSettings.useToDo
    var useHitokoto by UserSettings.useHitokoto
    fun toResult(): UserSettingResult {
        return UserSettingResult(useBg, useToDo, useHitokoto)
    }
}
@Serializable
data class UserSettingResult(
    var useBg: Boolean, val useToDo: Boolean, val useHitokoto: Boolean
)

suspend fun createDefaultSetting(): UserSetting {
    return dbOperate {
        UserSetting.new {
            useBg = true
            useToDo = true
            useHitokoto = true
        }
    }
}