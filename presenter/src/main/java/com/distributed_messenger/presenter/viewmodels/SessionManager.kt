package com.distributed_messenger.presenter.viewmodels

import com.distributed_messenger.core.UserRole
import java.util.UUID

object SessionManager {
    // UUID? для адекватного logout
    private var _currentUserId: UUID? = null
    private var _currentUserName: String = "Shadow"
    private var _currentUserRole: UserRole = UserRole.UNAUTHORIZED_USER

    val currentUserId: UUID
        get() = _currentUserId ?: throw IllegalStateException("User not logged in")

    val currentUserName: String
        get() = _currentUserName

    val currentUserRole: UserRole
        get() = _currentUserRole

    fun login(userId: UUID, userName: String, role: UserRole) {
        _currentUserId = userId
        _currentUserName = userName
        _currentUserRole = role
    }

    fun updateUserName(newName: String) {
        _currentUserName = newName
    }

    fun logout() {
        _currentUserId = null
        _currentUserName = "Shadow"
        _currentUserRole = UserRole.UNAUTHORIZED_USER
    }
}