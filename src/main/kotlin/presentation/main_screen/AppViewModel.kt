package presentation.main_screen

import data.DatabaseManager
import data.Project
import data.ProjectLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.util.prefs.Preferences

class AppViewModel {
    private val preferences = Preferences.userNodeForPackage(AppViewModel::class.java)
    private val databasePathKey = "database_path"

    private var databasePath: String? = preferences.get(databasePathKey, null)
    private val dbManager = DatabaseManager(databasePath)

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _logs = MutableStateFlow<List<ProjectLog>>(emptyList())
    val logs: StateFlow<List<ProjectLog>> = _logs

    init {
        loadProjects()
    }

    private fun loadProjects() {
        _projects.value = dbManager.getAllProjects()
    }

    fun getDatabasePath(): String {
        val path = databasePath ?: System.getProperty("user.dir")
        return path
    }

    fun saveDatabasePath(path: String) {
        try {
            dbManager.copyDatabaseToPath(path)
            dbManager.reinitializeDatabase(path)
            preferences.put(databasePathKey, path)
            databasePath = path
        } catch (e: Exception) {
            println("Failed to save database path: ${e.message}")
            throw e
        }
    }

    fun addProject(name: String) {
        dbManager.addProject(name)
        loadProjects()
    }

    fun startProject(id: Int) {
        dbManager.startProject(id)
        loadProjects()
    }

    fun stopProject(id: Int) {
        dbManager.stopProject(id)
        loadProjects()
    }

    fun deleteProject(projectId: Int) {
        dbManager.deleteProject(projectId)
        loadProjects()
    }

    fun getProjectLogs(projectId: Int) {
        _logs.value = dbManager.getLogsForProject(projectId)
    }

    fun updateLogStartTime(logId: Int, newStartTime: LocalDateTime) {
        dbManager.updateLogStartTime(logId, newStartTime)
        getProjectLogs(dbManager.getProjectIdFromLogId(logId))
    }

    fun updateLogStopTime(logId: Int, newStopTime: LocalDateTime?) {
        dbManager.updateLogStopTime(logId, newStopTime)
        getProjectLogs(dbManager.getProjectIdFromLogId(logId))
    }

    fun getProjectNameById(projectId: Int): String {
        return _projects.value.firstOrNull { it.id == projectId }?.name ?: "Unknown Project"
    }

    fun onCleared() {

    }
}