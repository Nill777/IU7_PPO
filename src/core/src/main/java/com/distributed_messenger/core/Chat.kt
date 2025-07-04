package com.distributed_messenger.core

import java.util.UUID

data class Chat(
    val id: UUID,
    val name: String,
    val creatorId: UUID,
    val companionId: UUID? = null,
    val isGroupChat: Boolean = false
)
