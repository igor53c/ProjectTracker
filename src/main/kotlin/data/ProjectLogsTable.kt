package data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ProjectLogsTable : Table() {
    val logId = integer("log_id").autoIncrement()
    val projectId = integer("project_id").references(ProjectsTable.id)
    val startTime = datetime("start_time")
    val stopTime = datetime("stop_time").nullable()

    override val primaryKey = PrimaryKey(logId)
}