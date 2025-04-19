package com.distributed_messenger.integration.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.core.Chat
import com.distributed_messenger.data.local.repositories.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

/*
Положительные тесты:
    Добавление чата: Проверяет, что чат корректно сохраняется в БД и возвращается его ID.
    Обновление чата: Изменяет название чата и проверяет, что изменения сохраняются.
    Удаление чата: Удаляет чат и подтверждает его отсутствие в БД.
    Получение всех чатов: Убеждается, что возвращаются все добавленные чаты.
    Фильтрация чатов по пользователю: Проверяет выборку чатов по creatorId или companionId.
Отрицательные тесты:
    Получение несуществующего чата: Возвращает null.
    Обновление/удаление несуществующего чата: Возвращает false.
    Конфликт при добавлении дубликата: Выбрасывает исключение при попытке повторно добавить тот же чат.
 */

class ChatRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: ChatRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = ChatRepository(database.chatDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Положительные тесты

    @Test
    fun addChat_should_insert_chat_and_return_id() = runTest {
        val chat = createTestChat()

        val insertedId = repository.addChat(chat)
        val fetchedChat = repository.getChat(insertedId)

        assertEquals(chat.id, insertedId)
        assertEquals(chat, fetchedChat)
    }

    @Test
    fun updateChat_should_modify_existing_chat() = runTest {
        val chat = createTestChat().apply { repository.addChat(this) }
        val updatedChat = chat.copy(name = "Updated Chat")

        val isUpdated = repository.updateChat(updatedChat)
        val fetchedChat = repository.getChat(chat.id)

        assertTrue(isUpdated)
        assertEquals("Updated Chat", fetchedChat?.name)
    }

    @Test
    fun deleteChat_should_remove_chat_from_db() = runTest {
        val chat = createTestChat().apply { repository.addChat(this) }

        val isDeleted = repository.deleteChat(chat.id)
        val fetchedChat = repository.getChat(chat.id)

        assertTrue(isDeleted)
        assertNull(fetchedChat)
    }

    @Test
    fun getAllChats_should_return_all_inserted_chats() = runTest {
        val chats = listOf(createTestChat(), createTestChat())
        chats.forEach { repository.addChat(it) }

        val result = repository.getAllChats()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(chats))
    }

    @Test
    fun getChatsByUser_should_filter_by_creator_or_companion() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        val chat1 = createTestChat(creatorId = userId)
        val chat2 = createTestChat(companionId = userId)
        repository.addChat(chat1)
        repository.addChat(chat2)

        val result = repository.getChatsByUser(userId)

        assertEquals(2, result.size)
    }

    // Отрицательные тесты

    @Test
    fun getChat_should_return_null_for_non_existent_id() = runTest {
        val result = repository.getChat(UUID.randomUUID())

        assertNull(result)
    }

    @Test
    fun updateChat_should_return_false_for_non_existent_chat() = runTest {
        val isUpdated = repository.updateChat(createTestChat())

        assertFalse(isUpdated)
    }

    @Test
    fun deleteChat_should_return_false_for_non_existent_id() = runTest {
        val isDeleted = repository.deleteChat(UUID.randomUUID())

        assertFalse(isDeleted)
    }

    @Test
    fun addChat_should_throw_exception_on_conflict() = runTest {
        val chat = createTestChat()
        repository.addChat(chat)

        assertThrows(Exception::class.java) {
            runTest { repository.addChat(chat) }
        }
    }

    private fun createTestChat(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Chat",
        creatorId: UUID = UUID.randomUUID(),
        companionId: UUID? = null,
        isGroupChat: Boolean = false
    ): Chat {
        return Chat(
            id = id,
            name = name,
            creatorId = creatorId,
            companionId = companionId,
            isGroupChat = isGroupChat
        )
    }
}
