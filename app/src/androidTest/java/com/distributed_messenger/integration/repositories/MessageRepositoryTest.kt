package com.distributed_messenger.integration.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.domain.models.Message
import com.distributed_messenger.implementation.repositories.MessageRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.UUID

/*
Положительные тесты:
    Добавление сообщения и проверка его наличия в БД.
    Обновление содержимого сообщения.
    Удаление сообщения и проверка его отсутствия.
    Получение всех сообщений.
    Фильтрация сообщений по ID чата.
Отрицательные тесты:
    Получение/обновление/удаление несуществующего сообщения.
    Обработка конфликтов при добавлении дубликатов.
*/

class MessageRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: MessageRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = MessageRepository(database.messageDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Положительные тесты

    @Test
    fun addMessage_should_insert_message_and_return_id() = runTest {
        val message = createTestMessage()

        val insertedId = repository.addMessage(message)
        val fetchedMessage = repository.getMessage(insertedId)

        assertEquals(message.id, insertedId)
        assertEquals(message, fetchedMessage)
    }

    @Test
    fun updateMessage_should_modify_existing_message() = runTest {
        val message = createTestMessage().apply { repository.addMessage(this) }
        val updatedMessage = message.copy(content = "Updated content")

        val isUpdated = repository.updateMessage(updatedMessage)
        val fetchedMessage = repository.getMessage(message.id)

        assertTrue(isUpdated)
        assertEquals("Updated content", fetchedMessage?.content)
    }

    @Test
    fun deleteMessage_should_remove_message_from_db() = runTest {
        val message = createTestMessage().apply { repository.addMessage(this) }

        val isDeleted = repository.deleteMessage(message.id)
        val fetchedMessage = repository.getMessage(message.id)

        assertTrue(isDeleted)
        assertNull(fetchedMessage)
    }

    @Test
    fun getAllMessages_should_return_all_inserted_messages() = runTest {
        val messages = listOf(createTestMessage(), createTestMessage())
        messages.forEach { repository.addMessage(it) }

        val result = repository.getAllMessages()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(messages))
    }

    @Test
    fun getMessagesByChat_should_filter_by_chatId() = runTest {
        val chatId = UUID.randomUUID()
        val message1 = createTestMessage(chatId = chatId)
        val message2 = createTestMessage(chatId = chatId)
        repository.addMessage(message1)
        repository.addMessage(message2)

        val result = repository.getMessagesByChat(chatId)

        assertEquals(2, result.size)
    }

    // Отрицательные тесты

    @Test
    fun getMessage_should_return_null_for_non_existent_id() = runTest {
        val result = repository.getMessage(UUID.randomUUID())

        assertNull(result)
    }

    @Test
    fun updateMessage_should_return_false_for_non_existent_message() = runTest {
        val isUpdated = repository.updateMessage(createTestMessage())

        assertFalse(isUpdated)
    }

    @Test
    fun deleteMessage_should_return_false_for_non_existent_id() = runTest {
        val isDeleted = repository.deleteMessage(UUID.randomUUID())

        assertFalse(isDeleted)
    }

    @Test
    fun addMessage_should_throw_exception_on_conflict() = runTest {
        val message = createTestMessage()
        repository.addMessage(message)

        assertThrows(Exception::class.java) {
            runTest { repository.addMessage(message) }
        }
    }

    private fun createTestMessage(
        id: UUID = UUID.randomUUID(),
        senderId: UUID = UUID.randomUUID(),
        chatId: UUID = UUID.randomUUID(),
        content: String = "Hello",
        fileId: UUID? = null,
        timestamp: Instant = Instant.now()
    ): Message {
        return Message(
            id = id,
            senderId = senderId,
            chatId = chatId,
            content = content,
            fileId = fileId,
            timestamp = timestamp
        )
    }
}
