package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.Chat
import com.distributed_messenger.domain.irepositories.IChatRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.conversions.Bson
import java.util.UUID

class MongoChatRepository(private val collection: MongoCollection<Document>) : IChatRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoChatRepository"
    )

    override suspend fun getChat(id: UUID): Chat? =
        loggingWrapper {
            collection.find(Filters.eq("_id", id.toString()))
                .firstOrNull()
                ?.toChat()
        }

    override suspend fun getAllChats(): List<Chat> =
        loggingWrapper {
            collection.find()
                .toList()
                .mapNotNull { it.toChat() }
        }

    override suspend fun getChatsByUser(userId: UUID): List<Chat> =
        loggingWrapper {
            collection.find(
                Filters.or(
                    Filters.eq("creatorId", userId.toString()),
                    Filters.eq("companionId", userId.toString())
                )
            ).toList().mapNotNull { it.toChat() }
        }

    override suspend fun addChat(chat: Chat): UUID =
        loggingWrapper {
            collection.insertOne(chat.toDocument())
            chat.id
        }

    override suspend fun updateChat(chat: Chat): Boolean =
        loggingWrapper {
            val result = collection.updateOne(
                Filters.eq("_id", chat.id.toString()),
                chat.toUpdateDocument()
            )
            result.modifiedCount == 1L
        }

    override suspend fun deleteChat(id: UUID): Boolean =
        loggingWrapper {
            val result = collection.deleteOne(Filters.eq("_id", id.toString()))
            result.deletedCount == 1L
        }

    private fun Chat.toDocument(): Document {
        return Document().apply {
            put("_id", id.toString())
            put("name", name)
            put("creatorId", creatorId.toString())
            put("companionId", companionId?.toString())
            put("isGroupChat", isGroupChat)
        }
    }

    private fun Document.toChat(): Chat? {
        return try {
            Chat(
                id = UUID.fromString(getString("_id")),
                name = getString("name"),
                creatorId = UUID.fromString(getString("creatorId")),
                companionId = getString("companionId")?.let { UUID.fromString(it) },
                isGroupChat = getBoolean("isGroupChat", false)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Chat.toUpdateDocument(): Bson {
        return Updates.combine(
            Updates.set("name", name),
            Updates.set("creatorId", creatorId.toString()),
            Updates.set("companionId", companionId?.toString()),
            Updates.set("isGroupChat", isGroupChat)
        )
    }
}