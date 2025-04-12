package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey @ColumnInfo(name = "chat_id") val chatId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "chat_name") val chatName: String,
    @ColumnInfo(name = "user_id") val userId: UUID,
    @ColumnInfo(name = "companion_id") val companionId: UUID? = null,
    @ColumnInfo(name = "is_group_chat") val isGroupChat: Boolean = false
)