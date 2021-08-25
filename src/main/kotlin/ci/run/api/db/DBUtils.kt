package ci.run.api.db

import org.jetbrains.exposed.sql.Database

object DBUtils {
    val database by lazy { Database.connect("jdbc:sqlite:date/data.db", "org.sqlite.JDBC") }
}