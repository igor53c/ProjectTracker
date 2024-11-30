package presentation.main_screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddProjectDialog(
    projectName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Project") },
        text = {
            TextField(
                value = projectName,
                onValueChange = onNameChange,
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(
                    text = "Add",
                    color = MaterialTheme.colors.background
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colors.background
                )
            }
        }
    )
}