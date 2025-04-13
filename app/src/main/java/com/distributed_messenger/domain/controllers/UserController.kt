package com.distributed_messenger.domain.controllers

import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.domain.services.IUserService
import java.util.UUID

class UserController(private val userService: IUserService) {
    suspend fun register(username: String, role: UserRole): UUID {
        return userService.register(username, role)
    }

    suspend fun getUser(id: UUID) = userService.getUser(id)

    suspend fun getAllUsers() = userService.getAllUsers()

    suspend fun updateUser(id: UUID, username: String) =
        userService.updateUser(id, username)

    suspend fun deleteUser(id: UUID) =
        userService.deleteUser(id)
}