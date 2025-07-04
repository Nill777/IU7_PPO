package com.distributed_messenger.unit.services

import com.distributed_messenger.core.Chat
import com.distributed_messenger.domain.irepositories.IChatRepository
import com.distributed_messenger.domain.services.ChatService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        val chatId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        coEvery { mockChatRepository.addChat(any()) } returns chatId

        val result = chatService.createChat("Test", creatorId, isGroupChat = false, companionId = null)

        assertEquals(chatId, result)
        coVerify { mockChatRepository.addChat(match {
            it.name == "Test" &&
                    it.creatorId == creatorId &&
                    !it.isGroupChat &&
                    it.companionId == null
        }) }
    }

    @Test
    fun `getChat should return chat when exists`() = runTest {
        val chatId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        val testChat = Chat(chatId, "Test", creatorId, companionId = null, isGroupChat = false)
        coEvery { mockChatRepository.getChat(chatId) } returns testChat

        val result = chatService.getChat(chatId)

        assertEquals(testChat, result)
    }

    @Test
    fun `getUserChats should return chats for user`() = runTest {
        val userId = UUID.randomUUID()
        val testChats = listOf(
            Chat(UUID.randomUUID(), "Chat1", userId, companionId = null, isGroupChat = false),
            Chat(UUID.randomUUID(), "Chat2", userId, companionId = null, isGroupChat = true)
        )
        coEvery { mockChatRepository.getChatsByUser(userId) } returns testChats

        val result = chatService.getUserChats(userId)

        assertEquals(testChats, result)
    }

    @Test
    fun `updateChat should return true when successful`() = runTest {
        val chatId = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        val original = Chat(chatId, "Old", creatorId, companionId = null, isGroupChat = false)
        coEvery { mockChatRepository.getChat(chatId) } returns original
        coEvery { mockChatRepository.updateChat(any()) } returns true

        val result = chatService.updateChat(chatId, "New")

        assertTrue(result)
        coVerify { mockChatRepository.updateChat(match {
            it.id == chatId && it.name == "New"
        }) }
    }

    @Test
    fun `updateChat should return false when chat not found`() = runTest {
        val chatId = UUID.randomUUID()
        coEvery { mockChatRepository.getChat(chatId) } returns null

        val result = chatService.updateChat(chatId, "New")

        assertFalse(result)
        coVerify(exactly = 0) { mockChatRepository.updateChat(any()) }
    }

    @Test
    fun `deleteChat should return true when deleted`() = runTest {
        val chatId = UUID.randomUUID()
        coEvery { mockChatRepository.deleteChat(chatId) } returns true

        val result = chatService.deleteChat(chatId)

        assertTrue(result)
    }
}