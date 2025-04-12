package com.distributed_messenger.test.unit.services

import com.distributed_messenger.domain.models.Message
import com.distributed_messenger.domain.repositories.IMessageRepository
import com.distributed_messenger.implementation.services.MessageService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MessageServiceTest {
    private lateinit var messageService: MessageService
    private val mockMessageRepository = mockk<IMessageRepository>()

    @BeforeEach
    fun setup() {
        messageService = MessageService(mockMessageRepository)
    }

    @Test
    fun `sendMessage should return new message id`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        val senderId = UUID.randomUUID()
        val chatId = UUID.randomUUID()
        coEvery { mockMessageRepository.addMessage(any()) } returns messageId

        // Act
        val result = messageService.sendMessage(senderId, chatId, "Hello", fileId = null)

        // Assert
        assertEquals(messageId, result)
        coVerify { mockMessageRepository.addMessage(match {
            it.senderId == senderId &&
                    it.chatId == chatId &&
                    it.content == "Hello" &&
                    it.fileId == null &&
                    it.timestamp.isBefore(Instant.now().plusSeconds(1)) // Проверка, что время в разумных пределах
        }) }
    }

    @Test
    fun `getMessage should return message when exists`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        val testMessage = Message(
            id = messageId,
            senderId = UUID.randomUUID(),
            chatId = UUID.randomUUID(),
            content = "Hello",
            fileId = null,
            timestamp = Instant.now()
        )
        coEvery { mockMessageRepository.getMessage(messageId) } returns testMessage

        // Act
        val result = messageService.getMessage(messageId)

        // Assert
        assertEquals(testMessage, result)
        coVerify { mockMessageRepository.getMessage(messageId) }
    }

    @Test
    fun `getMessage should return null when not exists`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        coEvery { mockMessageRepository.getMessage(messageId) } returns null

        // Act
        val result = messageService.getMessage(messageId)

        // Assert
        assertNull(result)
    }

    @Test
    fun `getChatMessages should return messages for chat`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        val testMessages = listOf(
            Message(
                id = UUID.randomUUID(),
                senderId = UUID.randomUUID(),
                chatId = chatId,
                content = "Hello",
                fileId = null,
                timestamp = Instant.now()
            ),
            Message(
                id = UUID.randomUUID(),
                senderId = UUID.randomUUID(),
                chatId = chatId,
                content = "Hi",
                fileId = null,
                timestamp = Instant.now()
            )
        )
        coEvery { mockMessageRepository.getMessagesByChat(chatId) } returns testMessages

        // Act
        val result = messageService.getChatMessages(chatId)

        // Assert
        assertEquals(testMessages, result)
        coVerify { mockMessageRepository.getMessagesByChat(chatId) }
    }

    @Test
    fun `editMessage should return true on success`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        val original = Message(
            id = messageId,
            senderId = UUID.randomUUID(),
            chatId = UUID.randomUUID(),
            content = "Old",
            fileId = null,
            timestamp = Instant.now()
        )
        coEvery { mockMessageRepository.getMessage(messageId) } returns original
        coEvery { mockMessageRepository.updateMessage(any()) } returns true

        // Act
        val result = messageService.editMessage(messageId, "New")

        // Assert
        assertTrue(result)
        coVerify {
            mockMessageRepository.updateMessage(match {
                it.id == messageId && it.content == "New" && it.timestamp == original.timestamp
            })
        }
    }

    @Test
    fun `editMessage should return false when message not found`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        coEvery { mockMessageRepository.getMessage(messageId) } returns null

        // Act
        val result = messageService.editMessage(messageId, "New")

        // Assert
        assertFalse(result)
        coVerify(exactly = 0) { mockMessageRepository.updateMessage(any()) }
    }

    @Test
    fun `deleteMessage should return true when deleted`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        coEvery { mockMessageRepository.deleteMessage(messageId) } returns true

        // Act
        val result = messageService.deleteMessage(messageId)

        // Assert
        assertTrue(result)
        coVerify { mockMessageRepository.deleteMessage(messageId) }
    }
}