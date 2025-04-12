package com.distributed_messenger.implementation.repositories

import com.distributed_messenger.domain.models.Chat
import com.distributed_messenger.domain.repositories.IChatRepository
import com.distributed_messenger.data.local.dao.ChatDao
import com.distributed_messenger.data.local.entities.ChatEntity
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) : IChatRepository {
    override suspend fun getChat(id: UUID): Chat? {
        return chatDao.getChatById(id)?.toDomain()
    }

    override suspend fun getAllChats(): List<Chat> {
        return chatDao.getAllChats().map { it.toDomain() }
    }

    override suspend fun getChatsByUser(userId: UUID): List<Chat> {
        return chatDao.getChatsByUserId(userId).map { it.toDomain() }
    }

    override suspend fun addChat(chat: Chat): UUID {
        return chatDao.insertChat(chat.toEntity())
    }

    override suspend fun updateChat(chat: Chat): Boolean {
        return chatDao.updateChat(chat.toEntity()) > 0
    }

    override suspend fun deleteChat(id: UUID): Boolean {
        return chatDao.deleteChat(id) > 0
    }

    private fun Chat.toEntity(): ChatEntity {
        return ChatEntity(
            id = id,
            name = name,
            creatorId = creatorId,
            companionId = companionId,
            isGroupChat = isGroupChat
        )
    }

    private fun ChatEntity.toDomain(): Chat {
        return Chat(
            id = id,
            name = name,
            creatorId = creatorId,
            companionId = companionId,
            isGroupChat = isGroupChat
        )
    }
}