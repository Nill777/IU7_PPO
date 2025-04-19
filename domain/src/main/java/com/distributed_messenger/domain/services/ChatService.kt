package com.distributed_messenger.domain.services

import com.distributed_messenger.core.Chat
import com.distributed_messenger.domain.iservices.IChatService
import com.distributed_messenger.domain.irepositories.IChatRepository
import java.util.UUID

class ChatService(private val chatRepository: IChatRepository) : IChatService {
    override suspend fun createChat(
        name: String,
        creatorId: UUID,
        isGroupChat: Boolean,
        companionId: UUID?
    ): UUID {
        val chat = Chat(
            id = UUID.randomUUID(),
            name = name,
            creatorId = creatorId,
            companionId = companionId,
            isGroupChat = isGroupChat
        )
        return chatRepository.addChat(chat)
    }

    override suspend fun getChat(id: UUID): Chat? {
        return chatRepository.getChat(id)
    }

    override suspend fun getUserChats(userId: UUID): List<Chat> {
        return chatRepository.getChatsByUser(userId)
    }

    override suspend fun updateChat(id: UUID, name: String): Boolean {
        val chat = chatRepository.getChat(id) ?: return false
        val updatedChat = chat.copy(id = id, name = name)
        return chatRepository.updateChat(updatedChat)
    }

    override suspend fun deleteChat(id: UUID): Boolean {
        return chatRepository.deleteChat(id)
    }
}
