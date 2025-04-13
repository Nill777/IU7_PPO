package com.distributed_messenger.domain.controllers

import com.distributed_messenger.domain.services.IMessageService
import java.util.UUID

class MessageController(private val messageService: IMessageService) {
    suspend fun sendMessage(
        senderId: UUID,
        chatId: UUID,
        content: String,
        fileId: UUID? = null
    ) = messageService.sendMessage(senderId, chatId, content, fileId)

    suspend fun getMessage(id: UUID) = messageService.getMessage(id)

    suspend fun getChatMessages(chatId: UUID) =
        messageService.getChatMessages(chatId)

    suspend fun editMessage(id: UUID, newContent: String) =
        messageService.editMessage(id, newContent)

    suspend fun deleteMessage(id: UUID) =
        messageService.deleteMessage(id)
}