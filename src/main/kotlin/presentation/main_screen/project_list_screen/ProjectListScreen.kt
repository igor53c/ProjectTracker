package presentation.main_screen.project_list_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.main_screen.AppViewModel
import presentation.main_screen.components.AddProjectDialog
import presentation.main_screen.components.ProjectRow

@Composable
fun ProjectListScreen(
    viewModel: AppViewModel,
    onNavigateToLogs: (Int) -> Unit,
    onSettingsClick: () -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var newProjectName by remember { mutableStateOf("") }
    val projects by viewModel.projects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                Button(
                    onClick = { isDialogOpen = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add project",
                        tint = MaterialTheme.colors.background
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Project",
                        color = MaterialTheme.colors.background
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                items(projects) { project ->
                    ProjectRow(
                        project = project,
                        onStart = { viewModel.startProject(project.id) },
                        onStop = { viewModel.stopProject(project.id) },
                        onDelete = { viewModel.deleteProject(project.id) },
                        onClick = { onNavigateToLogs(project.id) }
                    )
                }
            }
        }
    }

    if (isDialogOpen) {
        AddProjectDialog(
            projectName = newProjectName,
            onNameChange = { newProjectName = it },
            onConfirm = {
                if (newProjectName.isNotBlank()) {
                    viewModel.addProject(newProjectName)
                    newProjectName = ""
                    isDialogOpen = false
                }
            }, onDismiss = { isDialogOpen = false }
        )
    }
}
