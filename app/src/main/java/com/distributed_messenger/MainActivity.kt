package com.distributed_messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.distributed_messenger.ui.screens.AuthScreen
import com.distributed_messenger.ui.screens.MainScreen
import com.distributed_messenger.ui.theme.Distributed_messengerTheme
import com.distributed_messenger.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Distributed_messengerTheme {
                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

                if (isAuthenticated) {
                    MainScreen()
                } else {
                    AuthScreen(
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}