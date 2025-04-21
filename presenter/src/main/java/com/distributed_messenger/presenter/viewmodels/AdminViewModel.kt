package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.User
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.domain.iservices.IBlockService
import com.distributed_messenger.domain.iservices.IUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AdminViewModel(private val authViewModel: AuthViewModel,
                     private val userService: IUserService,
                     private val blockService: IBlockService) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _state = MutableStateFlow<AdminState>(AdminState.Idle)
    val state: StateFlow<AdminState> = _state

    suspend fun loadUsers() {
        _state.value = AdminState.Loading
        try {
            _users.value = userService.getAllUsers()
            _state.value = AdminState.Idle
        } catch (e: Exception) {
            _state.value = AdminState.Error(e.message ?: "Failed to load users")
        }
    }

    fun updateUserRole(userId: UUID, newRole: UserRole) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val success = userService.updateUserRole(userId, newRole)
                if (success) {
                    // Обновляем локальный список пользователей
                    _users.value = _users.value.map { user ->
                        if (user.id == userId) user.copy(role = newRole) else user
                    }
                    _state.value = AdminState.Success("Role updated successfully")
                } else {
                    _state.value = AdminState.Error("Failed to update user role")
                }
            } catch (e: Exception) {
                _state.value = AdminState.Error(
                    e.message ?: "Unknown error occurred while updating role"
                )
            }
        }
    }
//    fun blockUser(blockedUserId: UUID, reason: String? = null) {
//        viewModelScope.launch {
//            _state.value = AdminState.Loading
//            try {
//                val blockerId = authViewModel.getCurrentUserId()
//                val blockId = blockService.blockUser(blockerId, blockedUserId, reason)
//                _state.value = AdminState.Success("User blocked successfully")
//            } catch (e: Exception) {
//                _state.value = AdminState.Error(e.message ?: "Failed to block user")
//            }
//        }
//    }

    fun blockUser(userId: UUID, reason: String? = null) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val currentUserId = authViewModel.getCurrentUserId()
                val blockId = blockService.blockUser(currentUserId, userId, reason)
                _users.value = _users.value.map { user ->
                    if (user.id == userId) user.copy(blockedUsersId = currentUserId) else user
                }
                _state.value = AdminState.Success("User blocked")
            } catch (e: Exception) {
                _state.value = AdminState.Error(e.message ?: "Blocking failed")
            }
        }
    }
//    fun unblockUser(blockId: UUID) {
//        viewModelScope.launch {
//            _state.value = AdminState.Loading
//            try {
//                val success = blockService.unblockUser(blockId)
//                if (success) {
//                    _state.value = AdminState.Success("User unblocked successfully")
//                } else {
//                    _state.value = AdminState.Error("Failed to unblock user")
//                }
//            } catch (e: Exception) {
//                _state.value = AdminState.Error(e.message ?: "Failed to unblock user")
//            }
//        }
//    }
    fun unblockUser(blockedUserId: UUID) {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            try {
                val currentUserId = authViewModel.getCurrentUserId()
                val success = blockService.unblockUser(currentUserId, blockedUserId)
                if (success) {
                    // Обновляем список пользователей
                    loadUsers()
                    // _users.value = _users.value.map { user ->
                    //    if (user.id == userId) user.copy(blockedUsersId = currentUserId) else user
                    // }
                    _state.value = AdminState.Success("User unblocked")
                }
            } catch (e: Exception) {
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