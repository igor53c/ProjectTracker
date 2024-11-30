package presentation.main_screen.project_logs_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.ProjectLog
import java.time.Duration
import java.time.format.DateTimeFormatter

@Composable
fun LogRow(
    log: ProjectLog,
    dateTimeFormatter: DateTimeFormatter,
    onEditStartTime: (ProjectLog) -> Unit,
    onEditStopTime: (ProjectLog) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Text(
            text = log.startTime.format(dateTimeFormatter),
            modifier = Modifier
                .weight(1f)
                .clickable { onEditStartTime(log) },
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary
        )

        val stopTimeText = log.stopTime?.format(dateTimeFormatter) ?: "Still running"
        val stopTimeColor = if (log.stopTime == null) {
            MaterialTheme.colors.onBackground
        } else {
            MaterialTheme.colors.primary
        }
        val stopTimeModifier = if (log.stopTime == null) {
            Modifier.weight(1f)
        } else {
            Modifier
                .weight(1f)
                .clickable { onEditStopTime(log) }
        }
        Text(
            text = stopTimeText,
            modifier = stopTimeModifier,
            style = MaterialTheme.typography.body1,
            color = stopTimeColor
        )

        // Duration
        val duration = log.stopTime?.let {
            Duration.between(log.startTime, it)
        }
        val formattedDuration = duration?.let {
            formatDuration(it)
        } ?: "Running..."
        Text(
            text = formattedDuration,
            Modifier.weight(1f),
            style = MaterialTheme.typography.body1
        )
    }
}

fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
