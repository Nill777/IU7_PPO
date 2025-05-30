package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.MessageHistory
import com.distributed_messenger.data.local.dao.MessageHistoryDao
import com.distributed_messenger.data.local.entities.MessageHistoryEntity
import com.distributed_messenger.domain.irepositories.IMessageHistoryRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import java.util.UUID

class MessageHistoryRepository(private val messageHistoryDao: MessageHistoryDao) : IMessageHistoryRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MessageHistoryRepository"
    )

    override suspend fun addMessageHistory(messageHistory: MessageHistory): UUID =
        loggingWrapper {
            val entity = messageHistory.toEntity()
            val rowId = messageHistoryDao.insertMessageHistory(entity)
            if (rowId == -1L) throw Exception("Failed to insert message history")
            messageHistory.historyId
        }

    override suspend fun getHistoryForMessage(messageId: UUID): List<MessageHistory> =
        loggingWrapper {
            messageHistoryDao.getHistoryForMessage(messageId)
                .map { it.toDomain() }
        }

    override suspend fun getAllMessageHistory(): List<MessageHistory> =
        loggingWrapper {
            messageHistoryDao.getAllMessageHistory()
                .map { it.toDomain() }
        }

    private fun MessageHistory.toEntity(): MessageHistoryEntity =
        MessageHistoryEntity(
            historyId = historyId,
            messageId = messageId,
            editedContent = editedContent,
            editTimestamp = editTimestamp
        )

    private fun MessageHistoryEntity.toDomain(): MessageHistory =
        MessageHistory(
            historyId = historyId,
            messageId = messageId,
            editedContent = editedContent,
            editTimestamp = editTimestamp
        )
}