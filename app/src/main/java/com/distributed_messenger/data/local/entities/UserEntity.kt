package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "role") val role: String
)
