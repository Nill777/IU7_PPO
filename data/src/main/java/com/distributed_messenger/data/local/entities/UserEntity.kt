package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.distributed_messenger.core.UserRole
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "user_id") val userId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "role") val role: UserRole,
    @ColumnInfo(name = "blocked_users_id") val blockedUsersId: UUID? = null,
    @ColumnInfo(name = "profile_settings_id") val profileSettingsId: UUID,
    @ColumnInfo(name = "app_settings_id") val appSettingsId: UUID
)