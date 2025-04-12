package com.distributed_messenger.implementation.services

import com.distributed_messenger.domain.models.Message
import com.distributed_messenger.domain.services.IMessageService
import com.distributed_messenger.domain.repositories.IMessageRepository
import java.util.UUID
import java.time.Instant

class MessageService(private val messageRepository: IMessageRepository) : IMessageService {
    override suspend fun sendMessage(senderId: UUID, chatId: UUID, content: String, fileId: UUID?): UUID {
        val message = Message(
            id = UUID.randomUUID(),
            senderId = senderId,
            chatId = chatId,
            content = content,
            timestamp = Instant.now(),
            fileId = fileId
        )
        return messageRepository.addMessage(message)
    }

    override suspend fun getMessage(id: UUID): Message? {
        return messageRepository.getMessage(id)
    }

    override suspend fun getChatMessages(chatId: UUID): List<Message> {
        return messageRepository.getMessagesByChat(chatId)
    }

    override suspend fun editMessage(id: UUID, newContent: String): Boolean {
        val message = messageRepository.getMessage(id) ?: return false
        val updatedMessage = message.copy(content = newContent)
        return messageRepository.updateMessage(id, updatedMessage)
    }

    override suspend fun deleteMessage(id: UUID): Boolean {
        return messageRepository.deleteMessage(id)
    }
}
