package ci.run.api.service

import ci.run.api.model.ToDos
import ci.run.api.model.UserResult
import ci.run.api.model.UserSettings
import ci.run.api.model.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    val database: Database by lazy { Database.connect("jdbc:sqlite:data/data.db", "org.sqlite.JDBC") }

    fun init() {
        //初始化数据库表
        transaction(database) {
            addLogger(KotlinLoggingSqlLogger)
            create(Users, UserSettings,ToDos)
            //NOTE: Insert initial rows if any here
        }
    }

    //通用增删查改的通用函数保证，创建协程运行不阻塞主线程。
    suspend fun <T> dbOperate(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

    suspend fun drop() {
        dbOperate { drop(Users, UserSettings,ToDos) }
    }
}

object KotlinLoggingSqlLogger : SqlLogger {
    private val logger = LoggerFactory.getLogger(KotlinLoggingSqlLogger::class.java)
    override
    fun log(context: StatementContext, transaction: Transaction) {
        logger.info("SQL: ${context.expandArgs(transaction)}")
    }
}