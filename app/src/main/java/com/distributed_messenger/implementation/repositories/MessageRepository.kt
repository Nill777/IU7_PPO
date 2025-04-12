package com.distributed_messenger.implementation.repositories

import com.distributed_messenger.domain.models.Message
import com.distributed_messenger.domain.repositories.IMessageRepository
import com.distributed_messenger.data.local.dao.MessageDao
import com.distributed_messenger.data.local.entities.MessageEntity
import java.util.UUID

class MessageRepository(private val messageDao: MessageDao) : IMessageRepository {
    override suspend fun getMessage(id: UUID): Message? {
        return messageDao.getMessageById(id)?.toDomain()
    }

    override suspend fun getAllMessages(): List<Message> {
        return messageDao.getAllMessages().map { it.toDomain() }
    }

    override suspend fun getMessagesByChat(chatId: UUID): List<Message> {
        return messageDao.getMessagesByChatId(chatId).map { it.toDomain() }
    }

    override suspend fun addMessage(message: Message): UUID {
        val rowId = messageDao.insertMessage(message.toEntity())
        if (rowId == -1L) {
            throw Exception("Failed to insert message")
        }
        return message.id
    }

    override suspend fun updateMessage(message: Message): Boolean {
        return messageDao.updateMessage(message.toEntity()) > 0
    }

    override suspend fun deleteMessage(id: UUID): Boolean {
        return messageDao.deleteMessage(id) > 0
    }

    private fun Message.toEntity(): MessageEntity {
        return MessageEntity(
            messageId = id,
            senderId = senderId,
            chatId = chatId,
            content = content,
            fileId = fileId,
            timestamp = timestamp
        )
    }

    private fun MessageEntity.toDomain(): Message {
        return Message(
            id = messageId,
            senderId = senderId,
            chatId = chatId,
            content = content,
            fileId = fileId,
            timestamp = timestamp
        )
    }
}