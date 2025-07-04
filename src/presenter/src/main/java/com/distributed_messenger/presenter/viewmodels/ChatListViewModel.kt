package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.Chat
import com.distributed_messenger.core.Message
import com.distributed_messenger.domain.iservices.IChatService
import com.distributed_messenger.domain.iservices.IMessageService
import com.distributed_messenger.domain.iservices.IUserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatListViewModel(private val chatService: IChatService,
                        private val messageService: IMessageService,
                        private val userService: IUserService) : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _lastMessages = MutableStateFlow<Map<UUID, Message?>>(emptyMap())
    val lastMessages: StateFlow<Map<UUID, Message?>> = _lastMessages

    private val _displayNames = MutableStateFlow<Map<UUID, String?>>(emptyMap())
    val displayNames: StateFlow<Map<UUID, String?>> = _displayNames


    init {
        loadChats()
    }
    fun refresh() {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            val userId = SessionManager.currentUserId
            val chats = chatService.getUserChats(userId)

//            _chats.value = chatService.getUserChats(userId)
//
//
//            chats.collect { chatList ->
//                val lastMessagesMap = mutableMapOf<UUID, Message?>()
//                for (chat in chatList) {
//                    lastMessagesMap[chat.id] = messageService.getLastMessage(chat.id)
//                }
//                _lastMessages.value = lastMessagesMap
//            }
            // Загрузка последних сообщений
            val lastMessagesMap = mutableMapOf<UUID, Message?>()
            for (chat in chats) {
                lastMessagesMap[chat.id] = messageService.getLastMessage(chat.id)
            }

            // Сортировка чатов
            val sortedChats = chats.sortedByDescending { chat ->
                lastMessagesMap[chat.id]?.timestamp?.toEpochMilli() ?: Long.MIN_VALUE
            }

            // Загрузка имен
            val namesMap = chats.associate { chat ->
                chat.id to deriveChatName(chat)
            }

            _chats.value = sortedChats
            _lastMessages.value = lastMessagesMap
            _displayNames.value = namesMap
        }

//        val lastMessagesMap = mutableMapOf<UUID, Message?>()
//        for (chat in chats) {
//            viewModelScope.launch {
//                lastMessagesMap[chat.id] = messageService.getLastMessage(chat.id)
//            }
//        }
//        _lastMessages.value = lastMessagesMap
    }

//    private suspend fun deriveChatName(chat: Chat): String {
//        return if (chat.isGroupChat) {
//            chat.name
//        } else {
//            val targetId = when (SessionManager.currentUserId) {
//                chat.creatorId -> chat.companionId
//                else -> chat.creatorId
//            }
//            targetId?.let { userService.getUser(it)?.name } ?: "Unknown User"
//        }
//    }
    private suspend fun deriveChatName(chat: Chat): String? {
        return if (chat.isGroupChat) {
            chat.name
        } else {
            val targetId = when (SessionManager.currentUserId) {
                chat.creatorId -> chat.companionId
                else -> chat.creatorId
            }
            targetId?.let { userId ->
                userService.getUser(userId)?.username
            }
        }
    }
}