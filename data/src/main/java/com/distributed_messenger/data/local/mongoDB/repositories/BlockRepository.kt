package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.Block
import com.distributed_messenger.domain.irepositories.IBlockRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant
import java.util.UUID

class MongoBlockRepository(private val collection: MongoCollection<Document>) : IBlockRepository {

    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoBlockRepository"
    )

    override suspend fun getBlock(id: UUID): Block? =
        loggingWrapper {
            collection.find(Filters.eq("_id", id.toString()))
                .firstOrNull()
                ?.toBlock()
        }

    override suspend fun getAllBlocks(): List<Block> =
        loggingWrapper {
            collection.find()
                .toList()
                .mapNotNull { it.toBlock() }
        }

    override suspend fun getBlocksByUser(userId: UUID): List<Block> =
        loggingWrapper {
            collection.find(
                Filters.or(
                    Filters.eq("blockerId", userId.toString()),
                    Filters.eq("blockedUserId", userId.toString())
                )
            ).toList().mapNotNull { it.toBlock() }
        }

    override suspend fun addBlock(block: Block): UUID =
        loggingWrapper {
            collection.insertOne(block.toDocument())
            block.id
        }

    override suspend fun updateBlock(block: Block): Boolean =
        loggingWrapper {
            val result = collection.updateOne(
                Filters.eq("_id", block.id.toString()),
                block.toUpdateDocument()
            )
            result.modifiedCount == 1L
        }

    override suspend fun deleteBlock(id: UUID): Boolean =
        loggingWrapper {
            val result = collection.deleteOne(Filters.eq("_id", id.toString()))
            result.deletedCount == 1L
        }

    override suspend fun deleteBlocksByUserId(blockerId: UUID, blockedUserId: UUID): Boolean =
        loggingWrapper {
            val result = collection.deleteMany(
                Filters.and(
                    Filters.eq("blockerId", blockerId.toString()),
                    Filters.eq("blockedUserId", blockedUserId.toString())
                )
            )
            result.deletedCount > 0
        }

    private fun Block.toDocument(): Document {
        return Document().apply {
            put("_id", id.toString())
            put("blockerId", blockerId.toString())
            put("blockedUserId", blockedUserId.toString())
            put("reason", reason)
            put("timestamp", timestamp.toEpochMilli())
        }
    }

    private fun Document.toBlock(): Block? {
        return try {
            Block(
                id = UUID.fromString(getString("_id")),
                blockerId = UUID.fromString(getString("blockerId")),
                blockedUserId = UUID.fromString(getString("blockedUserId")),
                reason = get("reason")?.toString(),
                timestamp = Instant.ofEpochMilli(getLong("timestamp"))
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Block.toUpdateDocument(): Bson {
        return Updates.combine(
            Updates.set("blockerId", blockerId.toString()),
            Updates.set("blockedUserId", blockedUserId.toString()),
            Updates.set("reason", reason),
            Updates.set("timestamp", timestamp.toEpochMilli())
        )
    }
}
