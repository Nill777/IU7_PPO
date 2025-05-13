package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.Message
import com.distributed_messenger.domain.iservices.IMessageService
import com.distributed_messenger.domain.iservices.IChatService
import com.distributed_messenger.domain.iservices.IUserService
import com.distributed_messenger.core.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(private val messageService: IMessageService,
                    private val chatService: IChatService,
                    private val chatId: UUID) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _chatInfo = MutableStateFlow<Chat?>(null)
    val chatInfo: StateFlow<Chat?> = _chatInfo

    private val _deleteStatus = MutableStateFlow<Boolean?>(null)
    val deleteStatus: StateFlow<Boolean?> = _deleteStatus

    private val _editingMessage = MutableStateFlow<Message?>(null)
    val editingMessage: StateFlow<Message?> = _editingMessage

//    private val _companionName = MutableStateFlow<String?>(null)
//    val companionName: StateFlow<String?> = _companionName

    init {
        loadMessages()
        loadChatInfo()
//        loadCompanionData()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _messages.value = messageService.getChatMessages(chatId)
        }
    }
    private fun loadChatInfo() {
        viewModelScope.launch {
            _chatInfo.value = chatService.getChat(chatId)
        }
    }
//    private fun loadCompanionData() {
//        viewModelScope.launch {
//            _chatInfo.value?.let { chat ->
//                if (!chat.isGroupChat) {
//                    chat.companionId?.let { companionId ->
//                        _companionName.value = userService.getUser(companionId)?.username
//                    }
//                }
//            }
//        }
//    }

    fun sendMessage(content: String, fileId: UUID? = null) {
        viewModelScope.launch {
            val senderId = SessionManager.currentUserId
            messageService.sendMessage(
                senderId = senderId,
                chatId = chatId,
                content = content,
                fileId = fileId
            )
            loadMessages()
        }
    }

    fun deleteMessage(messageId: UUID) {
        viewModelScope.launch {
            val isDeleted = messageService.deleteMessage(messageId)
            if (isDeleted) {
                loadMessages()
            }
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            _deleteStatus.value = chatService.deleteChat(chatId)
        }
    }

    fun clearDeleteStatus() {
        _deleteStatus.value = null
    }

    fun startEditing(message: Message) {
        _editingMessage.value = message
    }

    fun editMessage(messageId: UUID, newContent: String) {
        viewModelScope.launch {
            val isEdited = messageService.editMessage(messageId, newContent)
            if (isEdited) {
                loadMessages()
            }
        }
    }

    fun cancelEditing() {
        _editingMessage.value = null
    }
}