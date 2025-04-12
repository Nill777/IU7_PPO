package com.distributed_messenger.test.unit.services

import com.distributed_messenger.domain.models.Chat
import com.distributed_messenger.domain.repositories.IChatRepository
import com.distributed_messenger.implementation.services.ChatService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatServiceTest {
    private lateinit var chatService: ChatService
    private val mockChatRepository = mockk<IChatRepository>()

    @BeforeEach
    fun setup() {
        chatService = ChatService(mockChatRepository)
    }

    @Test
    fun `createChat should return new chat id`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        coEvery { mockChatRepository.addChat(any()) } returns chatId

        // Act
        val result = chatService.createChat("Test", creatorId)

        // Assert
        assertEquals(chatId, result)
        coVerify { mockChatRepository.addChat(match {
            it.name == "Test" && it.creatorId == creatorId && !it.isGroupChat
        }) }
    }

    @Test
    fun `getChat should return chat when exists`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        val testChat = Chat(chatId, "Test", creatorId)
        coEvery { mockChatRepository.getChat(chatId) } returns testChat

        // Act
        val result = chatService.getChat(chatId)

        // Assert
        assertEquals(testChat, result)
    }

    @Test
    fun `getUserChats should return chats for user`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        val testChats = listOf(
            Chat(UUID.randomUUID(), "Chat1", userId),
            Chat(UUID.randomUUID(), "Chat2", userId)
        )
        coEvery { mockChatRepository.getChatsByUser(userId) } returns testChats

        // Act
        val result = chatService.getUserChats(userId)

        // Assert
        assertEquals(testChats, result)
    }

    @Test
    fun `updateChat should return true when successful`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        val original = Chat(chatId, "Old", UUID.randomUUID())
        coEvery { mockChatRepository.getChat(chatId) } returns original
        coEvery { mockChatRepository.updateChat(chatId, any()) } returns true

        // Act
        val result = chatService.updateChat(chatId, "New")

        // Assert
        assertTrue(result)
        coVerify {
            mockChatRepository.updateChat(chatId, match { it.name == "New" })
        }
    }

    @Test
    fun `updateChat should return false when chat not found`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        coEvery { mockChatRepository.getChat(chatId) } returns null

        // Act
        val result = chatService.updateChat(chatId, "New")

        // Assert
        assertTrue(!result)
    }

    @Test
    fun `deleteChat should return true when deleted`() = runTest {
        // Arrange
        val chatId = UUID.randomUUID()
        coEvery { mockChatRepository.deleteChat(chatId) } returns true

        // Act
        val result = chatService.deleteChat(chatId)

        // Assert
        assertTrue(result)
    }
}
