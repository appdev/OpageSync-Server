package ci.run.api.service

import ci.run.api.model.User
import ci.run.api.model.UserSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    val database: Database by lazy { Database.connect("jdbc:sqlite:data/data.db", "org.sqlite.JDBC") }

    fun init() {
        //初始化数据库表
        transaction(database) {
            create(User, UserSetting)

            //NOTE: Insert initial rows if any here
        }
    }

    //通用增删查改的通用函数保证，创建协程运行不阻塞主线程。
    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

    suspend fun drop() {
        dbQuery { drop(User, UserSetting) }
    }
}