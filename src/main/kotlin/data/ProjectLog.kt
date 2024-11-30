package data

import java.time.LocalDateTime

data class ProjectLog(
    val logId: Int,
    val projectId: Int,
    val startTime: LocalDateTime,
    val stopTime: LocalDateTime?
)