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
        val result = messageService.sendMessage(senderId, chatId, "Hello")

        // Assert
        assertEquals(messageId, result)
        coVerify { mockMessageRepository.addMessage(match {
            it.senderId == senderId && it.chatId == chatId && it.content == "Hello"
        }) }
    }

    @Test
    fun `getMessage should return message when exists`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID()
        val senderId = UUID.randomUUID()
        val chatId = UUID.randomUUID()
        val testMessage = Message(messageId, senderId, chatId, "Hello", Instant.now(), null)
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
        coEvery { mockMessageRepository.getMessage(UUID.randomUUID()) } returns null

        // Act
        val result = messageService.getMessage(UUID.randomUUID())

        // Assert
        assertNull(result)
    }

    @Test
    fun `getChatMessages should return messages for chat`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        val testMessages = listOf(
            Message(UUID.randomUUID(), UUID.randomUUID(), chatId, "Hello", Instant.now(), null),
            Message(UUID.randomUUID(), UUID.randomUUID(), chatId, "Hi", Instant.now(), null)
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
        val original = Message(messageId, UUID.randomUUID(), UUID.randomUUID(), "Old", Instant.now(), null)
        coEvery { mockMessageRepository.getMessage(messageId) } returns original
        coEvery { mockMessageRepository.updateMessage(messageId, any()) } returns true

        // Act
        val result = messageService.editMessage(messageId, "New")

        // Assert
        assertTrue(result)
        coVerify {
            mockMessageRepository.updateMessage(messageId, match {
                it.id == messageId && it.content == "New"
            })
        }
    }

    @Test
    fun `editMessage should return false when message not found`() = runTest {
        // Arrange
        coEvery { mockMessageRepository.getMessage(UUID.randomUUID()) } returns null

        // Act
        val result = messageService.editMessage(UUID.randomUUID(), "New")

        // Assert
        assertTrue(!result)
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
    }
}
