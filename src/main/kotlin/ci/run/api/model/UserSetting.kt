package ci.run.api.model

import org.jetbrains.exposed.dao.id.UUIDTable

object UserSetting : UUIDTable() {
    val useBg = bool("useBg")
    val useToDo = bool("useToDo")
    val useHitokoto = bool("useHitokoto")
    val sequelId = integer("sequel_id").uniqueIndex()

}