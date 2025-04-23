package com.distributed_messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.distributed_messenger.core.logging.Logger
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.dao.AppSettingsDao
import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.dao.UserDao
import com.distributed_messenger.data.local.repositories.AppSettingsRepository
import com.distributed_messenger.data.local.repositories.BlockRepository
import com.distributed_messenger.data.local.repositories.UserRepository
import com.distributed_messenger.domain.services.AppSettingsService
import com.distributed_messenger.domain.services.BlockService
import com.distributed_messenger.domain.services.UserService
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.presenter.viewmodels.AppSettingsViewModel
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.presenter.viewmodels.ProfileViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.screens.AdminDashboardScreen
import com.distributed_messenger.ui.screens.AdminPanelScreen
import com.distributed_messenger.ui.screens.AppSettingsScreen
import com.distributed_messenger.ui.screens.AuthScreen
import com.distributed_messenger.ui.screens.BlockManagementScreen
import com.distributed_messenger.ui.screens.MainScreen
import com.distributed_messenger.ui.screens.ProfileScreen

//val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        // Создание новой таблицы
//        db.execSQL(
//            """
//            CREATE TABLE IF NOT EXISTS app_settings (
//                setting_id INTEGER PRIMARY KEY AUTOINCREMENT,
//                setting_name TEXT NOT NULL,
//                setting_value INTEGER NOT NULL
//            )
//            """
//        )
//    }
//}

class MainActivity : ComponentActivity() {
    // 1. Инициализация Room Database
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
             Config.dbName
        ) // .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration(dropAllTables = true) // Удаляет данные при изменении схемы
        .build()
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
//    private val uslogger = Logger("app.log")
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

    // 2. Получение UserDao из базы данных
    private val blockDao: BlockDao by lazy {
        appDatabase.blockDao()
    }
    // 3. Создание UserRepository с UserDao
    private val blockRepository: BlockRepository by lazy {
        BlockRepository(blockDao)
    }
    // 4. Создание UserService с UserRepository
    private val blockService: BlockService by lazy {
        BlockService(blockRepository)
    }

    // 2. Получение UserDao из базы данных
    private val appSettingsDao: AppSettingsDao by lazy {
        appDatabase.appSettingsDao()
    }
    // 3. Создание UserRepository с UserDao
    private val appSettingsRepository: AppSettingsRepository by lazy {
        AppSettingsRepository(appSettingsDao)
    }
    // 4. Создание UserService с UserRepository
    private val appSettingsService: AppSettingsService by lazy {
        AppSettingsService(appSettingsRepository)
    }

    // 6. Создание ProfileViewModel
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppSettingsViewModel(appSettingsService) as T
            }
        }
    }

    // 6. Создание ProfileViewModel
    private val profileViewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userService, authViewModel) as T
            }
        }
    }

    // 7. Создание AdminViewModel
    private val adminViewModel: AdminViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminViewModel(authViewModel, userService, blockService) as T
            }
        }
    }

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация конфига перед использованием
        Config.initialize(applicationContext)
        Logger.initialize(applicationContext)

        setContent {
            // remember гарантирует, что объект не пересоздаётся при рекомпозициях.
            navController = rememberNavController()
            val navigationController = NavigationController(navController)

            NavHost(navController, startDestination = "auth") {
                composable("auth") {
                    AuthScreen(viewModel = authViewModel,
                        navigationController = navigationController
                    )
                }
                composable("home") {
                    MainScreen(navigationController = navigationController)
                }
                composable("profile") {
                    ProfileScreen(viewModel = profileViewModel,
                        navigationController = navigationController)
                }
                composable("admin_dashboard") {
                    AdminDashboardScreen(
                        viewModel = adminViewModel,
                        navigationController = navigationController
                    )
                }
                composable("role_management") {
                    AdminPanelScreen(
                        viewModel = adminViewModel,
                        navigationController = navigationController
                    )
                }
                composable("block_management") {
                    BlockManagementScreen(
                        viewModel = adminViewModel,
                        navigationController = navigationController
                    )
                }
                composable("app_settings") {
                    AppSettingsScreen(
                        viewModel = appSettingsViewModel,
                        navigationController = navigationController
                    )
                }
            }
        }
    }
}

