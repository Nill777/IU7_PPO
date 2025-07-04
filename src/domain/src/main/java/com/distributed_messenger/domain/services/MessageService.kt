package com.distributed_messenger.domain.services

import com.distributed_messenger.core.Message
import com.distributed_messenger.core.MessageHistory
import com.distributed_messenger.domain.irepositories.IMessageHistoryRepository
import com.distributed_messenger.logger.Logger
import com.distributed_messenger.logger.LoggingWrapper
import com.distributed_messenger.domain.iservices.IMessageService
import com.distributed_messenger.domain.irepositories.IMessageRepository
import java.util.UUID
import java.time.Instant

class MessageService(private val messageRepository: IMessageRepository,
                     private val messageHistoryRepository: IMessageHistoryRepository) : IMessageService {
    private val loggingWrapper = LoggingWrapper(
    origin = this,
    logger = Logger,
    tag = "MessageService"
)
    override suspend fun sendMessage(senderId: UUID, chatId: UUID, content: String, fileId: UUID?): UUID =
        loggingWrapper {
            val message = Message(
                id = UUID.randomUUID(),
                senderId = senderId,
                chatId = chatId,
                content = content,
                timestamp = Instant.now(),
                fileId = fileId
            )
            messageRepository.addMessage(message)
        }

    override suspend fun getMessage(id: UUID): Message? =
        loggingWrapper {
            messageRepository.getMessage(id)
        }

    override suspend fun getChatMessages(chatId: UUID): List<Message> =
        loggingWrapper {
            messageRepository.getMessagesByChat(chatId)
        }

    override suspend fun getLastMessage(chatId: UUID): Message? =
        loggingWrapper {
            messageRepository.getLastMessageByChat(chatId)
        }

    override suspend fun editMessage(id: UUID, newContent: String): Boolean =
        loggingWrapper {
            val message = messageRepository.getMessage(id) ?: return@loggingWrapper false

            // Сохранить старую версию в историю
            messageHistoryRepository.addMessageHistory(
                MessageHistory(
                    historyId = UUID.randomUUID(),
                    messageId = id,
                    editedContent = message.content,
                    editTimestamp = Instant.now()
                )
            )

            val updatedMessage = message.copy(id = id, content = newContent)
            messageRepository.updateMessage(updatedMessage)
        }

    override suspend fun getMessageHistory(messageId: UUID): List<MessageHistory> =
        loggingWrapper {
            messageHistoryRepository.getHistoryForMessage(messageId)
        }

    override suspend fun deleteMessage(id: UUID): Boolean =
        loggingWrapper {
            messageRepository.deleteMessage(id)
        }
}
