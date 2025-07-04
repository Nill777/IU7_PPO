package com.distributed_messenger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.time.Instant
import java.util.UUID

@Entity(tableName = "message_history")
data class MessageHistoryEntity(
    @PrimaryKey @ColumnInfo(name = "history_id") val historyId: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "message_id") val messageId: UUID,
    @ColumnInfo(name = "edited_content") val editedContent: String,
    @ColumnInfo(name = "edit_timestamp") val editTimestamp: Instant
)