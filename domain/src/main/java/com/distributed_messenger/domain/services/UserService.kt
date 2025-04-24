package com.distributed_messenger.domain.services

import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.logger.ILogger
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.distributed_messenger.domain.iservices.IUserService
import com.distributed_messenger.domain.irepositories.IUserRepository
import java.util.UUID

class UserService(private val userRepository: IUserRepository) : IUserService {
    // Создаём LoggingWrapper для текущего сервиса
    private val loggingWrapper = LoggingWrapper(
        origin = this,
        logger = Logger,
        tag = "UserService"
    )

    override suspend fun register(username: String, role: UserRole): UUID =
        loggingWrapper {
            val user = User(
                id = UUID.randomUUID(),
                username = username,
                role = role,
                blockedUsersId = null,
                profileSettingsId = UUID.randomUUID(),
                appSettingsId = UUID.randomUUID()
            )
            userRepository.addUser(user)
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

    override suspend fun login(username: String): UUID? =
        loggingWrapper {
            userRepository.findByUsername(username)?.id
        }

    override suspend fun getUser(id: UUID): User? =
        loggingWrapper {
            userRepository.getUser(id)
        }

    override suspend fun getAllUsers(): List<User> =
        loggingWrapper {
            userRepository.getAllUsers()
        }

    override suspend fun updateUser(id: UUID, username: String): Boolean =
        loggingWrapper {
            // return@label используется для явного указания, из какого контекста или лямбда-выражения
            val user = userRepository.getUser(id) ?: return@loggingWrapper false
            val updatedUser = user.copy(id = id, username = username)
            userRepository.updateUser(updatedUser)
        }

    override suspend fun updateUserRole(id: UUID, newRole: UserRole): Boolean =
        loggingWrapper {
            val user = userRepository.getUser(id) ?: return@loggingWrapper false
            val updatedUser = user.copy(id = id, role = newRole)
            userRepository.updateUser(updatedUser)
        }

    override suspend fun deleteUser(id: UUID): Boolean =
        loggingWrapper {
            userRepository.deleteUser(id)
        }
}