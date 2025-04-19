package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import com.distributed_messenger.data.local.entities.MessageEntity
import java.util.UUID

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE message_id = :id")
    suspend fun getMessageById(id: UUID): MessageEntity?

    @Query("SELECT * FROM messages")
    suspend fun getAllMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chat_id = :chatId")
    suspend fun getMessagesByChatId(chatId: UUID): List<MessageEntity>

    @Insert
    suspend fun insertMessage(message: MessageEntity): Long

    @Update
    suspend fun updateMessage(message: MessageEntity): Int

    // @Delete
    @Query("DELETE FROM messages WHERE message_id = :id")
    suspend fun deleteMessage(id: UUID): Int
}
