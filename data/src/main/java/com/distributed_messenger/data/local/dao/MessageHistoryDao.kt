package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.distributed_messenger.data.local.entities.MessageHistoryEntity
import java.util.UUID

@Dao
interface MessageHistoryDao {
    @Insert
    suspend fun insertMessageHistory(messageHistory: MessageHistoryEntity): Long

    @Query("SELECT * FROM message_history WHERE message_id = :messageId")
    suspend fun getHistoryForMessage(messageId: UUID): List<MessageHistoryEntity>
}