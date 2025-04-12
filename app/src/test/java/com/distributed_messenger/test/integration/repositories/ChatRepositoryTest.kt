package com.distributed_messenger.test.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.dao.ChatDao
import com.distributed_messenger.data.local.entities.ChatEntity
import com.distributed_messenger.implementation.repositories.ChatRepository
import com.distributed_messenger.domain.models.Chat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ChatRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var chatDao: ChatDao
    private lateinit var repository: ChatRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        chatDao = db.chatDao()
        repository = ChatRepository(chatDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `add and get chat`() = runTest {
        // Arrange
        val testChat = Chat(0, "Test", 1)

        // Act
        val id = repository.addChat(testChat)
        val result = repository.getChat(id)

        // Assert
        assertEquals(testChat.copy(id = id), result)
    }

    @Test
    fun `get chats by user`() = runTest {
        // Arrange
        val chats = listOf(
            ChatEntity(0, "Chat1", 1),
            ChatEntity(0, "Chat2", 1)
        )
        chats.forEach { chatDao.insertChat(it) }

        // Act
        val result = repository.getChatsByUser(1)

        // Assert
        assertEquals(2, result.size)
    }
}