package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.MessageHistory
import com.distributed_messenger.domain.irepositories.IMessageHistoryRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.bson.Document
import java.time.Instant
import java.util.UUID

class MongoMessageHistoryRepository(
    private val collection: MongoCollection<Document>
) : IMessageHistoryRepository {

    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoMessageHistoryRepository"
    )

    override suspend fun addMessageHistory(messageHistory: MessageHistory): UUID =
        loggingWrapper {
            collection.insertOne(messageHistory.toDocument())
            messageHistory.historyId
        }

    override suspend fun getHistoryForMessage(messageId: UUID): List<MessageHistory> =
        loggingWrapper {
            collection.find(Filters.eq("messageId", messageId.toString()))
                .toList()
                .mapNotNull { it.toMessageHistory() }
        }

    override suspend fun getAllMessageHistory(): List<MessageHistory> =
        loggingWrapper {
            collection.find()
                .toList()
                .mapNotNull { it.toMessageHistory() }
        }

    private fun MessageHistory.toDocument(): Document {
        return Document().apply {
            put("_id", historyId.toString())
            put("messageId", messageId.toString())
            put("editedContent", editedContent)
            put("editTimestamp", editTimestamp.toEpochMilli())
        }
    }

    private fun Document.toMessageHistory(): MessageHistory? {
        return try {
            MessageHistory(
                historyId = UUID.fromString(getString("_id")),
                messageId = UUID.fromString(getString("messageId")),
                editedContent = getString("editedContent"),
                editTimestamp = Instant.ofEpochMilli(getLong("editTimestamp"))
            )
        } catch (e: Exception) {
            null
        }
    }
}