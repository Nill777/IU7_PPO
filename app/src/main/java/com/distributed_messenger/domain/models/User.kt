package com.distributed_messenger.domain.models

import java.util.UUID

enum class UserRole {
    UNAUTHORIZED_USER,
    USER,
    MODERATOR,
    ADMINISTRATOR
}

data class User(
    val id: UUID,
    val username: String,
    val role: UserRole
)
