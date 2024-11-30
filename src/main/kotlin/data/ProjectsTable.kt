package data

import org.jetbrains.exposed.sql.Table

object ProjectsTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val isRunning = bool("is_running").default(false)
    val totalTime = long("total_time").default(0L)

    override val primaryKey = PrimaryKey(id)
}