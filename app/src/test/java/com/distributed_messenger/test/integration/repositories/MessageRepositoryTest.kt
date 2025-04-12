package com.distributed_messenger.test.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.dao.MessageDao
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.entities.MessageEntity
import com.distributed_messenger.implementation.repositories.MessageRepository
import com.distributed_messenger.domain.models.Message
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var messageDao: MessageDao
    private lateinit var repository: MessageRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        messageDao = db.messageDao()
        repository = MessageRepository(messageDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `add and get message`() = runTest {
        // Arrange
        val testMessage = Message(0, 1, 1, "Test", "123")

        // Act
        val id = repository.addMessage(testMessage)
        val result = repository.getMessage(id)

        // Assert
        assertEquals(testMessage.copy(id = id), result)
    }

    @Test
    fun `get non-existent message returns null`() = runTest {
        // Act
        val result = repository.getMessage(999)

        // Assert
        assertNull(result)
    }

    @Test
    fun `get messages by chat`() = runTest {
        // Arrange
        val messages = listOf(
            MessageEntity(0, 1, 1, "Msg1", "123"),
            MessageEntity(0, 2, 1, "Msg2", "124")
        )
        messages.forEach { messageDao.insertMessage(it) }

        // Act
        val result = repository.getMessagesByChat(1)

        // Assert
        assertEquals(2, result.size)
    }
}