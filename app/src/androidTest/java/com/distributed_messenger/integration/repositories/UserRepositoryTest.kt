package com.distributed_messenger.integration.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.domain.models.User
import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.implementation.repositories.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

/*
Положительные тесты:
    Добавление пользователя и проверка его наличия в БД.
    Обновление имени пользователя.
    Удаление пользователя и проверка его отсутствия.
    Получение всех пользователей.
Отрицательные тесты:
    Попытка получить/обновить/удалить несуществующего пользователя.
    Обработка конфликтов при добавлении дубликатов.
 */

class UserRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = UserRepository(database.userDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Положительные тесты

    @Test
    fun addUser_should_insert_user_and_return_id() = runTest {
        val user = createTestUser()

        val insertedId = repository.addUser(user)
        val fetchedUser = repository.getUser(insertedId)

        assertEquals(user.id, insertedId)
        assertEquals(user, fetchedUser)
    }

    @Test
    fun updateUser_should_modify_existing_user() = runTest {
        val user = createTestUser().apply { repository.addUser(this) }
        val updatedUser = user.copy(username = "NewUsername")

        val isUpdated = repository.updateUser(updatedUser)
        val fetchedUser = repository.getUser(user.id)

        assertTrue(isUpdated)
        assertEquals("NewUsername", fetchedUser?.username)
    }

    @Test
    fun deleteUser_should_remove_user_from_db() = runTest {
        val user = createTestUser().apply { repository.addUser(this) }

        val isDeleted = repository.deleteUser(user.id)
        val fetchedUser = repository.getUser(user.id)

        assertTrue(isDeleted)
        assertNull(fetchedUser)
    }

    @Test
    fun getAllUsers_should_return_all_inserted_users() = runTest {
        val users = listOf(createTestUser(), createTestUser())
        users.forEach { repository.addUser(it) }

        val result = repository.getAllUsers()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(users))
    }

    // Отрицательные тесты

    @Test
    fun getUser_should_return_null_for_non_existent_id() = runTest {
        val result = repository.getUser(UUID.randomUUID())

        assertNull(result)
    }

    @Test
    fun updateUser_should_return_false_for_non_existent_user() = runTest {
        val isUpdated = repository.updateUser(createTestUser())

        assertFalse(isUpdated)
    }

    @Test
    fun deleteUser_should_return_false_for_non_existent_id() = runTest {
        val isDeleted = repository.deleteUser(UUID.randomUUID())

        assertFalse(isDeleted)
    }

    @Test
    fun addUser_should_throw_exception_on_conflict() = runTest {
        val user = createTestUser()
        repository.addUser(user)

        assertThrows(Exception::class.java) {
            runTest { repository.addUser(user) }
        }
    }

    private fun createTestUser(
        id: UUID = UUID.randomUUID(),
        username: String = "testUser",
        role: UserRole = UserRole.USER,
        blockedUsersId: UUID? = null,
        profileSettingsId: UUID = UUID.randomUUID(),
        appSettingsId: UUID = UUID.randomUUID()
    ): User {
        return User(
            id = id,
            username = username,
            role = role,
            blockedUsersId = blockedUsersId,
            profileSettingsId = profileSettingsId,
            appSettingsId = appSettingsId
        )
    }
}
