package data

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDateTime


class DatabaseManager(preferencesPath: String?) {

    private var databasePath: String

    init {
        val path = preferencesPath?.takeIf { it.isNotBlank() } ?: getDefaultDatabasePath()
        databasePath = buildDatabasePath(path)
        ensureDatabaseExists()
        initializeDatabase()
    }

    private fun getDefaultDatabasePath(): String {
        return System.getProperty("user.dir")
    }

    private fun ensureDatabaseExists() {
        val databaseFile = File(databasePath)
        if (!databaseFile.exists()) {
            databaseFile.parentFile?.mkdirs()
            databaseFile.createNewFile()
        }
    }

    private fun initializeDatabase() {
        Database.connect("jdbc:sqlite:$databasePath", driver = "org.sqlite.JDBC")
        setupDatabase()
    }

    private fun setupDatabase() {
        transaction {
            SchemaUtils.create(ProjectsTable, ProjectLogsTable)
        }
    }

    fun copyDatabaseToPath(newPath: String) {
        val oldDatabaseFile = File(databasePath)
        val newDatabaseFile = File(buildDatabasePath(newPath))

        if (!oldDatabaseFile.exists()) {
            throw IllegalStateException("Source database file does not exist: ${oldDatabaseFile.absolutePath}")
        }

        if (newDatabaseFile.exists()) {
            if (!newDatabaseFile.delete()) {
                throw IllegalStateException("Failed to delete existing database file at: ${newDatabaseFile.absolutePath}")
            }
        }

        newDatabaseFile.parentFile?.mkdirs()
        oldDatabaseFile.copyTo(newDatabaseFile, overwrite = true)
        oldDatabaseFile.delete()
    }

    fun reinitializeDatabase(newPath: String) {
        val path = buildDatabasePath(newPath)
        val databaseFile = File(path)
        if (!databaseFile.exists()) {
            databaseFile.parentFile?.mkdirs()
            databaseFile.createNewFile()
        }
        Database.connect("jdbc:sqlite:$path", driver = "org.sqlite.JDBC")
        setupDatabase()
        databasePath = path
    }

    private fun buildDatabasePath(basePath: String): String {
        val normalizedPath = basePath.trimEnd('/', '\\')
        return "$normalizedPath/project_tracker.db"
    }

    fun addProject(name: String) {
        transaction {
            ProjectsTable.insert {
                it[ProjectsTable.name] = name
            }
        }
    }

    fun deleteProject(projectId: Int) {
        transaction {
            ProjectLogsTable.deleteWhere {
                ProjectLogsTable.projectId eq projectId
            }

            ProjectsTable.deleteWhere {
                id eq projectId
            }
        }
    }

    fun getAllProjects(): List<Project> = transaction {
        ProjectsTable.selectAll().map {
            Project(
                id = it[ProjectsTable.id],
                name = it[ProjectsTable.name],
                isRunning = it[ProjectsTable.isRunning],
                totalTime = it[ProjectsTable.totalTime]
            )
        }
    }

    fun startProject(projectId: Int) {
        transaction {
            ProjectsTable.update({ ProjectsTable.id eq projectId }) {
                it[isRunning] = true
            }

            ProjectLogsTable.insert {
                it[ProjectLogsTable.projectId] = projectId
                it[startTime] = LocalDateTime.now()
            }
        }
    }

    fun stopProject(projectId: Int) {
        transaction {
            ProjectsTable.update({ ProjectsTable.id eq projectId }) {
                it[isRunning] = false
            }

            ProjectLogsTable.update({
                (ProjectLogsTable.projectId eq projectId) and (ProjectLogsTable.stopTime.isNull())
            }) {
                it[stopTime] = LocalDateTime.now()
            }
        }
    }

    fun getLogsForProject(projectId: Int): List<ProjectLog> = transaction {
        ProjectLogsTable.select { ProjectLogsTable.projectId eq projectId }
            .map {
                ProjectLog(
                    logId = it[ProjectLogsTable.logId],
                    projectId = it[ProjectLogsTable.projectId],
                    startTime = it[ProjectLogsTable.startTime],
                    stopTime = it[ProjectLogsTable.stopTime]
                )
            }
    }

    fun getProjectIdFromLogId(logId: Int): Int {
        return transaction {
            ProjectLogsTable.select { ProjectLogsTable.logId eq logId }
                .map { it[ProjectLogsTable.projectId] }
                .singleOrNull() ?: throw IllegalArgumentException("Log not found for ID: $logId")
        }
    }

    fun updateLogStartTime(logId: Int, newStartTime: LocalDateTime) {
        transaction {
            ProjectLogsTable.update({ ProjectLogsTable.logId eq logId }) {
                it[startTime] = newStartTime
            }
        }
    }

    fun updateLogStopTime(logId: Int, newStopTime: LocalDateTime?) {
        transaction {
            ProjectLogsTable.update({ ProjectLogsTable.logId eq logId }) {
                it[stopTime] = newStopTime
            }
        }
    }
}