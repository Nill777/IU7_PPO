package com.distributed_messenger.implementation.repositories

import com.distributed_messenger.domain.models.Message
import com.distributed_messenger.domain.repositories.IMessageRepository
import com.distributed_messenger.data.local.dao.MessageDao
import com.distributed_messenger.data.local.entities.MessageEntity

class MessageRepository(private val messageDao: MessageDao) : IMessageRepository {
    override suspend fun getMessage(id: Int): Message? {
        return messageDao.getMessageById(id)?.toDomain()
    }

    override suspend fun getAllMessages(): List<Message> {
        return messageDao.getAllMessages().map { it.toDomain() }
    }

    override suspend fun getMessagesByChat(chatId: Int): List<Message> {
        return messageDao.getMessagesByChatId(chatId).map { it.toDomain() }
    }

    override suspend fun addMessage(message: Message): Int {
        return messageDao.insertMessage(message.toEntity()).toInt()
    }

    override suspend fun updateMessage(id: Int, message: Message): Boolean {
        return messageDao.updateMessage(message.toEntity()) > 0
    }

    override suspend fun deleteMessage(id: Int): Boolean {
        return messageDao.deleteMessage(id) > 0
    }

    private fun Message.toEntity(): MessageEntity {
        return MessageEntity(
            id = id,
            senderId = senderId,
            chatId = chatId,
            content = content,
            timestamp = timestamp,
            fileId = fileId
        )
    }

    private fun MessageEntity.toDomain(): Message {
        return Message(
            id = id,
            senderId = senderId,
            chatId = chatId,
            content = content,
            timestamp = timestamp,
            fileId = fileId
        )
    }
}