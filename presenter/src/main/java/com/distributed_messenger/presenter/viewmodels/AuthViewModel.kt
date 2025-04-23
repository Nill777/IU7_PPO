package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.core.logging.LogLevel
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.domain.iservices.IUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(private val iUserService: IUserService) : ViewModel() {
    // Состояния UI
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    // Хранение текущего пользователя
    private lateinit var currentUserId: UUID

    fun register(username: String, role: UserRole) {
        Logger.log("AuthViewModel", "Attempting registration for: $username ($role)")
        if (username.isBlank()) {
            Logger.log("AuthViewModel", "Empty username in registration", LogLevel.WARN)
            _authState.value = AuthState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = iUserService.register(username, role)
                currentUserId = userId
                _authState.value = AuthState.RegistrationSuccess(userId)
                Logger.log("AuthViewModel", "Registration successful. User ID: $userId")
            } catch (e: Exception) {
                Logger.log("AuthViewModel", "Registration failed: ${e.message}", LogLevel.ERROR, e)
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(username: String) {
        Logger.log("AuthViewModel", "Attempting login for: $username")
        if (username.isBlank()) {
            Logger.log("AuthViewModel", "Empty username in login", LogLevel.WARN)
            _authState.value = AuthState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = iUserService.login(username)
                userId?.let {
                    currentUserId = it
                    _authState.value = AuthState.LoginSuccess(it)
                    Logger.log("AuthViewModel", "Login successful. User ID: $it")
                } ?: run {
                    Logger.log("AuthViewModel", "User not found: $username", LogLevel.WARN)
                    _authState.value = AuthState.Error("User not found")
                }
            } catch (e: Exception) {
                Logger.log("AuthViewModel", "Login error: ${e.message}", LogLevel.ERROR, e)
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun getCurrentUserId(): UUID {
        if (!::currentUserId.isInitialized) {
            Logger.log("AuthViewModel", "Accessing uninitialized currentUserId", LogLevel.WARN)
        }
        return currentUserId
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        Logger.log("AuthViewModel", "Auth state reset")
    }
    
    // Модель состояний
    // "запечатанный" (ограниченный) класс-контейнер для состояний
    sealed class AuthState {
        object Idle : AuthState()   // object - синглтон (один экземпляр на всё приложение)
        object Loading : AuthState()
        data class RegistrationSuccess(val userId: UUID) : AuthState()  // класс для хранения данных
        data class LoginSuccess(val userId: UUID) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}