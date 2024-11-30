package presentation.main_screen.project_logs_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import data.ProjectLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EditLogDialog(
    logBeingEdited: ProjectLog?,
    isEditingStartTime: Boolean,
    newDateTime: String,
    dateTimeFormatter: DateTimeFormatter,
    onDateTimeChange: (String) -> Unit,
    onSave: (ProjectLog, LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var isError by remember { mutableStateOf(false) }

    if (logBeingEdited != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Edit ${if (isEditingStartTime) "Start Time" else "Stop Time"}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDateTime,
                        onValueChange = {
                            onDateTimeChange(it)
                            isError = false
                        },
                        label = { Text("Enter Date and Time") },
                        placeholder = { Text("yyyy-MM-dd HH:mm:ss") },
                        isError = isError,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions.Default
                    )
                    if (isError) {
                        Text(
                            text = "Invalid date format. Use yyyy-MM-dd HH:mm:ss.",
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    try {
                        val parsedDateTime = LocalDateTime.parse(newDateTime, dateTimeFormatter)
                        logBeingEdited.let { log ->
                            onSave(log, parsedDateTime)
                        }
                        onDismiss()
                    } catch (e: Exception) {
                        isError = true // Show error when parsing fails
                        println("Invalid date format: ${e.message}")
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
