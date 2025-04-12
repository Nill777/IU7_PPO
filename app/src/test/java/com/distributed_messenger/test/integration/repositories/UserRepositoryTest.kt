package com.distributed_messenger.test.repositories

import androidx.room.Room
import androidx.core.t
import androidx.test.core.app.ApplicationProvider
import com.distributed_messenger.data.local.AppDatabase
import com.distributed_messenger.data.local.dao.UserDao
import com.distributed_messenger.data.local.entities.UserEntity
import com.distributed_messenger.implementation.repositories.UserRepository
import com.distributed_messenger.domain.models.User
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        userDao = db.userDao()
        repository = UserRepository(userDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `add and get user`() = runTest {
        // Arrange
        val testUser = User(0, "testUser", "user")

        // Act
        val id = repository.addUser(testUser)
        val result = repository.getUser(id)

        // Assert
        assertEquals(testUser.copy(id = id), result)
    }

    @Test
    fun `update user should modify existing record`() = runTest {
        // Arrange
        val original = UserEntity(0, "oldName", "user")
        val id = userDao.insertUser(original).toInt()

        // Act
        val updated = User(id, "newName", "user")
        repository.updateUser(id, updated)
        val result = repository.getUser(id)

        // Assert
        assertEquals(updated, result)
    }
}