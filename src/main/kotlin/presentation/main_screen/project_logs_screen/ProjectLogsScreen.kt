package presentation.main_screen.project_logs_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.ProjectLog
import presentation.main_screen.AppViewModel
import presentation.main_screen.project_logs_screen.components.EditLogDialog
import presentation.main_screen.project_logs_screen.components.LogRow
import java.time.format.DateTimeFormatter

@Composable
fun ProjectLogsScreen(
    projectName: String,
    projectId: Int,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val logs by viewModel.logs.collectAsState()
    var logBeingEdited by remember { mutableStateOf<ProjectLog?>(null) }
    var isEditingStartTime by remember { mutableStateOf(true) }
    var newDateTime by remember { mutableStateOf("") }

    LaunchedEffect(projectId) {
        viewModel.getProjectLogs(projectId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = projectName,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Start Time", Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
                Text("Stop Time", Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
                Text("Duration", Modifier.weight(1f), style = MaterialTheme.typography.subtitle2)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(logs) { log ->
                    LogRow(
                        log = log,
                        dateTimeFormatter = dateTimeFormatter,
                        onEditStartTime = { selectedLog ->
                            logBeingEdited = selectedLog
                            isEditingStartTime = true
                            newDateTime = selectedLog.startTime.format(dateTimeFormatter)
                        },
                        onEditStopTime = { selectedLog ->
                            logBeingEdited = selectedLog
                            isEditingStartTime = false
                            newDateTime = selectedLog.stopTime?.format(dateTimeFormatter) ?: ""
                        }
                    )
                }
            }

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.background
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Back to Projects")
            }
        }
    }

    EditLogDialog(
        logBeingEdited = logBeingEdited,
        isEditingStartTime = isEditingStartTime,
        newDateTime = newDateTime,
        dateTimeFormatter = dateTimeFormatter,
        onDateTimeChange = { newDateTime = it },
        onSave = { log, parsedDateTime ->
            if (isEditingStartTime) {
                viewModel.updateLogStartTime(log.logId, parsedDateTime)
            } else {
                viewModel.updateLogStopTime(log.logId, parsedDateTime)
            }
        },
        onDismiss = { logBeingEdited = null }
    )
}

