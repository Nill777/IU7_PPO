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
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.dao.AppSettingsDao
import com.distributed_messenger.data.local.dao.BlockDao
import com.distributed_messenger.data.local.dao.ChatDao
import com.distributed_messenger.data.local.dao.FileDao
import com.distributed_messenger.data.local.dao.MessageDao
import com.distributed_messenger.data.local.dao.UserDao
import com.distributed_messenger.data.local.repositories.AppSettingsRepository
import com.distributed_messenger.data.local.repositories.BlockRepository
import com.distributed_messenger.data.local.repositories.ChatRepository
import com.distributed_messenger.data.local.repositories.FileRepository
import com.distributed_messenger.data.local.repositories.MessageRepository
import com.distributed_messenger.data.local.repositories.UserRepository
import com.distributed_messenger.domain.services.AppSettingsService
import com.distributed_messenger.domain.services.BlockService
import com.distributed_messenger.domain.services.ChatService
import com.distributed_messenger.domain.services.FileService
import com.distributed_messenger.domain.services.MessageService
import com.distributed_messenger.domain.services.UserService
import com.distributed_messenger.presenter.viewmodels.AdminViewModel
import com.distributed_messenger.presenter.viewmodels.AppSettingsViewModel
import com.distributed_messenger.presenter.viewmodels.AuthViewModel
import com.distributed_messenger.presenter.viewmodels.ChatListViewModel
import com.distributed_messenger.presenter.viewmodels.ChatViewModel
import com.distributed_messenger.presenter.viewmodels.NewChatViewModel
import com.distributed_messenger.presenter.viewmodels.ProfileViewModel
import com.distributed_messenger.ui.NavigationController
import com.distributed_messenger.ui.screens.AboutScreen
import com.distributed_messenger.ui.screens.AdminDashboardScreen
import com.distributed_messenger.ui.screens.AdminPanelScreen
import com.distributed_messenger.ui.screens.AppSettingsScreen
import com.distributed_messenger.ui.screens.AuthScreen
import com.distributed_messenger.ui.screens.BlockManagementScreen
import com.distributed_messenger.ui.screens.ChatListScreen
import com.distributed_messenger.ui.screens.ChatScreen
import com.distributed_messenger.ui.screens.MainScreen
import com.distributed_messenger.ui.screens.NewChatScreen
//import com.distributed_messenger.ui.screens.NewChatScreen
import com.distributed_messenger.ui.screens.ProfileScreen
import com.distributed_messenger.ui.screens.SettingsScreen
import com.distributed_messenger.ui.theme.DistributedMessengerTheme
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

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
//    private val sessionManager = SessionManager()
    private lateinit var navController: NavHostController

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

    // 2. Получение Dao из базы данных
    private val userDao: UserDao by lazy {
        appDatabase.userDao()
    }
    private val chatDao: ChatDao by lazy {
        appDatabase.chatDao()
    }
    private val fileDao: FileDao by lazy {
        appDatabase.fileDao()
    }
    private val messageDao: MessageDao by lazy {
        appDatabase.messageDao()
    }
    private val blockDao: BlockDao by lazy {
        appDatabase.blockDao()
    }
    private val appSettingsDao: AppSettingsDao by lazy {
        appDatabase.appSettingsDao()
    }

    // 3. Создание Repository с Dao
    private val userRepository: UserRepository by lazy {
        UserRepository(userDao)
    }
    private val chatRepository: ChatRepository by lazy {
        ChatRepository(chatDao)
    }
    private val fileRepository: FileRepository by lazy {
        FileRepository(fileDao)
    }
    private val messageRepository: MessageRepository by lazy {
        MessageRepository(messageDao)
    }
    private val blockRepository: BlockRepository by lazy {
        BlockRepository(blockDao)
    }
    private val appSettingsRepository: AppSettingsRepository by lazy {
        AppSettingsRepository(appSettingsDao)
    }

    // 4. Создание Service с Repository
    private val userService: UserService by lazy {
        UserService(userRepository)
    }
    private val chatService: ChatService by lazy {
        ChatService(chatRepository)
    }
    private val fileService: FileService by lazy {
        FileService(fileRepository)
    }
    private val messageService: MessageService by lazy {
        MessageService(messageRepository)
    }
    private val blockService: BlockService by lazy {
        BlockService(blockRepository)
    }
    private val appSettingsService: AppSettingsService by lazy {
        AppSettingsService(appSettingsRepository)
    }

    // 5. Создание ViewModel
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userService) as T
            }
        }
    }
    private val chatListViewModel: ChatListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatListViewModel(chatService, messageService, userService) as T
            }
        }
    }
    private val newChatViewModel: NewChatViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NewChatViewModel(userService, chatService) as T
            }
        }
    }
//    private val chatViewModel: ChatViewModel by viewModels {
//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return ChatViewModel(messageService, authViewModel, UUID.randomUUID()) as T
//            }
//        }
//    }
    private val profileViewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userService) as T
            }
        }
    }
    private val adminViewModel: AdminViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminViewModel(userService, blockService) as T
            }
        }
    }
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AppSettingsViewModel(appSettingsService) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация конфига перед использованием
        Config.initialize(applicationContext)

        val dir = File(applicationContext.getExternalFilesDir(null), Config.logDir).apply { mkdirs() }
        Logger.initialize(dir.absolutePath)

        setContent {
            DistributedMessengerTheme(
                appSettingsViewModel = appSettingsViewModel
            ) {
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
                    composable("chat_list") {
                        ChatListScreen(
                            viewModel = chatListViewModel,
                            authViewModel = authViewModel,
                            appSettingsViewModel = appSettingsViewModel,
                            navigationController = navigationController)
                    }
                    composable("chat/{chatId}") { backStackEntry ->
                        val chatId = UUID.fromString(backStackEntry.arguments?.getString("chatId"))
                        ChatScreen(
                            viewModel = ChatViewModel(
                                messageService = messageService,
                                chatService = chatService,
                                chatId = chatId
                            ),
//                            listViewModel = ChatListViewModel(
//                                chatService = chatService,
//                                messageService = messageService
//                            ),
                            navigationController = navigationController
                        )
                    }
                    composable("new_chat") {
                        NewChatScreen(viewModel = newChatViewModel,
                            navigationController = navigationController)
                    }
                    composable("profile") {
                        ProfileScreen(viewModel = profileViewModel,
                            navigationController = navigationController)
                    }
                    composable("settings") {
                        SettingsScreen(navigationController = navigationController)
                    }
                    composable("about_program") {
                        AboutScreen()
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
}

