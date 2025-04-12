package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.time.Instant
import java.util.UUID

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "sender_id") val senderId: UUID,
    @ColumnInfo(name = "chat_id") val chatId: UUID,
    val content: String,
    val timestamp: Instant,
    @ColumnInfo(name = "file_id") val fileId: UUID? = null
)
