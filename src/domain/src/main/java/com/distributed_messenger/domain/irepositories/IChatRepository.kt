package com.distributed_messenger.domain.irepositories

import com.distributed_messenger.core.Chat
import java.util.UUID

interface IChatRepository {
    suspend fun getChat(id: UUID): Chat?
    suspend fun getAllChats(): List<Chat>
    suspend fun getChatsByUser(userId: UUID): List<Chat>
    suspend fun addChat(chat: Chat): UUID
    suspend fun updateChat(chat: Chat): Boolean
    suspend fun deleteChat(id: UUID): Boolean
}
