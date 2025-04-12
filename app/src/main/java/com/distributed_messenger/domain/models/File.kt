package com.distributed_messenger.domain.models

import java.time.Instant
import java.util.UUID

data class File(
    val id: UUID,
    val name: String,
    val type: String,
    val path: String,
    val uploadedBy: UUID,
    val uploadedTimestamp: Instant
)
