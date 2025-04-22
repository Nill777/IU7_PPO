package com.distributed_messenger.domain.services

import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.core.logging.LogLevel
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.domain.iservices.IUserService
import com.distributed_messenger.domain.irepositories.IUserRepository
import java.util.UUID

class UserService(private val userRepository: IUserRepository,
                  private val logger: Logger
) : IUserService {
    override suspend fun register(username: String, role: UserRole): UUID {
        logger.log("UserService", "Registering user: $username", LogLevel.INFO)
        return try {
            val user = User(
                id = UUID.randomUUID(),
                username = username,
                role = role,
                blockedUsersId = null,
                profileSettingsId = UUID.randomUUID(),
                appSettingsId = UUID.randomUUID()
            )
            val userId = userRepository.addUser(user)
            logger.log("UserService", "User registered: $userId", LogLevel.INFO)
            userId
        } catch (e: Exception) {
            logger.log("UserService", "Registration failed: ${e.message}", LogLevel.ERROR, e)
            throw e
        }
    }
//    override suspend fun register(username: String, role: UserRole): UUID {
//        val user = User(
//            id = UUID.randomUUID(),
//            username = username,
//            role = role,
//            blockedUsersId = null,
//            profileSettingsId = UUID.randomUUID(),
//            appSettingsId = UUID.randomUUID()
//        )
//        return userRepository.addUser(user)
//    }

    override suspend fun login(username: String): UUID? {
        return userRepository.findByUsername(username)?.id
    }

    override suspend fun getUser(id: UUID): User? {
        return userRepository.getUser(id)
    }

    override suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    override suspend fun updateUser(id: UUID, username: String): Boolean {
        val user = userRepository.getUser(id) ?: return false
        val updatedUser = user.copy(id = id, username = username)
        return userRepository.updateUser(updatedUser)
    }

    override suspend fun updateUserRole(id: UUID, newRole: UserRole): Boolean {
        val user = userRepository.getUser(id) ?: return false
        val updatedUser = user.copy(id = id, role = newRole)
        return userRepository.updateUser(updatedUser)
    }

    override suspend fun deleteUser(id: UUID): Boolean {
        return userRepository.deleteUser(id)
    }
}