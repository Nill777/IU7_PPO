package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.logger.LogLevel
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.domain.iservices.IBlockService
import com.distributed_messenger.domain.iservices.IUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AdminViewModel(private val userService: IUserService,
                     private val blockService: IBlockService) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _state = MutableStateFlow<AdminState>(AdminState.Idle)
    val state: StateFlow<AdminState> = _state

    suspend fun loadUsers() {
        Logger.log("AdminViewModel", "Loading all users")
        _state.value = AdminState.Loading
        try {
            _users.value = userService.getAllUsers()
            _state.value = AdminState.Idle
            Logger.log("AdminViewModel", "Users loaded successfully (count: ${_users.value.size})")
        } catch (e: Exception) {
            Logger.log("AdminViewModel", "Error loading users: ${e.message}", LogLevel.ERROR, e)
            _state.value = AdminState.Error(e.message ?: "Failed to load users")
        }
    }

    fun updateUserRole(userId: UUID, newRole: UserRole) {
        Logger.log("AdminViewModel", "Updating role for user $userId to $newRole")
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val success = userService.updateUserRole(userId, newRole)
                if (success) {
                    _users.value = _users.value.map { user ->
                        if (user.id == userId) user.copy(role = newRole) else user
                    }
                    _state.value = AdminState.Success("Role updated successfully")
                    Logger.log("AdminViewModel", "User role updated successfully")
                } else {
                    Logger.log("AdminViewModel", "Failed to update user role", LogLevel.WARN)
                    _state.value = AdminState.Error("Failed to update user role")
                }
            } catch (e: Exception) {
                Logger.log("AdminViewModel", "Role update error: ${e.message}", LogLevel.ERROR, e)
                _state.value = AdminState.Error(
                    e.message ?: "Unknown error occurred while updating role"
                )
            }
        }
    }

    fun blockUser(userId: UUID, reason: String? = null) {
        Logger.log("AdminViewModel", "Blocking user $userId, reason: ${reason ?: "none"}")
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val currentUserId = SessionManager.currentUserId
                val blockId = blockService.blockUser(currentUserId, userId, reason)
                _users.value = _users.value.map { user ->
                    if (user.id == userId) user.copy(blockedUsersId = currentUserId) else user
                }
                _state.value = AdminState.Success("User blocked")
                Logger.log("AdminViewModel", "User blocked successfully. Block ID: $blockId")
            } catch (e: Exception) {
                Logger.log("AdminViewModel", "Blocking error: ${e.message}", LogLevel.ERROR, e)
                _state.value = AdminState.Error(e.message ?: "Blocking failed")
            }
        }
    }

    fun unblockUser(blockedUserId: UUID) {
        Logger.log("AdminViewModel", "Unblocking user $blockedUserId")
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val currentUserId = SessionManager.currentUserId
                val success = blockService.unblockUser(currentUserId, blockedUserId)
                if (success) {
                    _users.value = _users.value.map { user ->
                        if (user.id == blockedUserId) user.copy(blockedUsersId = null) else user
                    }
                    _state.value = AdminState.Success("User unblocked")
                    Logger.log("AdminViewModel", "User unblocked successfully")
                }
            } catch (e: Exception) {
                Logger.log("AdminViewModel", "Unblocking error: ${e.message}", LogLevel.ERROR, e)
                _state.value = AdminState.Error(e.message ?: "Unblocking failed")
            }
        }
    }

        sealed class AdminState {
        object Idle : AdminState()
        object Loading : AdminState()
        data class Success(val message: String) : AdminState()
        data class Error(val message: String) : AdminState()
    }
}