package com.distributed_messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.dao.UserDao
import com.distributed_messenger.data.local.repositories.UserRepository
import com.distributed_messenger.domain.services.UserService
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.screens.AuthScreen
import com.distributed_messenger.ui.screens.MainScreen
import com.distributed_messenger.ui.screens.ProfileScreen

class MainActivity : ComponentActivity() {
    // 1. Инициализация Room Database
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "messenger-db"
        ).build()
    }
    // 2. Получение UserDao из базы данных
    private val userDao: UserDao by lazy {
        appDatabase.userDao()
    }
    // 3. Создание UserRepository с UserDao
    private val userRepository: UserRepository by lazy {
        UserRepository(userDao)
    }
    // 4. Создание UserService с UserRepository
    private val userService: UserService by lazy {
        UserService(userRepository)
    }

    // 5. Передача userService в AuthViewModel
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userService) as T
            }
        }
    }

//    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // remember гарантирует, что объект не пересоздаётся при рекомпозициях.
            navController = rememberNavController()
            val navigationController = NavigationController(navController)

            NavHost(navController, startDestination = "auth") {
                composable("auth") {
                    AuthScreen(
                        viewModel = authViewModel,
                        navigationController = navigationController
                    )
                }
                composable("home") {
                    MainScreen(navigationController = navigationController)
                }
                composable("profile") {
                    ProfileScreen(navigationController = navigationController)
                }
            }
        }
    }
}

