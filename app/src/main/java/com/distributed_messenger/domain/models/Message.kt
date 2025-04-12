package com.distributed_messenger.domain.models

import java.time.Instant
import java.util.UUID

data class Message(
    val id: UUID,
    val senderId: UUID,
    val chatId: UUID,
    val content: String,
    val timestamp: Instant,
    val fileId: UUID? = null
)
