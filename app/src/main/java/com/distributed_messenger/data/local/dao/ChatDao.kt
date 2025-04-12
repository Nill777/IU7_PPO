package com.distributed_messenger.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import com.distributed_messenger.data.local.entities.ChatEntity
import java.util.UUID

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getChatById(id: UUID): ChatEntity?

    @Query("SELECT * FROM chats")
    suspend fun getAllChats(): List<ChatEntity>

    @Query("SELECT * FROM chats WHERE user_id = :userId OR companion_id = :userId")
    suspend fun getChatsByUserId(userId: UUID): List<ChatEntity>

    @Insert
    suspend fun insertChat(chat: ChatEntity): Long

    @Update
    suspend fun updateChat(chat: ChatEntity): Int

    @Delete
    suspend fun deleteChat(id: UUID): Int
}
