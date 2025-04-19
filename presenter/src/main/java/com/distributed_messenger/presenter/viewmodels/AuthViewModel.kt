package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.UserRole
import com.distributed_messenger.domain.iservices.IUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(private val iUserService: IUserService) : ViewModel() {
    // Состояния UI
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Регистрация пользователя
    fun register(username: String, role: UserRole) {
        if (username.isBlank()) {
            _authState.value = AuthState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = iUserService.register(username, role)
                _authState.value = AuthState.RegistrationSuccess(userId)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    // Авторизация пользователя
    fun login(username: String) {
        if (username.isBlank()) {
            _authState.value = AuthState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = iUserService.login(username)
                userId?.let {
                    _authState.value = AuthState.LoginSuccess(it)
                } ?: run {
                    _authState.value = AuthState.Error("User not found")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    // Сброс состояния
    fun resetState() {
        _authState.value = AuthState.Idle
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