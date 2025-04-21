package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.Chat
import com.distributed_messenger.domain.irepositories.IChatRepository
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
        val rowId = chatDao.insertChat(chat.toEntity())
        if (rowId == -1L) {
            throw Exception("Failed to insert chat")
        }
        return chat.id
    }

    override suspend fun updateChat(chat: Chat): Boolean {
        return chatDao.updateChat(chat.toEntity()) > 0
    }

    override suspend fun deleteChat(id: UUID): Boolean {
        return chatDao.deleteChat(id) > 0
    }

    private fun Chat.toEntity(): ChatEntity {
        return ChatEntity(
            chatId = id,
            chatName = name,
            userId = creatorId,
            companionId = companionId,
            isGroupChat = isGroupChat
        )
    }

    private fun ChatEntity.toDomain(): Chat {
        return Chat(
            id = chatId,
            name = chatName,
            creatorId = userId,
            companionId = companionId,
            isGroupChat = isGroupChat
        )
    }
}