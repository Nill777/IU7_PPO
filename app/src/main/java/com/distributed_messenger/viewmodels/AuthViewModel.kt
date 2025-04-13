package com.distributed_messenger.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun login(username: String, password: String) {
        viewModelScope.launch {
            // Здесь должна быть реальная логика аутентификации
            if (username == "admin" && password == "123") {
                _isAuthenticated.value = true
                _errorMessage.value = ""
            } else {
                _errorMessage.value = "Неверные учетные данные"
            }
        }
    }
}