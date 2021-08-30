package ci.run.api.model

import ci.run.api.model.Users.defaultExpression
import ci.run.api.service.DatabaseFactory
import ci.run.api.utils.Utils
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime
import org.joda.time.Days

object ToDos : LongIdTable(columnName = "todo_id") {
    val todo = varchar("todo", 1000)
    val isComplete = bool("is_finish").nullable().default(false)
    val createTime = datetime("todo_create_time").defaultExpression(CurrentDateTime())
    val completeTime = datetime("todo_complete_time").nullable()
    var userId = long("user_id")
}

class ToDo(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ToDo>(ToDos)

    var todo by ToDos.todo
    var isComplete by ToDos.isComplete
    var createTime by ToDos.createTime
    var completeTime by ToDos.completeTime
    var userId by ToDos.userId

    fun toResult(): ToDoListData {
        return ToDoListData(id.value, todo, userId, isComplete)
    }
}

fun SizedIterable<ToDo>.deleteExpiredTodo(expired: Int): List<ToDo> {
    val now = DateTime.now()
    val expiredList = this.filter {
        it.completeTime != null && Days.daysBetween(it.completeTime, now).days > expired
    }.toMutableList()
    expiredList.forEach { it.delete() }
    return this - expiredList
}

suspend fun toDoRemove(todoId: Long) {
    DatabaseFactory.dbOperate {
        ToDos.deleteWhere { ToDos.id.eq(todoId) }
    }
}

// 查找 todolist
suspend fun findToDoByUserId(userId: Long, deleteExpired: Boolean, expired: Int): MutableList<ToDoListData> {
    return DatabaseFactory.dbOperate {
        val userToDo = ToDo.find { ToDos.userId eq userId }
        val todo = if (deleteExpired) {
            userToDo.deleteExpiredTodo(expired)
        } else
            userToDo
        return@dbOperate todo.map { it.toResult() }
            .toMutableList()
    }
}

suspend fun createOrUpdateToDo(data: ToDoListData): ToDo? {
    return DatabaseFactory.dbOperate {
        if (data.todoId == 0L) {
            ToDo.new {
                todo = data.todo
                isComplete = data.isComplete
                userId = data.userId
            }
        } else {
            val todo = ToDo.findById(data.todoId)
            todo?.isComplete = data.isComplete
            if (data.isComplete == true)
                todo?.completeTime = Utils.getCurrentData()
           todo
        }
    }
}

@Serializable
data class ToDoListData(
    val todoId: Long = 0,
    val todo: String, var userId: Long = 0, val isComplete: Boolean? = false
)