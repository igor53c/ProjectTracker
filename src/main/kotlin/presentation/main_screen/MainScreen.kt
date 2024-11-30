package presentation.main_screen

import androidx.compose.runtime.*
import presentation.main_screen.project_list_screen.ProjectListScreen
import presentation.main_screen.project_logs_screen.ProjectLogsScreen
import presentation.main_screen.settings_screen.SettingsScreen

@Composable
fun mainScreen(viewModel: AppViewModel) {
    var currentScreen by remember { mutableStateOf("projectList") }
    var databasePath by remember { mutableStateOf(viewModel.getDatabasePath()) }

    when (currentScreen) {
        "projectList" -> {
            ProjectListScreen(
                viewModel = viewModel,
                onNavigateToLogs = { projectId -> currentScreen = "projectLogs/$projectId" },
                onSettingsClick = { currentScreen = "settings" }
            )
        }

        "settings" -> {
            SettingsScreen(
                currentPath = databasePath,
                onPathChange = { newPath ->
                    databasePath = newPath
                    viewModel.saveDatabasePath(newPath)
                },
                onBack = { currentScreen = "projectList" }
            )
        }

        else -> {
            val projectId = currentScreen.removePrefix("projectLogs/").toIntOrNull()
            if (projectId != null) {
                val projectName = viewModel.getProjectNameById(projectId)
                ProjectLogsScreen(
                    projectName = projectName,
                    projectId = projectId,
                    viewModel = viewModel,
                    onBack = { currentScreen = "projectList" }
                )
            }
        }
    }
}

