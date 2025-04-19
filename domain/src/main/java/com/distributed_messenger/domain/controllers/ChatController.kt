package com.distributed_messenger.domain.controllers

import com.distributed_messenger.domain.iservices.IChatService
import java.util.UUID

class ChatController(private val chatService: IChatService) {
    suspend fun createChat(
        name: String,
        creatorId: UUID,
        isGroupChat: Boolean = false,
        companionId: UUID? = null
    ) = chatService.createChat(name, creatorId, isGroupChat, companionId)

    suspend fun getChat(id: UUID) = chatService.getChat(id)

    suspend fun getUserChats(userId: UUID) =
        chatService.getUserChats(userId)

    suspend fun updateChat(id: UUID, name: String) =
        chatService.updateChat(id, name)

    suspend fun deleteChat(id: UUID) =
        chatService.deleteChat(id)
}