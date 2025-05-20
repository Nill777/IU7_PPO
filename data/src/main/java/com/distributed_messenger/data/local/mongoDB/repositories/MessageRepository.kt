package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.Message
import com.distributed_messenger.domain.irepositories.IMessageRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant
import java.util.UUID

class MongoMessageRepository(private val collection: MongoCollection<Document>) : IMessageRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoMessageRepository"
    )

    override suspend fun getMessage(id: UUID): Message? =
        loggingWrapper {
            collection.find(Filters.eq("_id", id.toString()))
                .firstOrNull()
                ?.toMessage()
        }

    override suspend fun getAllMessages(): List<Message> =
        loggingWrapper {
            collection.find()
                .toList()
                .mapNotNull { it.toMessage() }
        }

    override suspend fun getMessagesByChat(chatId: UUID): List<Message> =
        loggingWrapper {
            collection.find(Filters.eq("chatId", chatId.toString()))
                .toList()
                .mapNotNull { it.toMessage() }
        }

    override suspend fun getLastMessageByChat(chatId: UUID): Message? =
        loggingWrapper {
            collection.find(Filters.eq("chatId", chatId.toString()))
                .sort(Sorts.descending("timestamp"))
                .firstOrNull()
                ?.toMessage()
        }

    override suspend fun addMessage(message: Message): UUID =
        loggingWrapper {
            collection.insertOne(message.toDocument())
            message.id
        }

    override suspend fun updateMessage(message: Message): Boolean =
        loggingWrapper {
            val result = collection.updateOne(
                Filters.eq("_id", message.id.toString()),
                message.toUpdateDocument()
            )
            result.modifiedCount == 1L
        }

    override suspend fun deleteMessage(id: UUID): Boolean =
        loggingWrapper {
            val result = collection.deleteOne(Filters.eq("_id", id.toString()))
            result.deletedCount == 1L
        }

    private fun Message.toDocument(): Document {
        return Document().apply {
            put("_id", id.toString())
            put("senderId", senderId.toString())
            put("chatId", chatId.toString())
            put("content", content)
            put("fileId", fileId?.toString()) // UUID? -> String?
            put("timestamp", timestamp.toEpochMilli())
        }
    }

    private fun Document.toMessage(): Message? {
        return try {
            Message(
                id = UUID.fromString(getString("_id")),
                senderId = UUID.fromString(getString("senderId")),
                chatId = UUID.fromString(getString("chatId")),
                content = getString("content"),
                fileId = get("fileId")?.toString()?.let { UUID.fromString(it) },
                timestamp = Instant.ofEpochMilli(getLong("timestamp"))
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Message.toUpdateDocument(): Bson {
        return com.mongodb.client.model.Updates.combine(
            com.mongodb.client.model.Updates.set("senderId", senderId.toString()),
            com.mongodb.client.model.Updates.set("chatId", chatId.toString()),
            com.mongodb.client.model.Updates.set("content", content),
            com.mongodb.client.model.Updates.set("fileId", fileId?.toString()),
            com.mongodb.client.model.Updates.set("timestamp", timestamp.toEpochMilli())
        )
    }
}