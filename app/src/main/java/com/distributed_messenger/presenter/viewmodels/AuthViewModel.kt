package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.domain.controllers.UserController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//class AuthViewModel : ViewModel() {
//    private val _isAuthenticated = MutableStateFlow(false)
//    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
//
//    private val _errorMessage = MutableStateFlow("")
//    val errorMessage: StateFlow<String> = _errorMessage
//
//    fun login(username: String, password: String) {
//        viewModelScope.launch {
//            // Здесь должна быть реальная логика аутентификации
//            if (username == "admin" && password == "123") {
//                _isAuthenticated.value = true
//                _errorMessage.value = ""
//            } else {
//                _errorMessage.value = "Неверные учетные данные"
//            }
//        }
//    }
//}

class AuthViewModel(
    private val userController: UserController
) : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun temporaryLogin(username: String) {
        viewModelScope.launch {
            try {
                // Используем регистрацию как вход
                val userId = userController.register(username, UserRole.USER)
                _isAuthenticated.value = true
                _errorMessage.value = "Вход выполнен как новый пользователь: $userId"
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
            }
        }
    }
}