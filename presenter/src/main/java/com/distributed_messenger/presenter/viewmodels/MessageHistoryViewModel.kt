package com.distributed_messenger.presenter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.distributed_messenger.core.Message
import com.distributed_messenger.core.MessageHistory
import com.distributed_messenger.domain.iservices.IMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class MessageHistoryViewModel(private val messageService: IMessageService) : ViewModel() {
    private val _history = MutableStateFlow<List<MessageHistory>>(emptyList())
    val history: StateFlow<List<MessageHistory>> = _history

    private val _currentMessage = MutableStateFlow<Message?>(null)
    val currentMessage = _currentMessage.asStateFlow()

    fun loadHistory(messageId: UUID) {
        viewModelScope.launch {
            _currentMessage.value = messageService.getMessage(messageId)
            _history.value = messageService.getMessageHistory(messageId)
                .sortedByDescending { it.editTimestamp }
        }
    }
}