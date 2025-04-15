package com.distributed_messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.distributed_messenger.domain.controllers.UserController
import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.domain.iservices.IUserService
import com.distributed_messenger.ui.screens.AuthScreen
import com.distributed_messenger.ui.screens.MainScreen
import com.distributed_messenger.ui.theme.Distributed_messengerTheme
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import java.util.UUID

class MainActivity : ComponentActivity() {
    // Инициализация зависимостей
    private val userService: IUserService by lazy { UserServiceStub() }
    private val userController: UserController by lazy { UserController(userService) }

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userController) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Distributed_messengerTheme {
                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

                if (isAuthenticated) {
                    MainScreen()
                } else {
                    AuthScreen(authViewModel = authViewModel)
                }
            }
        }
    }
}

// Заглушка сервиса для примера
class UserServiceStub : IUserService {
    private val users = mutableListOf<com.distributed_messenger.domain.models.User>()

    override suspend fun register(username: String, role: UserRole): UUID {
        val user = com.distributed_messenger.domain.models.User(
            id = UUID.randomUUID(),
            username = username,
            role = role,
            blockedUsersId = null,
            profileSettingsId = UUID.randomUUID(),
            appSettingsId = UUID.randomUUID()
        )
        users.add(user)
        return user.id
    }

    override suspend fun getUser(id: UUID) = users.find { it.id == id }
    override suspend fun getAllUsers() = users.toList()
    override suspend fun updateUser(id: UUID, username: String) = true
    override suspend fun deleteUser(id: UUID) = true
}