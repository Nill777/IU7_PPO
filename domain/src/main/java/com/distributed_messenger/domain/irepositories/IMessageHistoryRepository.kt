package com.distributed_messenger.domain.irepositories

import com.distributed_messenger.core.MessageHistory
import java.util.UUID

interface IMessageHistoryRepository {
    suspend fun addMessageHistory(messageHistory: MessageHistory): UUID
    suspend fun getHistoryForMessage(messageId: UUID): List<MessageHistory>
}