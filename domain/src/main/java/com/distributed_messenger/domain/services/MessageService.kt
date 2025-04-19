package com.distributed_messenger.domain.services

import com.distributed_messenger.core.Message
import com.distributed_messenger.domain.iservices.IMessageService
import com.distributed_messenger.domain.irepositories.IMessageRepository
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
        val updatedMessage = message.copy(id = id, content = newContent)
        return messageRepository.updateMessage(updatedMessage)
    }

    override suspend fun deleteMessage(id: UUID): Boolean {
        return messageRepository.deleteMessage(id)
    }
}
