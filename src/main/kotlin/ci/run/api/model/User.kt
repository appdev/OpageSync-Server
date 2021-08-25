package ci.run.api.model

import org.jetbrains.exposed.sql.Table

class User : Table() {
    val id = integer("id")
    val name = varchar("name", 50)
    val director = varchar("director", 50)
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id, name = "id")
}