package com.distributed_messenger.domain.repositories

import com.distributed_messenger.domain.models.User
import java.util.UUID

interface IUserRepository {
    suspend fun getUser(id: UUID): User?
    suspend fun getAllUsers(): List<User>
    suspend fun addUser(user: User): UUID
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(id: UUID): Boolean
}
