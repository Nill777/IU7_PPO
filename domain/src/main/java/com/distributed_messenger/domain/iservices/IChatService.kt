package com.distributed_messenger.domain.iservices

import com.distributed_messenger.core.Chat
import java.util.UUID

interface IChatService {
    suspend fun createChat(name: String, creatorId: UUID, isGroupChat: Boolean = false, companionId: UUID? = null): UUID
    suspend fun getChat(id: UUID): Chat?
    suspend fun getUserChats(userId: UUID): List<Chat>
    suspend fun updateChat(id: UUID, name: String): Boolean
    suspend fun deleteChat(id: UUID): Boolean
}
