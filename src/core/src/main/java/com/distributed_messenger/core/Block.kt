package com.distributed_messenger.core

import java.time.Instant
import java.util.UUID

data class Block(
    val id: UUID,
    val blockerId: UUID,
    val blockedUserId: UUID,
    val reason: String? = null,
    val timestamp: Instant
)
