package com.distributed_messenger.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Message(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val content: String,
    val fileId: UUID? = null,
    val timestamp: Instant
) {
    fun formatTimestamp(): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return timestamp.atZone(ZoneId.systemDefault()).format(formatter)
    }
}