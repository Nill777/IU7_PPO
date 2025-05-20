package com.distributed_messenger.data.local.mongoDB.repositories

import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.domain.irepositories.IUserRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonBinary
import org.bson.BsonString
import org.bson.UuidRepresentation
import org.bson.conversions.Bson
import java.util.UUID

class MongoUserRepository(private val collection: MongoCollection<org.bson.Document>) : IUserRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "MongoUserRepository"
    )

    override suspend fun getUser(id: UUID): User? =
        loggingWrapper {
            val document = collection.find(Filters.eq("_id", id.toString())).firstOrNull()
            document?.toUser()
        }

    override suspend fun getAllUsers(): List<User> =
        loggingWrapper {
            collection.find().toList().mapNotNull { it.toUser() }
        }

    override suspend fun findByUsername(username: String): User? =
        loggingWrapper {
            val document = collection.find(Filters.eq("username", username)).firstOrNull()
            document?.toUser()
        }

    override suspend fun addUser(user: User): UUID =
        loggingWrapper {
            val document = user.toDocument()
            collection.insertOne(document)
            user.id
        }

    override suspend fun updateUser(user: User): Boolean =
        loggingWrapper {
            val result = collection.updateOne(
                Filters.eq("_id", user.id.toString()),
                user.toUpdateDocument()
            )
            result.modifiedCount == 1L
        }

    override suspend fun deleteUser(id: UUID): Boolean =
        loggingWrapper {
            val result = collection.deleteOne(Filters.eq("_id", id.toString()))
            result.deletedCount == 1L
        }

    private fun User.toDocument(): org.bson.Document {
        return org.bson.Document().apply {
            put("_id", id.toString()) // UUID -> String
            put("username", username)
            put("role", role.name)
            put("blockedUsersId", blockedUsersId?.toString())
            put("profileSettingsId", profileSettingsId.toString())
            put("appSettingsId", appSettingsId.toString())
        }
    }

    private fun org.bson.Document.toUser(): User? {
        return try {
            User(
                id = UUID.fromString(getString("_id")), // String -> UUID
                username = getString("username"),
                role = UserRole.valueOf(getString("role")),
                blockedUsersId = getString("blockedUsersId")?.let { UUID.fromString(it) },
                profileSettingsId = UUID.fromString(getString("profileSettingsId")),
                appSettingsId = UUID.fromString(getString("appSettingsId"))
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun User.toUpdateDocument(): Bson {
        return Updates.combine(
            Updates.set("username", username),
            Updates.set("role", role.name),
            Updates.set("blockedUsersId", blockedUsersId?.toString()),
            Updates.set("profileSettingsId", profileSettingsId.toString()),
            Updates.set("appSettingsId", appSettingsId.toString())
        )
    }
}