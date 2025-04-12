package com.distributed_messenger.implementation.services

import com.distributed_messenger.domain.models.User
import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.domain.services.IUserService
import com.distributed_messenger.domain.repositories.IUserRepository
import java.util.UUID

class UserService(private val userRepository: IUserRepository) : IUserService {
    override suspend fun register(username: String, role: UserRole): UUID {
        val user = User(
            id = UUID.randomUUID(),
            username = username,
            role = role
        )
        return userRepository.addUser(user)
    }

    override suspend fun getUser(id: UUID): User? {
        return userRepository.getUser(id)
    }

    override suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    override suspend fun updateUser(id: UUID, username: String): Boolean {
        val user = userRepository.getUser(id) ?: return false
        val updatedUser = user.copy(username = username)
        return userRepository.updateUser(id, updatedUser)
    }

    override suspend fun deleteUser(id: UUID): Boolean {
        return userRepository.deleteUser(id)
    }
}
