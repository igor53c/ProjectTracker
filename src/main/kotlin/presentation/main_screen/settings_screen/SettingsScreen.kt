package presentation.main_screen.settings_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import javax.swing.JFileChooser

@Composable
fun SettingsScreen(
    currentPath: String,
    onPathChange: (String) -> Unit,
    onBack: () -> Unit
) {
    var newPath by remember { mutableStateOf(currentPath) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Database Path",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newPath,
                    onValueChange = { newPath = it },
                    label = { Text("Database Path") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val chosenPath = choosePathDialog()
                        if (chosenPath != null) {
                            newPath = chosenPath
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose Path")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) {
                    Text("Cancel")
                }
                Button(onClick = {
                    onPathChange(newPath)
                    onBack()
                }) {
                    Text("Save")
                }
            }
        }
    }
}

fun choosePathDialog(): String? {
    val chooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    }
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile.absolutePath
    } else null
}
