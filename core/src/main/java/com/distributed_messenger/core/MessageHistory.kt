package com.distributed_messenger.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

data class MessageHistory(
    val historyId: UUID,
    val messageId: UUID,
    val editedContent: String,
    val editTimestamp: Instant
) {
    fun formatTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return editTimestamp.atZone(ZoneId.systemDefault()).format(formatter)
    }
}