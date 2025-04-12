package com.distributed_messenger.domain.repositories

import com.distributed_messenger.domain.models.Message
import java.util.UUID

interface IMessageRepository {
    suspend fun getMessage(id: UUID): Message?
    suspend fun getAllMessages(): List<Message>
    suspend fun getMessagesByChat(chatId: UUID): List<Message>
    suspend fun addMessage(message: Message): UUID
    suspend fun updateMessage(message: Message): Boolean
    suspend fun deleteMessage(id: UUID): Boolean
}
