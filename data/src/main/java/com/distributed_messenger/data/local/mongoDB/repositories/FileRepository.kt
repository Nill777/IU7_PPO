package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.File
import com.distributed_messenger.domain.irepositories.IFileRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant
import java.util.UUID

class MongoFileRepository(private val collection: MongoCollection<Document>) : IFileRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoFileRepository"
    )

    override suspend fun getFile(id: UUID): File? =
        loggingWrapper {
            collection.find(Filters.eq("_id", id))
                .firstOrNull()
                ?.toFile()
        }

    override suspend fun getAllFiles(): List<File> =
        loggingWrapper {
            collection.find()
                .toList()
                .mapNotNull { it.toFile() }
        }

    override suspend fun getFilesByUser(userId: UUID): List<File> =
        loggingWrapper {
            collection.find(Filters.eq("uploadedBy", userId))
                .toList()
                .mapNotNull { it.toFile() }
        }

    override suspend fun addFile(file: File): UUID =
        loggingWrapper {
            collection.insertOne(file.toDocument())
            file.id
        }

    override suspend fun updateFile(file: File): Boolean =
        loggingWrapper {
            val result = collection.updateOne(
                Filters.eq("_id", file.id),
                file.toUpdateDocument()
            )
            result.modifiedCount == 1L
        }

    override suspend fun deleteFile(id: UUID): Boolean =
        loggingWrapper {
            val result = collection.deleteOne(Filters.eq("_id", id))
            result.deletedCount == 1L
        }

    private fun File.toDocument(): Document {
        return Document().apply {
            put("_id", id)
            put("name", name)
            put("type", type)
            put("path", path)
            put("uploadedBy", uploadedBy.toString())
            put("uploadedTimestamp", uploadedTimestamp.toEpochMilli())
        }
    }

    private fun Document.toFile(): File? {
        return try {
            File(
                id = getUUID("_id"),
                name = getString("name"),
                type = getStringOrNull("type"),
                path = getString("path"),
                uploadedBy = UUID.fromString(getString("uploadedBy")),
                uploadedTimestamp = Instant.ofEpochMilli(getLong("uploadedTimestamp"))
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun File.toUpdateDocument(): Bson {
        return com.mongodb.client.model.Updates.combine(
            com.mongodb.client.model.Updates.set("name", name),
            com.mongodb.client.model.Updates.set("type", type),
            com.mongodb.client.model.Updates.set("path", path),
            com.mongodb.client.model.Updates.set("uploadedBy", uploadedBy.toString()),
            com.mongodb.client.model.Updates.set("uploadedTimestamp", uploadedTimestamp.toEpochMilli())
        )
    }

    private fun Document.getUUID(key: String): UUID = UUID.fromString(getString(key))
    private fun Document.getStringOrNull(key: String): String? = get(key)?.toString()
}