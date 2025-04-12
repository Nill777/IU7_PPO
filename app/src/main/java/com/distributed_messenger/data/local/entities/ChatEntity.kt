package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "user_id") val creatorId: UUID,
    @ColumnInfo(name = "companion_id") val companionId: UUID? = null,
    @ColumnInfo(name = "is_group_chat") val isGroupChat: Boolean = false
)
