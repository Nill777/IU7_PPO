package com.distributed_messenger.domain.iservices

import com.distributed_messenger.core.Message
import java.util.UUID

interface IMessageService {
    suspend fun sendMessage(senderId: UUID, chatId: UUID, content: String, fileId: UUID? = null): UUID
    suspend fun getMessage(id: UUID): Message?
    suspend fun getChatMessages(chatId: UUID): List<Message>
    suspend fun editMessage(id: UUID, newContent: String): Boolean
    suspend fun deleteMessage(id: UUID): Boolean
}
