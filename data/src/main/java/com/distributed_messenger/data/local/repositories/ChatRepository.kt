package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.Chat
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.distributed_messenger.domain.irepositories.IChatRepository
import com.distributed_messenger.data.local.dao.ChatDao
import com.distributed_messenger.data.local.entities.ChatEntity
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) : IChatRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "ChatRepository"
    )

    override suspend fun getChat(id: UUID): Chat? =
        loggingWrapper {
            chatDao.getChatById(id)?.toDomain()
        }

    override suspend fun getAllChats(): List<Chat> =
        loggingWrapper {
            chatDao.getAllChats().map { it.toDomain() }
        }

    override suspend fun getChatsByUser(userId: UUID): List<Chat> =
        loggingWrapper {
            chatDao.getChatsByUserId(userId).map { it.toDomain() }
        }

    override suspend fun addChat(chat: Chat): UUID =
        loggingWrapper {
            val rowId = chatDao.insertChat(chat.toEntity())
            if (rowId == -1L) {
                throw Exception("Failed to insert chat")
            }
            chat.id
        }

    override suspend fun updateChat(chat: Chat): Boolean =
        loggingWrapper {
            chatDao.updateChat(chat.toEntity()) > 0
        }

    override suspend fun deleteChat(id: UUID): Boolean =
        loggingWrapper {
            chatDao.deleteChat(id) > 0
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