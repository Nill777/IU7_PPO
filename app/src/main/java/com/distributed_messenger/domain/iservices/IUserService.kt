package com.distributed_messenger.domain.iservices

import com.distributed_messenger.domain.models.User
import com.distributed_messenger.domain.models.UserRole
import java.util.UUID

interface IUserService {
    suspend fun register(username: String, role: UserRole): UUID
    suspend fun getUser(id: UUID): User?
    suspend fun getAllUsers(): List<User>
    suspend fun updateUser(id: UUID, username: String): Boolean
    suspend fun deleteUser(id: UUID): Boolean
}
