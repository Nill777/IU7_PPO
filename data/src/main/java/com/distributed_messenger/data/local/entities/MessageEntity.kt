package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.time.Instant
import java.util.UUID

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey @ColumnInfo(name = "message_id") val messageId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "sender_id") val senderId: UUID,
    @ColumnInfo(name = "chat_id") val chatId: UUID,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "file_id") val fileId: UUID? = null,
    @ColumnInfo(name = "timestamp") val timestamp: Instant
)