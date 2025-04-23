package com.distributed_messenger.data.local.repositories

import com.distributed_messenger.core.User
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.core.logging.LoggingWrapper
import com.distributed_messenger.domain.irepositories.IUserRepository
import com.distributed_messenger.data.local.dao.UserDao
import com.distributed_messenger.data.local.entities.UserEntity
import java.util.UUID

class UserRepository(private val userDao: UserDao) : IUserRepository {
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "UserRepository"
    )

    override suspend fun getUser(id: UUID): User? =
        loggingWrapper {
            userDao.getUserById(id)?.toDomain()
        }

    override suspend fun getAllUsers(): List<User> =
        loggingWrapper {
            userDao.getAllUsers().map { it.toDomain() }
        }

    override suspend fun findByUsername(username: String): User? =
        loggingWrapper {
            userDao.findByUsername(username)?.toDomain()
        }

    override suspend fun addUser(user: User): UUID =
        loggingWrapper {
            val rowId = userDao.insertUser(user.toEntity())
            if (rowId == -1L) {
                throw Exception("Failed to insert user")
            }
            user.id
        }

    override suspend fun updateUser(user: User): Boolean =
        loggingWrapper {
            userDao.updateUser(user.toEntity()) > 0
        }

    override suspend fun deleteUser(id: UUID): Boolean =
        loggingWrapper {
            userDao.deleteUser(id) > 0
        }


    private fun User.toEntity(): UserEntity {
        return UserEntity(
            userId = id,
            username = username,
            role = role,
            blockedUsersId = blockedUsersId,
            profileSettingsId = profileSettingsId,
            appSettingsId = appSettingsId
        )
    }

    private fun UserEntity.toDomain(): User {
        return User(
            id = userId,
            username = username,
            role = role,
            blockedUsersId = blockedUsersId,
            profileSettingsId = profileSettingsId,
            appSettingsId = appSettingsId
        )
    }
}