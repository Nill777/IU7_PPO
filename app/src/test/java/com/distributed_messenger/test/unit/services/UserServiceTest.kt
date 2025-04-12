package com.distributed_messenger.test.unit.services

import com.distributed_messenger.domain.models.User
import com.distributed_messenger.domain.models.UserRole
import com.distributed_messenger.domain.repositories.IUserRepository
import com.distributed_messenger.implementation.services.UserService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserServiceTest {
    private lateinit var userService: UserService
    private val mockUserRepository = mockk<IUserRepository>()

    @BeforeEach
    fun setup() {
        userService = UserService(mockUserRepository)
    }

    @Test
    fun `register should return new user id`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.addUser(any()) } returns userId

        // Act
        val result = userService.register("testUser", UserRole.USER)

        // Assert
        assertEquals(userId, result)
        coVerify { mockUserRepository.addUser(match {
            it.username == "testUser" && it.role == UserRole.USER
        }) }
    }

    @Test
    fun `register with role should use specified role`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.addUser(any()) } returns userId

        // Act
        val result = userService.register("admin", UserRole.ADMINISTRATOR)

        // Assert
        assertEquals(userId, result)
        coVerify { mockUserRepository.addUser(match {
            it.username == "admin" && it.role == UserRole.ADMINISTRATOR
        }) }
    }

    @Test
    fun `getUser should return user when exists`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        val testUser = User(userId, "testUser", UserRole.USER)
        coEvery { mockUserRepository.getUser(userId) } returns testUser

        // Act
        val result = userService.getUser(userId)

        // Assert
        assertEquals(testUser, result)
    }

    @Test
    fun `getAllUsers should return all users`() = runTest {
        // Arrange
        val testUsers = listOf(
            User(UUID.randomUUID(), "user1", UserRole.USER),
            User(UUID.randomUUID(), "user2", UserRole.ADMINISTRATOR)
        )
        coEvery { mockUserRepository.getAllUsers() } returns testUsers

        // Act
        val result = userService.getAllUsers()

        // Assert
        assertEquals(testUsers, result)
    }

    @Test
    fun `updateUser should return true on success`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        val original = User(userId, "oldName", UserRole.USER)
        coEvery { mockUserRepository.getUser(userId) } returns original
        coEvery { mockUserRepository.updateUser(userId, any()) } returns true

        // Act
        val result = userService.updateUser(userId, "newName")

        // Assert
        assertTrue(result)
        coVerify {
            mockUserRepository.updateUser(userId, match { it.username == "newName" })
        }
    }

    @Test
    fun `updateUser should return false when user not found`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.getUser(userId) } returns null

        // Act
        val result = userService.updateUser(userId, "newName")

        // Assert
        assertTrue(!result)
    }

    @Test
    fun `deleteUser should return true when successful`() = runTest {
        // Arrange
        val userId = UUID.randomUUID()
        coEvery { mockUserRepository.deleteUser(userId) } returns true

        // Act
        val result = userService.deleteUser(userId)

        // Assert
        assertTrue(result)
    }
}
